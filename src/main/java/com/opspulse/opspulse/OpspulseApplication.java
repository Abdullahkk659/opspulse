package com.opspulse.opspulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OpspulseApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpspulseApplication.class, args);
	}

}
