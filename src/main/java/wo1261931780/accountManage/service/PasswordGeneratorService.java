package wo1261931780.accountManage.service;

import wo1261931780.accountManage.dto.password.PasswordGenerateDTO;
import wo1261931780.accountManage.dto.password.PasswordGenerateVO;

/**
 * 密码生成服务接口
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface PasswordGeneratorService {

    /**
     * 生成密码
     *
     * @param dto 生成参数
     * @return 生成结果
     */
    PasswordGenerateVO generate(PasswordGenerateDTO dto);

    /**
     * 检查密码强度
     *
     * @param password 密码
     * @return 强度评估
     */
    PasswordGenerateVO.PasswordStrength checkStrength(String password);
}
