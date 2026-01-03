package wo1261931780.accountManage.security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * XSS 过滤请求包装器
 * 对请求参数和请求体进行 XSS 过滤
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * XSS 危险标签和属性的正则模式
     */
    private static final Pattern[] XSS_PATTERNS = {
            // Script tags
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("src[\r\n]*=[\r\n]*'(.*?)'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\"(.*?)\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Event handlers
            Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // eval expressions
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // expression
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // javascript:
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // vbscript:
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            // iframe
            Pattern.compile("<iframe(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    };

    private byte[] cachedBody;

    public XssHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 缓存请求体，以便多次读取
        InputStream inputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(inputStream);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return cleanXss(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        String[] cleanValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleanValues[i] = cleanXss(values[i]);
        }
        return cleanValues;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> paramMap = super.getParameterMap();
        Map<String, String[]> cleanMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String[] values = entry.getValue();
            String[] cleanValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                cleanValues[i] = cleanXss(values[i]);
            }
            cleanMap.put(entry.getKey(), cleanValues);
        }
        return cleanMap;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return cleanXss(value);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 对 JSON 请求体进行 XSS 过滤
        String body = new String(cachedBody, StandardCharsets.UTF_8);
        String cleanBody = cleanXssForJson(body);
        byte[] cleanBytes = cleanBody.getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cleanBytes);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Not implemented
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 清理 XSS 攻击代码
     *
     * @param value 原始值
     * @return 清理后的值
     */
    private String cleanXss(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        // 使用正则替换危险内容
        String cleanValue = value;
        for (Pattern pattern : XSS_PATTERNS) {
            cleanValue = pattern.matcher(cleanValue).replaceAll("");
        }
        // HTML 转义特殊字符
        return HtmlUtils.htmlEscape(cleanValue);
    }

    /**
     * 对 JSON 请求体进行 XSS 过滤
     * 只过滤危险标签，不进行 HTML 转义（避免破坏 JSON 结构）
     *
     * @param json JSON 字符串
     * @return 过滤后的 JSON
     */
    private String cleanXssForJson(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }
        String cleanJson = json;
        for (Pattern pattern : XSS_PATTERNS) {
            cleanJson = pattern.matcher(cleanJson).replaceAll("");
        }
        return cleanJson;
    }
}
