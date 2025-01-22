package com.faisaldev.loan_calculator.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("user_entity")
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
    private List<String> roles;

}