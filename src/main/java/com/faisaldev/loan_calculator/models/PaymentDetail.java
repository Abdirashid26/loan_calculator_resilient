package com.faisaldev.loan_calculator.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetail {
    private int paymentNumber;
    private double paymentAmount;
    private double principalAmount;
    private double interestAmount;
    private double remainingBalance;
}