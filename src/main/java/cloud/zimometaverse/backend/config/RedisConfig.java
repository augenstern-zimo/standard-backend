package cloud.zimometaverse.backend.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 
 * <p>职责：
 * <ul>
 *   <li>配置 RedisTemplate 序列化方式</li>
 *   <li>启用 Spring Cache 支持</li>
 *   <li>支持 Java 对象与 JSON 互转</li>
 * </ul>
 * 
 * <p>序列化策略：
 * <ul>
 *   <li>Key：StringRedisSerializer（字符串）</li>
 *   <li>Value：Jackson2JsonRedisSerializer（JSON）</li>
 *   <li>HashKey：StringRedisSerializer</li>
 *   <li>HashValue：Jackson2JsonRedisSerializer</li>
 * </ul>
 * 
 * @author Smart Learning Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 配置 RedisTemplate
     * 
     * <p>使用 Jackson2JsonRedisSerializer 进行序列化，支持：
     * <ul>
     *   <li>Java 8 时间类型（LocalDateTime 等）</li>
     *   <li>自动类型推断</li>
     *   <li>可读的 JSON 格式</li>
     * </ul>
     * 
     * @param connectionFactory Redis 连接工厂
     * @return 配置好的 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("初始化 RedisTemplate");
        
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 创建 JSON 序列化器
        Jackson2JsonRedisSerializer<Object> serializer = createJsonSerializer();

        // 创建字符串序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // Key 采用 String 序列化
        template.setKeySerializer(stringSerializer);
        // Value 采用 JSON 序列化
        template.setValueSerializer(serializer);
        
        // Hash Key 采用 String 序列化
        template.setHashKeySerializer(stringSerializer);
        // Hash Value 采用 JSON 序列化
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        
        log.info("RedisTemplate 初始化成功");
        return template;
    }

    /**
     * 创建 Jackson JSON 序列化器
     * 
     * @return Jackson2JsonRedisSerializer
     */
    private Jackson2JsonRedisSerializer<Object> createJsonSerializer() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 指定要序列化的域：field、get、set，以及修饰符范围：ANY（包括 private 和 public）
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        // 启用默认类型，存储类型信息，反序列化时使用
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        
        // 注册 Java 8 时间模块，支持 LocalDateTime、LocalDate 等
        objectMapper.registerModule(new JavaTimeModule());
        
        serializer.setObjectMapper(objectMapper);
        
        return serializer;
    }
}

