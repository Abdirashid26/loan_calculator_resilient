package com.faisaldev.loan_calculator.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        return Mono.just(authToken)
                .flatMap(token -> {
                    if (!jwtUtil.validateToken(token)) {
                        return Mono.empty();
                    }

                    String username = jwtUtil.getUsernameFromToken(token);
                    return Mono.just(new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            new ArrayList<>()
                    ));
                })
                .cast(Authentication.class);
    }
}