package com.fluxbank.user_service.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fluxbank.user_service.domain.exceptions.InvalidTokenException;
import com.fluxbank.user_service.domain.exceptions.TokenGenerationException;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JwtTokenServiceImpl implements TokenService {

    @Value("${security.jwt.secret}")
    private String TOKEN_SECRET;


    @Override
    public String generateToken(User user) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            String token = JWT.create()
                    .withIssuer("fluxbank")
                    .withSubject(user.getEmail())
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new TokenGenerationException("Error while generating token " + exception.getMessage());
        }
    }

    @Override
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            return JWT.require(algorithm)
                    .withIssuer("fluxbank")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            throw new InvalidTokenException("Received a invalid jwt token " + exception.getMessage());
        }
    }

    @Override
    public Instant getExpirationDate() {
        return LocalDateTime.now().
                plusHours(2).
                toInstant(ZoneOffset.of("-03:00"));
    }

    @Override
    public Instant getExpirationDate(String token) {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);

        return JWT.require(algorithm)
                .withIssuer("fluxbank")
                .build()
                .verify(token)
                .getExpiresAtAsInstant();
    }
}
