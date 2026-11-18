package com.zetheta.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class PaymentOrchestrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentOrchestrationApplication.class, args);
	}

}
