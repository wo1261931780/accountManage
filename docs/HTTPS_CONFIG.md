# HTTPS 配置指南

## 开发环境配置

### 1. 生成自签名证书

在项目根目录执行以下命令生成自签名证书:

```bash
# 生成 PKCS12 格式的密钥库
keytool -genkeypair -alias accountmanage \
  -keyalg RSA -keysize 2048 \
  -storetype PKCS12 \
  -keystore src/main/resources/keystore.p12 \
  -validity 365 \
  -storepass changeit \
  -dname "CN=localhost, OU=Development, O=AccountManage, L=Shanghai, ST=Shanghai, C=CN"
```

### 2. 修改 application.yaml

在 `application.yaml` 中添加 SSL 配置:

```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-type: PKCS12
    key-store-password: changeit
    key-alias: accountmanage
```

### 3. HTTP 自动重定向到 HTTPS

如果需要同时支持 HTTP 并自动重定向到 HTTPS，可以创建配置类:

```java
@Configuration
public class HttpsRedirectConfig {

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    private Connector httpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}
```

---

## 生产环境配置

### 1. 获取正式 SSL 证书

推荐使用以下方式获取 SSL 证书:

- **Let's Encrypt** (免费): 使用 certbot 工具自动申请
- **云服务商**: 阿里云、腾讯云等提供免费 DV 证书
- **商业证书**: DigiCert, Comodo, GlobalSign 等

### 2. 证书格式转换

如果证书是 PEM 格式(.crt/.pem + .key)，需要转换为 PKCS12:

```bash
# 将 PEM 格式转换为 PKCS12
openssl pkcs12 -export \
  -in certificate.crt \
  -inkey private.key \
  -out keystore.p12 \
  -name accountmanage \
  -CAfile ca_bundle.crt \
  -caname root
```

### 3. 安全建议

1. **密钥库密码**: 使用强密码，通过环境变量注入
2. **证书续期**: 设置自动续期提醒
3. **TLS 版本**: 仅启用 TLS 1.2 和 TLS 1.3
4. **加密套件**: 禁用弱加密算法

```yaml
server:
  ssl:
    enabled: true
    key-store: ${SSL_KEYSTORE_PATH}
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: accountmanage
    # 仅启用 TLS 1.2+
    enabled-protocols: TLSv1.2,TLSv1.3
    # 安全的加密套件
    ciphers: TLS_AES_256_GCM_SHA384,TLS_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
```

### 4. 使用 Nginx 反向代理 (推荐)

生产环境推荐使用 Nginx 处理 SSL:

```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /path/to/fullchain.pem;
    ssl_certificate_key /path/to/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
    ssl_prefer_server_ciphers off;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

---

## 注意事项

1. **开发环境**: 浏览器会提示证书不受信任，可以选择继续访问
2. **自签名证书**: 仅用于开发和测试，不要用于生产环境
3. **密钥保护**: 私钥文件权限应设置为 600，仅 root/应用用户可读
4. **定期轮换**: 建议每年更换一次证书和密钥
