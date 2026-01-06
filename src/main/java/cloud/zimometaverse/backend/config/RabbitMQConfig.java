package cloud.zimometaverse.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * 
 * <p>职责：
 * <ul>
 *   <li>配置消息转换器（JSON）</li>
 *   <li>配置消息发送模板</li>
 *   <li>配置消息监听器工厂</li>
 *   <li>定义交换机、队列、绑定关系</li>
 * </ul>
 * 
 * <p>消息确认机制：
 * <ul>
 *   <li>发布者确认：correlated</li>
 *   <li>发布者返回：enabled</li>
 *   <li>消费者确认：manual（手动确认）</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 定义业务交换机和队列
 * public static final String USER_EXCHANGE = "user.topic";
 * public static final String USER_REGISTER_QUEUE = "user.register.queue";
 * public static final String USER_REGISTER_ROUTING_KEY = "user.register";
 * 
 * @Bean
 * public TopicExchange userExchange() {
 *     return new TopicExchange(USER_EXCHANGE, true, false);
 * }
 * 
 * @Bean
 * public Queue userRegisterQueue() {
 *     return QueueBuilder.durable(USER_REGISTER_QUEUE)
 *             .deadLetterExchange(DEAD_LETTER_EXCHANGE)
 *             .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
 *             .build();
 * }
 * 
 * @Bean
 * public Binding userRegisterBinding() {
 *     return BindingBuilder.bind(userRegisterQueue())
 *             .to(userExchange())
 *             .with(USER_REGISTER_ROUTING_KEY);
 * }
 * }</pre>
 * 
 * @author Smart Learning Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    /**
     * 死信交换机名称
     */
    public static final String DEAD_LETTER_EXCHANGE = "dlx.topic";

    /**
     * 死信队列名称
     */
    public static final String DEAD_LETTER_QUEUE = "dlx.queue";

    /**
     * 死信路由键
     */
    public static final String DEAD_LETTER_ROUTING_KEY = "dlx.#";

    /**
     * 消息转换器 - 使用 JSON 格式
     * 
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public MessageConverter messageConverter() {
        log.info("配置 RabbitMQ 消息转换器：Jackson2JsonMessageConverter");
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置 RabbitTemplate
     * 
     * <p>功能：
     * <ul>
     *   <li>设置消息转换器</li>
     *   <li>启用强制消息返回（mandatory）</li>
     *   <li>配置发布确认回调</li>
     *   <li>配置消息返回回调</li>
     * </ul>
     * 
     * @param connectionFactory 连接工厂
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        log.info("配置 RabbitTemplate");
        
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        
        // 设置消息转换器
        template.setMessageConverter(messageConverter());
        
        // 设置强制标志：消息路由失败时返回给生产者
        template.setMandatory(true);
        
        // 配置发布确认回调：消息是否成功到达交换机
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.debug("消息发送成功：correlationData={}", correlationData);
            } else {
                log.error("消息发送失败：correlationData={}, cause={}", correlationData, cause);
            }
        });
        
        // 配置消息返回回调：消息无法路由到队列时触发
        template.setReturnsCallback(returned -> {
            log.error("消息路由失败，消息被退回：" +
                      "message={}, replyCode={}, replyText={}, exchange={}, routingKey={}",
                    returned.getMessage(),
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey()
            );
        });
        
        log.info("RabbitTemplate 配置完成");
        return template;
    }

    /**
     * 配置消息监听器容器工厂
     * 
     * @param connectionFactory 连接工厂
     * @return SimpleRabbitListenerContainerFactory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        log.info("配置 RabbitListenerContainerFactory");
        
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        
        // 手动确认模式（已在 application.yml 中配置，此处可省略）
        // factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        
        return factory;
    }

    /**
     * 创建死信交换机
     * 
     * @return TopicExchange
     */
    @Bean
    public TopicExchange deadLetterExchange() {
        log.info("创建死信交换机：{}", DEAD_LETTER_EXCHANGE);
        return new TopicExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    /**
     * 创建死信队列
     * 
     * @return Queue
     */
    @Bean
    public Queue deadLetterQueue() {
        log.info("创建死信队列：{}", DEAD_LETTER_QUEUE);
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    /**
     * 绑定死信队列到死信交换机
     * 
     * @return Binding
     */
    @Bean
    public Binding deadLetterBinding() {
        log.info("绑定死信队列到死信交换机：queue={}, exchange={}, routingKey={}",
                DEAD_LETTER_QUEUE, DEAD_LETTER_EXCHANGE, DEAD_LETTER_ROUTING_KEY);
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }
}

