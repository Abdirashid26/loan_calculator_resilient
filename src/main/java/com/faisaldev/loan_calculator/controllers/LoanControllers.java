package com.faisaldev.loan_calculator.controllers;


import com.faisaldev.loan_calculator.models.LoanRequest;
import com.faisaldev.loan_calculator.models.UniversalResponse;
import com.faisaldev.loan_calculator.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/loan")
@RequiredArgsConstructor
public class LoanControllers {

    private final LoanService loanService;


    @PostMapping("/calculate-payment-schedule")
    public Mono<ResponseEntity<?>> calculatePaymentSchedule(@RequestBody LoanRequest loanRequest) {
        return loanService.calculateLoanSchedule(loanRequest)
                .map(res -> ResponseEntity.ok().body(UniversalResponse.builder()
                        .status("00")
                        .message("Loan Repayment Schedule")
                        .data(res)
                        .build()));
    }


}
