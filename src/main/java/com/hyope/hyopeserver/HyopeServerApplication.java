package com.hyope.hyopeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class HyopeServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(HyopeServerApplication.class, args);
	}
}