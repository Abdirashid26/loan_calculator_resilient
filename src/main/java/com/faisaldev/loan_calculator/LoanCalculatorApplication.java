package com.faisaldev.loan_calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoanCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanCalculatorApplication.class, args);
	}


//	@Bean
//	public CommandLineRunner initializeDefaultUser( CustomUserDetailsService customUserDetailsService) {
//		return args -> {
//			val roles = new ArrayList<String>();
//			roles.add("ADMIN");
//			customUserDetailsService.createUser(
//					"faisaldev26@gmail.com",
//					"faisal",
//					roles
//			).subscribe();
//		};
//	}

}
