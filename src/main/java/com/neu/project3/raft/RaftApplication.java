package com.neu.project3.raft;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

@SpringBootApplication
@EnableScheduling
public class RaftApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaftApplication.class, args);
    }

    @Bean
    @Qualifier("thread_pool_task_executor")
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(32);
        executor.setThreadNamePrefix("acp_wallet_task");
        executor.initialize();
        return executor;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(1000);
        factory.setConnectTimeout(1000);
        return new RestTemplate(factory);
    }

    @Bean
    public CloseableHttpClient closeableHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setMaxConnTotal(10);
        builder.setMaxConnPerRoute(4);
        return builder.build();
    }

}
