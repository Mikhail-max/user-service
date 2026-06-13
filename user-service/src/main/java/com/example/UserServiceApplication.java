package com.example;

import com.example.ui.ConsoleUI;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(UserServiceApplication.class, args);
        Logger logger = LoggerFactory.getLogger(UserServiceApplication.class);
        logger.info("Запуск Spring Boot приложения user-service...");
        ConsoleUI consoleUI = context.getBean(ConsoleUI.class);
        consoleUI.start();


    }
}


