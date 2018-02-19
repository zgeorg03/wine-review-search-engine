package com.zgeorg03;

import com.zgeorg03.core.CollectionsManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
public class App {

    @Autowired
    private CollectionsManager collectionsManager;

    public static void main(String args[]){
        SpringApplication.run(App.class,args);
    }

    @Bean
    public CommandLineRunner schedulingRunner(TaskExecutor executor) {
        return args -> executor.execute(collectionsManager);
    }
    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
