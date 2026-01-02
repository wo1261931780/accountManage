package wo1261931780.accountManage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.common.result.ResultCode;
import wo1261931780.accountManage.common.utils.IdGenerator;
import wo1261931780.accountManage.common.utils.PasswordUtils;
import wo1261931780.accountManage.dto.request.AccountCreateDTO;
import wo1261931780.accountManage.dto.request.AccountQueryDTO;
import wo1261931780.accountManage.dto.request.AccountUpdateDTO;
import wo1261931780.accountManage.dto.response.AccountVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.mapper.AccountMapper;
import wo1261931780.accountManage.mapper.PlatformMapper;
import wo1261931780.accountManage.service.AccountService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 账号 Service 实现类
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>
        implements AccountService {

    private final IdGenerator idGenerator;
    private final PasswordUtils passwordUtils;
    private final PlatformMapper platformMapper;

    @Override
    public PageResult<AccountVO> page(AccountQueryDTO query) {
        Page<Account> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<Account> result = baseMapper.selectAccountPage(page, query);

        List<AccountVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result, voList);
    }

    @Override
    public AccountVO getById(Long id) {
        Account account = baseMapper.selectAccountById(id);
        if (account == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "账号不存在");
        }
        return convertToVOWithPassword(account);
    }

    @Override
    public List<AccountVO> listByPlatformId(Long platformId) {
        List<Account> accounts = baseMapper.selectByPlatformId(platformId);
        return accounts.stream()
                .map(this::convertToVOWithPassword)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword(Long id) {
        Account account = super.getById(id);
        if (account == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "账号不存在");
        }
        return passwordUtils.decrypt(account.getPasswordEncrypted(), account.getPasswordSalt());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account create(AccountCreateDTO dto) {
        // 检查平台是否存在
        Platform platform = platformMapper.selectById(dto.getPlatformId());
        if (platform == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "平台不存在");
        }

        // 创建实体
        Account account = new Account();
        BeanUtil.copyProperties(dto, account);
        account.setId(idGenerator.nextId());

        // 加密密码
        String salt = passwordUtils.generateSalt();
        String encryptedPassword = passwordUtils.encrypt(dto.getPassword(), salt);
        account.setPasswordSalt(salt);
        account.setPasswordEncrypted(encryptedPassword);

        // 保存
        save(account);
        log.info("创建账号成功: id={}, accountName={}", account.getId(), account.getAccountName());
        return account;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(AccountUpdateDTO dto) {
        // 检查是否存在
        Account existing = super.getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "账号不存在");
        }

        // 检查平台是否存在
        if (dto.getPlatformId() != null) {
            Platform platform = platformMapper.selectById(dto.getPlatformId());
            if (platform == null) {
                throw new BusinessException(ResultCode.DATA_NOT_EXIST, "平台不存在");
            }
        }

        // 更新实体
        Account account = new Account();
        BeanUtil.copyProperties(dto, account, "password");

        // 如果密码不为空，重新加密
        if (StrUtil.isNotBlank(dto.getPassword())) {
            String salt = passwordUtils.generateSalt();
            String encryptedPassword = passwordUtils.encrypt(dto.getPassword(), salt);
            account.setPasswordSalt(salt);
            account.setPasswordEncrypted(encryptedPassword);
        }

        boolean result = updateById(account);
        log.info("更新账号成功: id={}", dto.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 检查是否存在
        Account existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "账号不存在");
        }

        // 逻辑删除
        boolean result = removeById(id);
        log.info("删除账号成功: id={}", id);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        boolean result = removeByIds(ids);
        log.info("批量删除账号成功: ids={}", ids);
        return result;
    }

    /**
     * 转换为 VO（不含密码）
     */
    private AccountVO convertToVO(Account account) {
        AccountVO vo = new AccountVO();
        BeanUtil.copyProperties(account, vo);
        vo.setPlatformName(account.getPlatformName());
        vo.setPlatformTypeName(account.getPlatformTypeName());
        // 列表查询不返回密码
        vo.setPassword(null);
        return vo;
    }

    /**
     * 转换为 VO（含解密密码）
     */
    private AccountVO convertToVOWithPassword(Account account) {
        AccountVO vo = new AccountVO();
        BeanUtil.copyProperties(account, vo);
        vo.setPlatformName(account.getPlatformName());
        vo.setPlatformTypeName(account.getPlatformTypeName());

        // 获取平台URL
        if (account.getPlatformId() != null) {
            Platform platform = platformMapper.selectById(account.getPlatformId());
            if (platform != null) {
                vo.setPlatformUrl(platform.getPlatformUrl());
                vo.setPlatformTypeId(platform.getPlatformTypeId());
            }
        }

        // 解密密码
        try {
            String plainPassword = passwordUtils.decrypt(account.getPasswordEncrypted(), account.getPasswordSalt());
            vo.setPassword(plainPassword);
        } catch (Exception e) {
            log.error("解密密码失败: accountId={}", account.getId(), e);
            vo.setPassword("[解密失败]");
        }
        return vo;
    }
}
