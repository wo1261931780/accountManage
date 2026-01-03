package wo1261931780.accountManage.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * XSS 过滤器测试
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@DisplayName("XSS 过滤器测试")
class XssHttpServletRequestWrapperTest {

    @Test
    @DisplayName("过滤 script 标签")
    void testFilterScriptTag() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("name", "<script>alert('xss')</script>");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        String result = wrapper.getParameter("name");

        assertFalse(result.contains("<script>"));
        assertFalse(result.contains("</script>"));
        assertFalse(result.contains("alert"));
    }

    @Test
    @DisplayName("过滤 javascript: 协议")
    void testFilterJavascriptProtocol() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("url", "javascript:alert('xss')");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        String result = wrapper.getParameter("url");

        assertFalse(result.toLowerCase().contains("javascript:"));
    }

    @Test
    @DisplayName("过滤事件处理器")
    void testFilterEventHandler() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("img", "<img src='x' onerror='alert(1)'>");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        String result = wrapper.getParameter("img");

        assertFalse(result.contains("onerror="));
    }

    @Test
    @DisplayName("过滤 iframe 标签")
    void testFilterIframe() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("content", "<iframe src='evil.com'></iframe>");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        String result = wrapper.getParameter("content");

        assertFalse(result.contains("<iframe"));
    }

    @Test
    @DisplayName("过滤 eval 表达式")
    void testFilterEval() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("code", "eval(document.cookie)");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        String result = wrapper.getParameter("code");

        assertFalse(result.contains("eval("));
    }

    @Test
    @DisplayName("正常内容不应被过滤")
    void testNormalContent() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String normalContent = "这是正常的账号名称 test123";
        request.setParameter("name", normalContent);

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        String result = wrapper.getParameter("name");

        assertEquals(normalContent, result);
    }

    @Test
    @DisplayName("空值处理")
    void testNullValue() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // 不设置参数

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        String result = wrapper.getParameter("notExist");

        assertNull(result);
    }

    @Test
    @DisplayName("过滤多个参数值")
    void testFilterMultipleValues() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("tags", "<script>bad</script>");
        request.addParameter("tags", "normal");
        request.addParameter("tags", "javascript:void(0)");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        String[] values = wrapper.getParameterValues("tags");

        assertNotNull(values);
        assertEquals(3, values.length);
        assertFalse(values[0].contains("<script>"));
        assertEquals("normal", values[1]);
        assertFalse(values[2].toLowerCase().contains("javascript:"));
    }

    @Test
    @DisplayName("HTML 特殊字符转义")
    void testHtmlEscape() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("content", "<div>test</div>");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        String result = wrapper.getParameter("content");

        // 应该被 HTML 转义
        assertTrue(result.contains("&lt;") || result.contains("&gt;"));
    }
}
