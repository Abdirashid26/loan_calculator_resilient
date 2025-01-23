package com.faisaldev.loan_calculator;

import com.faisaldev.loan_calculator.models.User;
import com.faisaldev.loan_calculator.repositories.UserRepository;
import lombok.val;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class LoanCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanCalculatorApplication.class, args);
	}


//	@Bean
//	public CommandLineRunner initializeDefaultUser(UserRepository userRepository) {
//		return args -> {
//			val roles = new ArrayList<String>();
//			roles.add("ADMIN");
//			userRepository.save(new User(
//					"faisaldev26@gmail.com",
//					"faisal",
//					roles
//			)).doOnSuccess(user -> {
//				System.out.println("User saved: " + user);
//			}).subscribe();
//		};
//	}

}
