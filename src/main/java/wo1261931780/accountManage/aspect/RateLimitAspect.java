package wo1261931780.accountManage.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import wo1261931780.accountManage.annotation.RateLimit;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.ResultCode;
import wo1261931780.accountManage.security.UserContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 接口限流切面
 * 基于内存的简单限流实现（生产环境建议使用Redis）
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    /**
     * 限流记录存储
     * key: 限流key
     * value: 限流窗口记录
     */
    private final Map<String, RateLimitWindow> limitCache = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        String limitKey = buildLimitKey(point, rateLimit);

        // 检查是否超过限流
        if (!tryAcquire(limitKey, rateLimit)) {
            log.warn("接口限流触发, key: {}, limit: {}/{}{}",
                    limitKey, rateLimit.limit(), rateLimit.window(),
                    rateLimit.timeUnit().name().toLowerCase());
            throw new BusinessException(ResultCode.BAD_REQUEST, rateLimit.message());
        }

        return point.proceed();
    }

    /**
     * 构建限流key
     */
    private String buildLimitKey(ProceedingJoinPoint point, RateLimit rateLimit) {
        StringBuilder keyBuilder = new StringBuilder("rate_limit:");

        // 添加自定义前缀
        if (!rateLimit.key().isEmpty()) {
            keyBuilder.append(rateLimit.key()).append(":");
        } else {
            // 使用方法签名作为key
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            keyBuilder.append(method.getDeclaringClass().getSimpleName())
                    .append(".")
                    .append(method.getName())
                    .append(":");
        }

        // 根据限流类型添加标识
        switch (rateLimit.limitType()) {
            case IP -> keyBuilder.append("ip:").append(getClientIp());
            case USER -> {
                Long userId = UserContext.getUserId();
                keyBuilder.append("user:").append(userId != null ? userId : "anonymous");
            }
            case GLOBAL -> keyBuilder.append("global");
        }

        return keyBuilder.toString();
    }

    /**
     * 尝试获取访问许可
     */
    private boolean tryAcquire(String key, RateLimit rateLimit) {
        long windowMillis = rateLimit.timeUnit().toMillis(rateLimit.window());
        long currentTime = System.currentTimeMillis();

        RateLimitWindow window = limitCache.compute(key, (k, existingWindow) -> {
            if (existingWindow == null || existingWindow.isExpired(currentTime)) {
                // 创建新窗口
                return new RateLimitWindow(currentTime, currentTime + windowMillis);
            }
            return existingWindow;
        });

        // 检查是否超过限制
        int currentCount = window.incrementAndGet();
        return currentCount <= rateLimit.limit();
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }

        HttpServletRequest request = attributes.getRequest();
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
     * 限流时间窗口
     */
    private static class RateLimitWindow {
        private final long startTime;
        private final long endTime;
        private final AtomicInteger count;

        public RateLimitWindow(long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.count = new AtomicInteger(0);
        }

        public boolean isExpired(long currentTime) {
            return currentTime > endTime;
        }

        public int incrementAndGet() {
            return count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }
    }

    /**
     * 定期清理过期的限流记录（可以通过定时任务调用）
     */
    public void cleanExpiredRecords() {
        long currentTime = System.currentTimeMillis();
        limitCache.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
        log.debug("清理过期限流记录完成, 当前记录数: {}", limitCache.size());
    }
}
