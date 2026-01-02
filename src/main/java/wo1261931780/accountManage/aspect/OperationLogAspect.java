package wo1261931780.accountManage.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import wo1261931780.accountManage.annotation.OperationLog;
import wo1261931780.accountManage.entity.SysOperationLog;
import wo1261931780.accountManage.security.LoginUser;
import wo1261931780.accountManage.security.UserContext;
import wo1261931780.accountManage.service.OperationLogService;
import wo1261931780.accountManage.util.WebUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    /**
     * 环绕通知
     */
    @Around("@annotation(wo1261931780.accountManage.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog opLog = method.getAnnotation(OperationLog.class);

        // 构建日志对象
        SysOperationLog operationLog = new SysOperationLog();
        operationLog.setModule(opLog.module());
        operationLog.setOperationType(opLog.type().name());
        operationLog.setDescription(opLog.description());

        // 获取请求信息
        HttpServletRequest request = WebUtils.getRequest();
        if (request != null) {
            operationLog.setRequestMethod(request.getMethod());
            operationLog.setRequestUrl(request.getRequestURI());
            operationLog.setOperationIp(WebUtils.getClientIp(request));
        }

        // 获取用户信息
        LoginUser loginUser = UserContext.getUser();
        if (loginUser != null) {
            operationLog.setUserId(loginUser.getUserId());
            operationLog.setUsername(loginUser.getUsername());
        }

        // 保存请求参数
        if (opLog.saveParams()) {
            String params = getParams(joinPoint, signature);
            operationLog.setRequestParams(StrUtil.sub(params, 0, 2000)); // 限制长度
        }

        Object result = null;
        try {
            // 执行方法
            result = joinPoint.proceed();
            operationLog.setStatus(1);

            // 保存响应结果
            if (opLog.saveResult() && result != null) {
                String resultJson = JSONUtil.toJsonStr(result);
                operationLog.setResponseResult(StrUtil.sub(resultJson, 0, 2000)); // 限制长度
            }
        } catch (Throwable e) {
            operationLog.setStatus(0);
            operationLog.setErrorMsg(StrUtil.sub(e.getMessage(), 0, 500));
            throw e;
        } finally {
            // 计算执行时长
            long duration = System.currentTimeMillis() - startTime;
            operationLog.setDuration(duration);

            // 异步保存日志
            operationLogService.saveLog(operationLog);
        }

        return result;
    }

    /**
     * 获取请求参数
     */
    private String getParams(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (paramNames == null || paramNames.length == 0) {
            return "";
        }

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            Object arg = args[i];
            // 过滤不需要记录的参数类型
            if (arg instanceof HttpServletRequest
                || arg instanceof HttpServletResponse
                || arg instanceof MultipartFile) {
                continue;
            }
            // 过滤密码参数
            if ("password".equalsIgnoreCase(paramNames[i])
                || paramNames[i].toLowerCase().contains("password")) {
                params.put(paramNames[i], "******");
            } else {
                params.put(paramNames[i], arg);
            }
        }

        try {
            return JSONUtil.toJsonStr(params);
        } catch (Exception e) {
            return params.toString();
        }
    }
}
