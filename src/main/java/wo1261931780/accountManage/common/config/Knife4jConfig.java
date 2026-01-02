package wo1261931780.accountManage.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API 文档配置
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("账号密码管理系统 API")
                        .description("Account Password Management System API Documentation")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("wo1261931780")
                                .url("https://github.com/wo1261931780/accountManage"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
