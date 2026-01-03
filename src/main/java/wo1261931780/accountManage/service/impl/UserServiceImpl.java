package wo1261931780.accountManage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.dto.user.*;
import wo1261931780.accountManage.entity.SysUser;
import wo1261931780.accountManage.mapper.SysUserMapper;
import wo1261931780.accountManage.service.UserService;

/**
 * 用户管理服务实现类
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    @Override
    public IPage<UserVO> pageUsers(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索 (用户名/昵称/邮箱)
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(SysUser::getUsername, queryDTO.getKeyword())
                    .or()
                    .like(SysUser::getNickname, queryDTO.getKeyword())
                    .or()
                    .like(SysUser::getEmail, queryDTO.getKeyword())
            );
        }

        // 状态过滤
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, queryDTO.getStatus());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> userPage = this.page(page, wrapper);

        // 转换为VO
        return userPage.convert(this::convertToVO);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateDTO createDTO) {
        // 检查用户名是否存在
        if (existsByUsername(createDTO.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否存在
        if (StrUtil.isNotBlank(createDTO.getEmail()) && existsByEmail(createDTO.getEmail(), null)) {
            throw new BusinessException("邮箱已被使用");
        }

        // 检查手机号是否存在
        if (StrUtil.isNotBlank(createDTO.getPhone()) && existsByPhone(createDTO.getPhone(), null)) {
            throw new BusinessException("手机号已被使用");
        }

        SysUser user = new SysUser();
        BeanUtil.copyProperties(createDTO, user);

        // 加密密码
        user.setPassword(BCrypt.hashpw(createDTO.getPassword()));

        // 设置默认昵称
        if (StrUtil.isBlank(user.getNickname())) {
            user.setNickname(createDTO.getUsername());
        }

        this.save(user);
        log.info("创建用户成功: {}", user.getUsername());

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(Long id, UserUpdateDTO updateDTO) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查邮箱是否被其他用户使用
        if (StrUtil.isNotBlank(updateDTO.getEmail()) && existsByEmail(updateDTO.getEmail(), id)) {
            throw new BusinessException("邮箱已被使用");
        }

        // 检查手机号是否被其他用户使用
        if (StrUtil.isNotBlank(updateDTO.getPhone()) && existsByPhone(updateDTO.getPhone(), id)) {
            throw new BusinessException("手机号已被使用");
        }

        // 只更新非空字段
        if (StrUtil.isNotBlank(updateDTO.getNickname())) {
            user.setNickname(updateDTO.getNickname());
        }
        if (StrUtil.isNotBlank(updateDTO.getEmail())) {
            user.setEmail(updateDTO.getEmail());
        }
        if (StrUtil.isNotBlank(updateDTO.getPhone())) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getStatus() != null) {
            user.setStatus(updateDTO.getStatus());
        }

        boolean result = this.updateById(user);
        log.info("更新用户信息: {} - {}", user.getUsername(), result ? "成功" : "失败");

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 防止删除管理员账户
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除管理员账户");
        }

        boolean result = this.removeById(id);
        log.info("删除用户: {} - {}", user.getUsername(), result ? "成功" : "失败");

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long id, Integer status) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 防止禁用管理员账户
        if ("admin".equals(user.getUsername()) && status == 0) {
            throw new BusinessException("不能禁用管理员账户");
        }

        user.setStatus(status);
        boolean result = this.updateById(user);
        log.info("更新用户状态: {} -> {} - {}", user.getUsername(), status == 1 ? "启用" : "禁用", result ? "成功" : "失败");

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id, ResetPasswordDTO resetPasswordDTO) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setPassword(BCrypt.hashpw(resetPasswordDTO.getNewPassword()));
        boolean result = this.updateById(user);
        log.info("重置用户密码: {} - {}", user.getUsername(), result ? "成功" : "失败");

        return result;
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)) > 0;
    }

    @Override
    public boolean existsByEmail(String email, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, email);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }

    @Override
    public boolean existsByPhone(String phone, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, phone);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }

    /**
     * 将实体转换为VO
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }
}
