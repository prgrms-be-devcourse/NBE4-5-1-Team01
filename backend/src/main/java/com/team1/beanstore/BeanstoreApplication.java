package com.team1.beanstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BeanstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeanstoreApplication.class, args);
	}

}
