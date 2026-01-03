package wo1261931780.accountManage.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import wo1261931780.accountManage.service.IpBlacklistService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * IP黑名单过滤器
 * 在请求处理前检查IP是否被封禁
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2) // 在XSS过滤器之后执行
public class IpBlacklistFilter extends OncePerRequestFilter {

    @Autowired
    private IpBlacklistService ipBlacklistService;

    @Value("${security.ip-blacklist.enabled:true}")
    private boolean enabled;

    /**
     * 白名单IP（始终放行）
     */
    private static final Set<String> WHITELIST_IPS = Set.of(
            "127.0.0.1",
            "0:0:0:0:0:0:0:1",
            "::1"
    );

    /**
     * 白名单路径（始终放行）
     */
    private static final Set<String> WHITELIST_PATHS = Set.of(
            "/actuator/health",
            "/doc.html",
            "/swagger-resources",
            "/v3/api-docs",
            "/webjars"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 功能未启用，直接放行
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        String requestUri = request.getRequestURI();

        // 白名单IP直接放行
        if (WHITELIST_IPS.contains(clientIp)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 白名单路径直接放行
        for (String path : WHITELIST_PATHS) {
            if (requestUri.startsWith(path)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 检查IP是否被封禁
        if (ipBlacklistService.isBlocked(clientIp)) {
            log.warn("拦截被封禁IP访问: {} -> {}", clientIp, requestUri);
            sendForbiddenResponse(response, clientIp);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个代理的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 发送禁止访问响应
     */
    private void sendForbiddenResponse(HttpServletResponse response, String ip) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String jsonResponse = String.format(
                "{\"code\":403,\"message\":\"您的IP地址 %s 已被封禁，请联系管理员\",\"data\":null}",
                ip
        );
        response.getWriter().write(jsonResponse);
    }
}
