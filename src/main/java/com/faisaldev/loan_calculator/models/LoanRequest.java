package com.faisaldev.loan_calculator.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoanRequest {
    @NotNull(message = "Loan amount is required")
    @Min(value = 1, message = "Loan amount must be greater than 0")
    private double loanAmount;

    @NotNull(message = "Annual interest rate is required")
    @Min(value = 0, message = "Annual interest rate must be greater than or equal to 0")
    private double annualInterestRate;

    @NotNull(message = "Loan period is required")
    @Min(value = 1, message = "Loan period must be greater than 0")
    private int loanPeriod;

    @NotNull(message = "Loan period unit is required")
    private LoanPeriodUnit loanPeriodUnit;

    @NotNull(message = "Repayment frequency is required")
    private RepaymentFrequency repaymentFrequency;

    @NotNull(message = "Interest Type is required")
    private InterestType interestType;

}
