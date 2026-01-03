package wo1261931780.accountManage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * API 版本控制配置
 *
 * 当前支持的 API 版本：
 * - v1: /api/v1/* - 当前稳定版本
 *
 * 版本策略：
 * 1. 使用 URL 路径进行版本控制 (/api/v1/, /api/v2/)
 * 2. 主要版本更新时创建新的版本路径
 * 3. 旧版本 API 将保持兼容至少 6 个月
 * 4. 废弃的 API 将通过响应头 X-API-Deprecated 标记
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Configuration
public class ApiVersionConfig {

    @Value("${spring.application.name:AccountManage}")
    private String applicationName;

    @Value("${api.version:1.7.0}")
    private String apiVersion;

    /**
     * 当前 API 版本
     */
    public static final String API_V1 = "/api/v1";

    /**
     * API 版本前缀
     */
    public static final String API_PREFIX = "/api";

    /**
     * OpenAPI 配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("账号密码管理系统 API")
                        .description("""
                                ## API 版本说明

                                当前 API 版本: **v1** (稳定版)

                                ### 版本历史

                                | 版本 | 发布日期 | 状态 | 说明 |
                                |------|---------|------|------|
                                | v1.7 | 2026-01-03 | 当前 | 添加 IP 黑名单、Redis 缓存 |
                                | v1.6 | 2026-01-03 | 稳定 | Docker、CI/CD、XSS 防护 |
                                | v1.5 | 2026-01-02 | 稳定 | 快捷操作、数据可视化、国际化 |
                                | v1.4 | 2026-01-02 | 稳定 | 账号详情、登录安全、单元测试 |

                                ### 认证方式

                                所有需要认证的接口需要在请求头中携带 JWT Token：
                                ```
                                Authorization: Bearer <access_token>
                                ```

                                ### 错误码说明

                                | 状态码 | 说明 |
                                |--------|------|
                                | 200 | 操作成功 |
                                | 400 | 请求参数错误 |
                                | 401 | 未授权或 Token 过期 |
                                | 403 | 禁止访问 |
                                | 404 | 资源不存在 |
                                | 500 | 服务器内部错误 |
                                """)
                        .version(apiVersion)
                        .contact(new Contact()
                                .name("开发团队")
                                .email("admin@example.com")
                                .url("https://github.com/wo1261931780/accountManage"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("开发环境"),
                        new Server()
                                .url("https://api.example.com")
                                .description("生产环境")
                ));
    }
}
