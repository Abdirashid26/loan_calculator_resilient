package com.faisaldev.loan_calculator.services;

import com.faisaldev.loan_calculator.models.LoanRequest;
import com.faisaldev.loan_calculator.models.LoanResponse;
import reactor.core.publisher.Mono;

public interface LoanService {

    Mono<LoanResponse> calculateLoanSchedule(LoanRequest loanRequest);
}
