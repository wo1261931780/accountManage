package wo1261931780.accountManage.security;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录用户信息
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Accessors(chain = true)
public class LoginUser {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 访问令牌
     */
    private String token;
}
