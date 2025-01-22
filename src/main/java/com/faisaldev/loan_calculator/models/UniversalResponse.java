package com.faisaldev.loan_calculator.models;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UniversalResponse {
    private String status;
    private String message;
    private Object data;
}
