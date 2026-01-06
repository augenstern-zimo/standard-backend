package cloud.zimometaverse.backend.config;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * MinIO 对象存储配置
 * 
 * <p>职责：
 * <ul>
 *   <li>配置 MinIO 客户端连接参数</li>
 *   <li>创建 MinioClient Bean</li>
 *   <li>支持从配置文件读取参数</li>
 * </ul>
 * 
 * <p>使用方式：
 * <pre>{@code
 * @Autowired
 * private MinioClient minioClient;
 * 
 * @Autowired
 * private MinioConfig minioConfig;
 * }</pre>
 * 
 * @author Smart Learning Team
 * @since 1.0.0
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * MinIO 服务地址
     * 例如：http://localhost:9000 或 https://minio.example.com
     */
    private String endpoint;

    /**
     * MinIO 控制台地址（可选，用于日志输出）
     */
    private String consoleEndpoint;

    /**
     * Access Key（访问密钥）
     */
    private String accessKey;

    /**
     * Secret Key（秘密密钥）
     */
    private String secretKey;

    /**
     * 默认存储桶名称
     */
    private String bucketName;

    /**
     * 是否使用 HTTPS
     * 生产环境建议设置为 true
     */
    private Boolean secure = false;

    /**
     * 连接超时时间（秒）
     */
    private Integer connectTimeout = 10;

    /**
     * 写超时时间（秒）
     */
    private Integer writeTimeout = 60;

    /**
     * 读超时时间（秒）
     */
    private Integer readTimeout = 10;

    /**
     * 创建 MinioClient Bean
     * 
     * @return MinioClient 实例
     */
    @Bean
    public MinioClient minioClient() {
        log.info("初始化 MinIO 客户端，endpoint: {}, bucketName: {}", endpoint, bucketName);
        
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            // 设置超时时间
            client.setTimeout(
                    TimeUnit.SECONDS.toMillis(connectTimeout),
                    TimeUnit.SECONDS.toMillis(writeTimeout),
                    TimeUnit.SECONDS.toMillis(readTimeout)
            );

            log.info("MinIO 客户端初始化成功");
            return client;
            
        } catch (Exception e) {
            log.error("MinIO 客户端初始化失败", e);
            throw new IllegalStateException("无法初始化 MinIO 客户端：" + e.getMessage(), e);
        }
    }
}

