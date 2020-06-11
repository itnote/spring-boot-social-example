package com.axisj.examples.spring.social;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class ApplicationInitializer {

	public static void main(String[] args) {

//		RedisServer redisServer = new RedisServer ();
//		redisServer.start();


		SpringApplication.run(ApplicationInitializer.class, args);
	}
}
