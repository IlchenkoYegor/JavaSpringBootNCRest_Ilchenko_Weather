package ua.edu.sumdu.j2ee.ilchenkoYegor.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ua.edu.sumdu.j2ee.ilchenkoYegor.properties.RestProperties;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfigure {
    Logger log = LogManager.getLogger(AsyncConfigure.class);
    RestProperties restProperties;
    AsyncConfigure(RestProperties restProperties){
        this.restProperties = restProperties;
    }
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor(){
        log.info("AsyncExecutor created");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(restProperties.getAmountOfCores());
        executor.setMaxPoolSize(restProperties.getAmountOfThreads());
        executor.setQueueCapacity(restProperties.getQueueCapacity());
        executor.setThreadNamePrefix("AsynchThread-");
        executor.initialize();
        return executor;
    }
}
