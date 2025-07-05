package com.fluxbank.user_service.domain.service;

import com.fluxbank.user_service.domain.model.User;

import java.time.Instant;

public interface TokenService {

    String generateToken(User user);
    String validateToken(String token);
    Instant getExpirationDate();
    Instant getExpirationDate(String token);

}
