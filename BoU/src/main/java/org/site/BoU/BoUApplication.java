package org.site.BoU;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BoUApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoUApplication.class, args);
	}

}
