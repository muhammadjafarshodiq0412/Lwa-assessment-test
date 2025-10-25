package com.lwa.shop.lwa_eureka_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class LwaEurekaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LwaEurekaServiceApplication.class, args);
	}

}
