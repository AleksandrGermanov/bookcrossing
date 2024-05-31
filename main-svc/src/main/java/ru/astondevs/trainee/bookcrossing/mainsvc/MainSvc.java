package ru.astondevs.trainee.bookcrossing.mainsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class MainSvc {
    public static void main(String[] args) {
        SpringApplication.run(MainSvc.class, args);
    }
}