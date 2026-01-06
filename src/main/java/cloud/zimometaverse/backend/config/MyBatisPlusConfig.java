package cloud.zimometaverse.backend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 
 * <p>职责：
 * <ul>
 *   <li>配置分页插件</li>
 *   <li>配置乐观锁插件</li>
 *   <li>配置防全表更新删除插件</li>
 *   <li>配置 Mapper 扫描路径</li>
 * </ul>
 * 
 * <p>插件说明：
 * <ul>
 *   <li>分页插件：自动处理分页 SQL</li>
 *   <li>乐观锁插件：支持 @Version 注解实现乐观锁</li>
 *   <li>防全表更新删除：防止误操作导致全表数据被删除或更新</li>
 * </ul>
 * 
 * @author Smart Learning Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@MapperScan("cloud.zimometaverse.backend.mapper")
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器配置
     * 
     * <p>插件执行顺序：
     * <ol>
 *   <li>多租户插件</li>
     *   <li>动态表名插件</li>
     *   <li>分页插件</li>
     *   <li>乐观锁插件</li>
     *   <li>SQL 性能规范插件</li>
     *   <li>防全表更新删除插件</li>
     * </ol>
     * 
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("初始化 MyBatis-Plus 拦截器");
        
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置最大单页限制数量（防止恶意查询）
        paginationInterceptor.setMaxLimit(1000L);
        // 溢出总页数后是否进行处理（默认不处理）
        paginationInterceptor.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInterceptor);
        log.info("已添加分页插件，最大单页限制：1000");

        // 2. 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        log.info("已添加乐观锁插件");

        // 3. 防全表更新删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        log.info("已添加防全表更新删除插件");

        log.info("MyBatis-Plus 拦截器初始化完成");
        return interceptor;
    }
}

