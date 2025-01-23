package com.faisaldev.loan_calculator;

import com.faisaldev.loan_calculator.models.*;
import com.faisaldev.loan_calculator.services.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoanServiceTest {

    @Autowired
    private LoanService loanService;

    private static final double DELTA = 0.02;




    @Test
    void testInvalidLoanAmount() {
        LoanRequest request = new LoanRequest(
                -1000,
                10,
                12,
                LoanPeriodUnit.MONTHS,
                RepaymentFrequency.MONTHLY,
                InterestType.FLAT
        );

        Mono<LoanResponse> responseMono = loanService.calculateLoanSchedule(request);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Loan amount must be positive"))
                .verify();
    }

    @Test
    void testInvalidInterestRate() {
        LoanRequest request = new LoanRequest(
                1000,
                -10,
                12,
                LoanPeriodUnit.MONTHS,
                RepaymentFrequency.MONTHLY,
                InterestType.FLAT
        );

        Mono<LoanResponse> responseMono = loanService.calculateLoanSchedule(request);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Interest rate cannot be negative"))
                .verify();
    }

    @Test
    void testInvalidLoanPeriod() {
        LoanRequest request = new LoanRequest(
                1000,
                10,
                -12,
                LoanPeriodUnit.MONTHS,
                RepaymentFrequency.MONTHLY,
                InterestType.FLAT
        );

        Mono<LoanResponse> responseMono = loanService.calculateLoanSchedule(request);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Loan period must be positive"))
                .verify();
    }


}
