package com.faisaldev.loan_calculator.services.impl;

import com.faisaldev.loan_calculator.models.*;
import com.faisaldev.loan_calculator.services.LoanService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Service
public class LoanServiceImpl implements LoanService {
    private static final int DAYS_IN_YEAR = 365;
    private static final int DAYS_IN_MONTH = 30;
    private static final int DAYS_IN_WEEK = 7;
    private static final int SCALE = 2;

    @Override
    public Mono<LoanResponse> calculateLoanSchedule(LoanRequest request) {
        return Mono.fromCallable(() -> {
            validateRequest(request);
            validatePeriodFrequencyCompatibility(request);

            int totalPayments = calculateTotalPayments(
                    request.getLoanPeriod(),
                    request.getLoanPeriodUnit(),
                    request.getRepaymentFrequency()
            );

            BigDecimal periodicRate = computePeriodicInterestRate(
                    BigDecimal.valueOf(request.getAnnualInterestRate()),
                    request.getRepaymentFrequency()
            );

            BigDecimal loanAmount = BigDecimal.valueOf(request.getLoanAmount());

            return switch (request.getInterestType()) {
                case FLAT -> calculateFlatInterestSchedule(loanAmount, totalPayments, periodicRate);
                case REDUCING_BALANCE -> calculateReducingBalanceSchedule(loanAmount, totalPayments, periodicRate);
                case COMPOUND -> calculateCompoundInterestSchedule(loanAmount, totalPayments, periodicRate);
            };
        });
    }

