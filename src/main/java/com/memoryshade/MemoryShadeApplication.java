package com.memoryshade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MemoryShadeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoryShadeApplication.class, args);
    }

}
