package wo1261931780.accountManage.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * XSS 过滤器
 * 拦截所有请求，对参数和请求体进行 XSS 过滤
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class XssFilter implements Filter {

    /**
     * 不需要 XSS 过滤的路径
     */
    private static final String[] EXCLUDE_PATHS = {
            "/doc.html",
            "/swagger-resources",
            "/webjars/",
            "/v3/api-docs",
            "/actuator/"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestUri = httpRequest.getRequestURI();

        // 检查是否需要跳过 XSS 过滤
        if (shouldExclude(requestUri)) {
            chain.doFilter(request, response);
            return;
        }

        // 包装请求，进行 XSS 过滤
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(httpRequest);
        chain.doFilter(xssRequest, response);
    }

    /**
     * 检查路径是否需要排除
     */
    private boolean shouldExclude(String requestUri) {
        for (String excludePath : EXCLUDE_PATHS) {
            if (requestUri.startsWith(excludePath) || requestUri.contains(excludePath)) {
                return true;
            }
        }
        return false;
    }
}
