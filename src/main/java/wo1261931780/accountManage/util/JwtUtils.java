package wo1261931780.accountManage.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret:YWNjb3VudE1hbmFnZVNlY3JldEtleUZvckpXVFRva2VuR2VuZXJhdGlvbjIwMjY=}")
    private String secret;

    /**
     * 访问令牌过期时间(毫秒) - 默认2小时
     */
    @Value("${jwt.access-token-expiration:7200000}")
    private Long accessTokenExpiration;

    /**
     * 刷新令牌过期时间(毫秒) - 默认7天
     */
    @Value("${jwt.refresh-token-expiration:604800000}")
    private Long refreshTokenExpiration;

    /**
     * 令牌签发者
     */
    @Value("${jwt.issuer:AccountManage}")
    private String issuer;

    /**
     * 用户ID的claim key
     */
    private static final String CLAIM_USER_ID = "userId";

    /**
     * 用户名的claim key
     */
    private static final String CLAIM_USERNAME = "username";

    /**
     * 令牌类型的claim key
     */
    private static final String CLAIM_TOKEN_TYPE = "tokenType";

    /**
     * 访问令牌类型
     */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /**
     * 刷新令牌类型
     */
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成访问令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 访问令牌
     */
    public String generateAccessToken(Long userId, String username) {
        return generateToken(userId, username, TOKEN_TYPE_ACCESS, accessTokenExpiration);
    }

    /**
     * 生成刷新令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        return generateToken(userId, username, TOKEN_TYPE_REFRESH, refreshTokenExpiration);
    }

    /**
     * 生成令牌
     *
     * @param userId     用户ID
     * @param username   用户名
     * @param tokenType  令牌类型
     * @param expiration 过期时间
     * @return 令牌
     */
    private String generateToken(Long userId, String username, String tokenType, Long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_USERNAME, username);
        claims.put(CLAIM_TOKEN_TYPE, tokenType);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析令牌
     *
     * @param token 令牌
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("JWT令牌解析失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 从令牌获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get(CLAIM_USER_ID, Long.class);
    }

    /**
     * 从令牌获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get(CLAIM_USERNAME, String.class);
    }

    /**
     * 获取令牌类型
     *
     * @param token 令牌
     * @return 令牌类型
     */
    public String getTokenType(String token) {
        Claims claims = parseToken(token);
        return claims.get(CLAIM_TOKEN_TYPE, String.class);
    }

    /**
     * 验证令牌是否有效
     *
     * @param token 令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 验证是否为访问令牌
     *
     * @param token 令牌
     * @return 是否为访问令牌
     */
    public boolean isAccessToken(String token) {
        return TOKEN_TYPE_ACCESS.equals(getTokenType(token));
    }

    /**
     * 验证是否为刷新令牌
     *
     * @param token 令牌
     * @return 是否为刷新令牌
     */
    public boolean isRefreshToken(String token) {
        return TOKEN_TYPE_REFRESH.equals(getTokenType(token));
    }

    /**
     * 获取令牌过期时间
     *
     * @param token 令牌
     * @return 过期时间
     */
    public Date getExpiration(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 获取访问令牌过期时间(秒)
     *
     * @return 过期时间(秒)
     */
    public Long getAccessTokenExpirationSeconds() {
        return accessTokenExpiration / 1000;
    }
}
