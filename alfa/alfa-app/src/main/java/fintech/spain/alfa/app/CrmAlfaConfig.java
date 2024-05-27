package fintech.spain.alfa.app;

import com.google.common.cache.CacheBuilder;
import fintech.bo.api.server.PermissionsMaintainer;
import fintech.dc.impl.DcSettingsServiceBean;
import fintech.security.user.UserService;
import fintech.spain.alfa.app.audit.LoggingAndAuditMdcFilter;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.servlet.Filter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@EnableCaching
public class CrmAlfaConfig implements SchedulingConfigurer, AsyncConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }


    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

    @DependsOn("crmAlfaSetup")
    @Bean
    public PermissionsMaintainer permissionsMaintainer(UserService userService) {
        PermissionsMaintainer permissionsMaintainer = new PermissionsMaintainer(userService);
        permissionsMaintainer.initPermissions();
        return permissionsMaintainer;
    }

    @Bean
    public CacheManager cacheManager() {
        GuavaCacheManager cacheManager = new GuavaCacheManager(DcSettingsServiceBean.DC_SETTINGS);
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(30, TimeUnit.SECONDS);
        cacheManager.setCacheBuilder(cacheBuilder);
        return cacheManager;
    }

    @Bean
    public Filter loggingMdcFilter() {
        return new LoggingAndAuditMdcFilter();
    }
}