    private void validateRequest(LoanRequest request) {
        if (request.getLoanAmount() <= 0) {
            throw new IllegalArgumentException("Loan amount must be positive");
        }
        if (request.getAnnualInterestRate() < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        if (request.getLoanPeriod() <= 0) {
            throw new IllegalArgumentException("Loan period must be positive");
        }
    }

    private void validatePeriodFrequencyCompatibility(LoanRequest request) {
        int totalPayments = calculateTotalPayments(
                request.getLoanPeriod(),
                request.getLoanPeriodUnit(),
                request.getRepaymentFrequency()
        );

        if (totalPayments < 1) {
            throw new IllegalArgumentException(
                    "Repayment frequency " + request.getRepaymentFrequency() +
                            " is too long for loan period of " + request.getLoanPeriod() +
                            " " + request.getLoanPeriodUnit()
            );
        }
    }

    private int calculateTotalPayments(int loanPeriod, LoanPeriodUnit unit, RepaymentFrequency frequency) {
        int periodInMonths = switch (unit) {
            case DAYS -> (int) Math.ceil(loanPeriod / 30.0);
            case WEEKS -> (int) Math.ceil(loanPeriod * 7.0 / 30.0);
            case MONTHS -> loanPeriod;
            case YEARS -> loanPeriod * 12;
        };

        int frequencyInMonths = switch (frequency) {
            case DAILY -> 0;  // Special case, handle separately
            case WEEKLY -> 0;  // Special case, handle separately
            case MONTHLY -> 1;
            case QUARTERLY -> 3;
            case ANNUALLY -> 12;
        };

        if (frequency == RepaymentFrequency.DAILY) {
            return convertLoanPeriodToDays(loanPeriod, unit);
        } else if (frequency == RepaymentFrequency.WEEKLY) {
            return (int) Math.ceil(convertLoanPeriodToDays(loanPeriod, unit) / 7.0);
        }

        return Math.max(1, (int) Math.ceil(periodInMonths / (double) frequencyInMonths));
    }

    private int convertLoanPeriodToDays(int loanPeriod, LoanPeriodUnit unit) {
        return switch (unit) {
            case DAYS -> loanPeriod;
            case WEEKS -> loanPeriod * DAYS_IN_WEEK;
            case MONTHS -> loanPeriod * DAYS_IN_MONTH;
            case YEARS -> loanPeriod * DAYS_IN_YEAR;
        };
    }

    private BigDecimal computePeriodicInterestRate(BigDecimal annualRate, RepaymentFrequency frequency) {
        int periodsPerYear = switch (frequency) {
            case DAILY -> DAYS_IN_YEAR;
            case WEEKLY -> 52;
            case MONTHLY -> 12;
            case QUARTERLY -> 4;
            case ANNUALLY -> 1;
        };

        return annualRate
                .divide(BigDecimal.valueOf(periodsPerYear), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
    }

    private LoanResponse calculateFlatInterestSchedule(BigDecimal loanAmount, int totalPayments, BigDecimal periodicRate) {
        List<PaymentDetail> paymentSchedule = new ArrayList<>();

        BigDecimal totalInterest = loanAmount
                .multiply(periodicRate)
                .multiply(BigDecimal.valueOf(totalPayments))
                .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal totalAmountPayable = loanAmount.add(totalInterest);
        BigDecimal periodicPayment = totalAmountPayable
                .divide(BigDecimal.valueOf(totalPayments), SCALE, RoundingMode.HALF_UP);

        BigDecimal remainingBalance = loanAmount;
        BigDecimal periodicInterest = totalInterest
                .divide(BigDecimal.valueOf(totalPayments), SCALE, RoundingMode.HALF_UP);
        BigDecimal periodicPrincipal = periodicPayment.subtract(periodicInterest);

        for (int i = 1; i <= totalPayments; i++) {
            remainingBalance = remainingBalance.subtract(periodicPrincipal);

            if (i == totalPayments) {
                if (remainingBalance.compareTo(BigDecimal.ZERO) != 0) {
                    periodicPrincipal = periodicPrincipal.add(remainingBalance);
                    periodicPayment = periodicPrincipal.add(periodicInterest);
                    remainingBalance = BigDecimal.ZERO;
                }
            }

            paymentSchedule.add(new PaymentDetail(
                    i,
                    periodicPayment.doubleValue(),
                    periodicPrincipal.doubleValue(),
                    periodicInterest.doubleValue(),
                    remainingBalance.doubleValue()
            ));
        }

        return new LoanResponse(
                loanAmount.doubleValue(),
                totalInterest.doubleValue(),
                totalAmountPayable.doubleValue(),
                paymentSchedule
        );
    }

    private LoanResponse calculateReducingBalanceSchedule(BigDecimal loanAmount, int totalPayments, BigDecimal periodicRate) {
        List<PaymentDetail> paymentSchedule = new ArrayList<>();
        BigDecimal remainingBalance = loanAmount;

        // Calculate EMI using the formula: EMI = P * r * (1 + r)^n / ((1 + r)^n - 1)
        BigDecimal rPlusOne = periodicRate.add(BigDecimal.ONE);
        BigDecimal rPlusOnePowN = rPlusOne.pow(totalPayments);
        BigDecimal denominator = rPlusOnePowN.subtract(BigDecimal.ONE);

        BigDecimal periodicPayment = loanAmount
                .multiply(periodicRate)
                .multiply(rPlusOnePowN)
                .divide(denominator, SCALE, RoundingMode.HALF_UP);

        BigDecimal totalInterestPaid = BigDecimal.ZERO;

        for (int i = 1; i <= totalPayments; i++) {
            BigDecimal interestAmount = remainingBalance
                    .multiply(periodicRate)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            BigDecimal principalAmount = periodicPayment.subtract(interestAmount);

            // Adjust final payment
            if (i == totalPayments) {
                principalAmount = remainingBalance;
                periodicPayment = principalAmount.add(interestAmount);
            }

            remainingBalance = remainingBalance.subtract(principalAmount);
            totalInterestPaid = totalInterestPaid.add(interestAmount);

            paymentSchedule.add(new PaymentDetail(
                    i,
                    periodicPayment.doubleValue(),
                    principalAmount.doubleValue(),
                    interestAmount.doubleValue(),
                    remainingBalance.doubleValue()
            ));
        }

        BigDecimal totalAmountPaid = loanAmount.add(totalInterestPaid);

        return new LoanResponse(
                loanAmount.doubleValue(),
                totalInterestPaid.doubleValue(),
                totalAmountPaid.doubleValue(),
                paymentSchedule
        );
    }

    private LoanResponse calculateCompoundInterestSchedule(BigDecimal loanAmount, int totalPayments, BigDecimal periodicRate) {
        List<PaymentDetail> paymentSchedule = new ArrayList<>();
        BigDecimal remainingBalance = loanAmount;
        BigDecimal totalInterestPaid = BigDecimal.ZERO;

        BigDecimal principalPerPeriod = loanAmount
                .divide(BigDecimal.valueOf(totalPayments), SCALE, RoundingMode.HALF_UP);

        for (int i = 1; i <= totalPayments; i++) {
            BigDecimal interestAmount = remainingBalance
                    .multiply(periodicRate)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            BigDecimal principalAmount = principalPerPeriod;

            // Adjust final payment
            if (i == totalPayments) {
                principalAmount = remainingBalance;
            }

            BigDecimal payment = principalAmount.add(interestAmount);
            remainingBalance = remainingBalance.add(interestAmount).subtract(principalAmount);
            totalInterestPaid = totalInterestPaid.add(interestAmount);

            paymentSchedule.add(new PaymentDetail(
                    i,
                    payment.doubleValue(),
                    principalAmount.doubleValue(),
                    interestAmount.doubleValue(),
                    remainingBalance.doubleValue()
            ));
        }

        BigDecimal totalAmountPaid = loanAmount.add(totalInterestPaid);

        return new LoanResponse(
                loanAmount.doubleValue(),
                totalInterestPaid.doubleValue(),
                totalAmountPaid.doubleValue(),
                paymentSchedule
        );
    }
}
