package wo1261931780.accountManage.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import wo1261931780.accountManage.common.result.ResultCode;
import wo1261931780.accountManage.util.JwtUtils;
import wo1261931780.accountManage.util.WebUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 白名单路径 - 不需要认证
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/doc.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/favicon.ico",
            "/error"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();

        // 检查是否为白名单路径
        if (isWhiteListed(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取令牌
        String token = WebUtils.getToken(request);

        if (token == null) {
            sendUnauthorized(response, "请先登录");
            return;
        }

        try {
            // 验证令牌
            if (!jwtUtils.validateToken(token)) {
                sendUnauthorized(response, "令牌无效");
                return;
            }

            // 验证是否为访问令牌
            if (!jwtUtils.isAccessToken(token)) {
                sendUnauthorized(response, "请使用访问令牌");
                return;
            }

            // 获取用户信息并设置上下文
            Long userId = jwtUtils.getUserId(token);
            String username = jwtUtils.getUsername(token);

            LoginUser loginUser = new LoginUser()
                    .setUserId(userId)
                    .setUsername(username)
                    .setToken(token);

            UserContext.setUser(loginUser);

            // 继续过滤链
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendUnauthorized(response, "令牌已过期，请重新登录");
        } catch (JwtException e) {
            sendUnauthorized(response, "令牌无效");
        } finally {
            // 清理上下文
            UserContext.clear();
        }
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhiteListed(String requestPath) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    /**
     * 返回未授权响应
     */
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"code\":401,\"message\":\"%s\",\"data\":null}", message));
    }
}
