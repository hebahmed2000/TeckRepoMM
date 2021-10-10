package com.evision.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FileAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileAppApplication.class, args);
	}

}
