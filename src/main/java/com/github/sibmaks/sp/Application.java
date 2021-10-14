package com.github.sibmaks.sp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application entry point
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    /**
     * Application entry point
     * @param args cmd arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
