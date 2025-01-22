package com.faisaldev.loan_calculator.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanResponse {
    private double totalLoanAmount;
    private double totalInterestPaid;
    private double totalAmountPaid;
    private List<PaymentDetail> paymentSchedule;
}