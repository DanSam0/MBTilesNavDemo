package com.dansam0.mbtilesnavdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class MbtilesnavdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MbtilesnavdemoApplication.class, args);
	}

}
