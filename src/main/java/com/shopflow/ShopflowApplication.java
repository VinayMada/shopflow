package com.shopflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ShopflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopflowApplication.class, args);
	}

}
