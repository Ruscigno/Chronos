package br.com.ruscigno.chronos.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ChronosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChronosApplication.class, args);
	}

}
