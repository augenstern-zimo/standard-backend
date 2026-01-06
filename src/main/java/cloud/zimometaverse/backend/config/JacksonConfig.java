package cloud.zimometaverse.backend.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 序列化配置
 * 
 * <p>职责：
 * <ul>
 *   <li>配置 Java 8 时间类型的序列化格式</li>
 *   <li>统一日期时间格式</li>
 *   <li>支持前后端时间格式一致性</li>
 * </ul>
 * 
 * <p>格式规范：
 * <ul>
 *   <li>LocalDateTime：yyyy-MM-dd HH:mm:ss</li>
 *   <li>LocalDate：yyyy-MM-dd</li>
 *   <li>LocalTime：HH:mm:ss</li>
 * </ul>
 * 
 * @author Smart Learning Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class JacksonConfig {

    /**
     * 日期时间格式
     */
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 时间格式
     */
    private static final String TIME_PATTERN = "HH:mm:ss";

    /**
     * 自定义 Jackson ObjectMapper
     * 
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        log.info("配置 Jackson 序列化格式：" +
                "LocalDateTime={}, LocalDate={}, LocalTime={}",
                DATE_TIME_PATTERN, DATE_PATTERN, TIME_PATTERN);
        
        return builder -> {
            // LocalDateTime 序列化
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(
                    DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(
                    DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));

            // LocalDate 序列化
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(
                    DateTimeFormatter.ofPattern(DATE_PATTERN)));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(
                    DateTimeFormatter.ofPattern(DATE_PATTERN)));

            // LocalTime 序列化
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(
                    DateTimeFormatter.ofPattern(TIME_PATTERN)));
            builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(
                    DateTimeFormatter.ofPattern(TIME_PATTERN)));
        };
    }
}

