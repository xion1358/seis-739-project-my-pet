package com.mypetserver.mypetserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.mypetserver.mypetserver.repository")
@EntityScan("com.mypetserver.mypetserver.dto")
public class MypetserverApplication {

	private static final Logger logger = LoggerFactory.getLogger(MypetserverApplication.class);

	// Program entry point
	public static void main(String[] args) {
		SpringApplication.run(MypetserverApplication.class, args);
		logger.info("MypetserverApplication started");
	}
}
