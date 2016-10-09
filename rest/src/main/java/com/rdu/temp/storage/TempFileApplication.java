package com.rdu.temp.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author rdu
 * @since 05.10.2016
 */
@SpringBootApplication
@EnableScheduling
public class TempFileApplication {
    public static void main(String[] args) {
        SpringApplication.run(TempFileApplication.class, args);
    }
}
