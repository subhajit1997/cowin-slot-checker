package com.cowin.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 ***
 ****created by: Subhajit Biswas
 *** 
 */

@SpringBootApplication
@EnableScheduling
public class CowinSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CowinSchedulerApplication.class, args);
	}

}
