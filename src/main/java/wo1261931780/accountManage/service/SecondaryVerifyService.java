package wo1261931780.accountManage.service;

import wo1261931780.accountManage.dto.auth.SecondaryVerifyDTO;
import wo1261931780.accountManage.dto.response.PasswordViewVO;

/**
 * 二次验证服务接口
 */
public interface SecondaryVerifyService {

    /**
     * 验证用户密码
     * @param password 用户输入的密码
     * @return 是否验证通过
     */
    boolean verifyUserPassword(String password);

    /**
     * 二次验证后查看密码
     * @param dto 二次验证请求
     * @return 密码信息（包含明文密码）
     */
    PasswordViewVO viewPasswordWithVerification(SecondaryVerifyDTO dto);
}
