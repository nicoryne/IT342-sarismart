package me.sarismart.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import me.sarismart.backend.Config.AppConfig;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class SarismartBackendApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SarismartBackendApiApplication.class, args);
	}

}
