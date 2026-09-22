package com.example.MedVita.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example"})
public class TelaLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelaLoginApplication.class, args);
	}

}
