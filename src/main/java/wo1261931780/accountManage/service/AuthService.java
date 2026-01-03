package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import wo1261931780.accountManage.dto.auth.ChangePasswordDTO;
import wo1261931780.accountManage.dto.auth.LoginDTO;
import wo1261931780.accountManage.dto.auth.LoginVO;
import wo1261931780.accountManage.dto.auth.UpdateProfileDTO;
import wo1261931780.accountManage.dto.auth.UserInfoVO;
import wo1261931780.accountManage.entity.SysUser;

/**
 * 认证服务接口
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface AuthService extends IService<SysUser> {

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @param ip       客户端IP
     * @return 登录结果
     */
    LoginVO login(LoginDTO loginDTO, String ip);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的登录结果
     */
    LoginVO refreshToken(String refreshToken);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserInfoVO getCurrentUser();

    /**
     * 修改密码
     *
     * @param changePasswordDTO 修改密码信息
     */
    void changePassword(ChangePasswordDTO changePasswordDTO);

    /**
     * 更新用户资料
     *
     * @param updateProfileDTO 更新资料信息
     */
    void updateProfile(UpdateProfileDTO updateProfileDTO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    SysUser getByUsername(String username);

    /**
     * 解锁用户账号
     *
     * @param userId 用户ID
     */
    void unlockUser(Long userId);

    /**
     * 创建默认管理员账号(如果不存在)
     */
    void initDefaultAdmin();
}
