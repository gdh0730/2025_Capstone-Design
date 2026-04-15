package com.poseman.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.poseman.backend.config.AppProperties;
import com.poseman.backend.config.ReportProperties;
import com.poseman.backend.config.ShinobiProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		ShinobiProperties.class,
		ReportProperties.class,
		AppProperties.class
})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
