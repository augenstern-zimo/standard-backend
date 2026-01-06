package cloud.zimometaverse.backend.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web 配置类
 * 
 * <p>职责：
 * <ul>
 *   <li>配置跨域（CORS）</li>
 *   <li>配置静态资源映射</li>
 *   <li>配置拦截器</li>
 *   <li>配置参数解析器</li>
 * </ul>
 * 
 * @author Smart Learning Team
 * @since 1.0.0
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "business.cors")
public class WebConfig implements WebMvcConfigurer {

    /**
     * 允许的源（域名）
     */
    private List<String> allowedOrigins;

    /**
     * 允许的 HTTP 方法
     */
    private List<String> allowedMethods;

    /**
     * 允许的请求头
     */
    private List<String> allowedHeaders;

    /**
     * 是否允许携带凭证（Cookie）
     */
    private Boolean allowCredentials;

    /**
     * 预检请求的有效期（秒）
     */
    private Long maxAge;

    /**
     * 配置跨域
     * 
     * <p>允许前端跨域访问后端 API
     * 
     * @param registry CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("配置 CORS 跨域，allowedOrigins={}, allowedMethods={}",
                allowedOrigins, allowedMethods);
        
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins.toArray(new String[0]))
                .allowedMethods(allowedMethods.toArray(new String[0]))
                .allowedHeaders(allowedHeaders.toArray(new String[0]))
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
        
        log.info("CORS 跨域配置完成");
    }

    /* 
     * 如需添加拦截器，取消注释以下代码：
     * 
     * @Override
     * public void addInterceptors(InterceptorRegistry registry) {
     *     registry.addInterceptor(new YourInterceptor())
     *             .addPathPatterns("/api/**")
     *             .excludePathPatterns("/api/auth/login", "/api/auth/register");
     * }
     */

    /* 
     * 如需自定义静态资源映射，取消注释以下代码：
     * 
     * @Override
     * public void addResourceHandlers(ResourceHandlerRegistry registry) {
     *     registry.addResourceHandler("/static/**")
     *             .addResourceLocations("classpath:/static/");
     * }
     */
}

