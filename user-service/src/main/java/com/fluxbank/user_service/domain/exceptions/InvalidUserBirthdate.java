package com.fluxbank.user_service.domain.exceptions;

public class InvalidUserBirthdate extends RuntimeException {
    public InvalidUserBirthdate() {
        super("We only support cpf's for people above 16 years old.");
    }
}
