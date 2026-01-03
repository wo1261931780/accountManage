package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import wo1261931780.accountManage.dto.user.*;
import wo1261931780.accountManage.entity.SysUser;

/**
 * 用户管理服务接口
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface UserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 分页用户列表
     */
    IPage<UserVO> pageUsers(UserQueryDTO queryDTO);

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    UserVO getUserById(Long id);

    /**
     * 创建用户
     *
     * @param createDTO 创建用户请求
     * @return 创建的用户ID
     */
    Long createUser(UserCreateDTO createDTO);

    /**
     * 更新用户信息
     *
     * @param id        用户ID
     * @param updateDTO 更新用户请求
     * @return 是否更新成功
     */
    boolean updateUser(Long id, UserUpdateDTO updateDTO);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态: 0-禁用, 1-启用
     * @return 是否更新成功
     */
    boolean updateUserStatus(Long id, Integer status);

    /**
     * 重置用户密码
     *
     * @param id              用户ID
     * @param resetPasswordDTO 重置密码请求
     * @return 是否重置成功
     */
    boolean resetPassword(Long id, ResetPasswordDTO resetPasswordDTO);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @param excludeId 排除的用户ID(用于更新时排除自己)
     * @return 是否存在
     */
    boolean existsByEmail(String email, Long excludeId);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @param excludeId 排除的用户ID(用于更新时排除自己)
     * @return 是否存在
     */
    boolean existsByPhone(String phone, Long excludeId);
}
