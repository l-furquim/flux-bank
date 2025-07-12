package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.application.usecase.CreatePixKeyUsecase;
import com.fluxbank.user_service.domain.enums.KeyType;
import com.fluxbank.user_service.domain.exceptions.DuplicatedKeyException;
import com.fluxbank.user_service.domain.exceptions.InvalidPixKeyCreationException;
import com.fluxbank.user_service.domain.exceptions.UserNotFoundException;
import com.fluxbank.user_service.domain.model.PixKey;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.repository.PixKeyRepository;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.interfaces.dto.CreatePixKeyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CreatePixKeyService implements CreatePixKeyUsecase {

    private final PixKeyRepository repository;
    private final UserRepository userRepository;

    public CreatePixKeyService(PixKeyRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public void create(CreatePixKeyRequest request, String userId) {
        Optional<User> userFounded = userRepository.getUserData(UUID.fromString(userId));

        if (userFounded.isEmpty()) {
            throw new UserNotFoundException();
        }

        KeyType type = request.type();
        String value = request.value();

        if (type == KeyType.CPF) {
            if (value != null) {
                throw new InvalidPixKeyCreationException();
            }
        }

        List<PixKey> userKeys = repository.findByUserId(userFounded.get().getId());

        if (type == KeyType.CPF) {
            boolean alreadyExists = !userKeys.stream().filter(k -> k.getType().equals(KeyType.CPF)).toList().isEmpty();
            if (alreadyExists) {
                throw new DuplicatedKeyException("You already have a pix key with your cpf.");
            }
        }

        if (type == KeyType.TEL) {
            if (value == null || value.length() != 11) {
                throw new InvalidPixKeyCreationException();
            }

            boolean alreadyExists = !userKeys.stream().filter(k -> k.getType().equals(KeyType.TEL) && k.getValue().equals(value)).toList().isEmpty();

            if(alreadyExists){
                throw new DuplicatedKeyException("You already have a telephone key with this value.");
            }

        }

        if (type == KeyType.EMAIL) {
            if (!isValidEmail(value)) {
                throw new InvalidPixKeyCreationException();
            }
            boolean alreadyExists = !userKeys.stream().filter(k -> k.getType().equals(KeyType.EMAIL) && k.getValue().equals(value)).toList().isEmpty();

            if(alreadyExists){
                throw new DuplicatedKeyException("You already have a email key with this value.");
            }

        }

        PixKey key = PixKey.builder()
                .value(type == KeyType.CPF ? userFounded.get().getCpf() : value)
                .type(type)
                .ownerId(userFounded.get().getId())
                .build();

        log.info("Key criada: {}", key);

        repository.createPixKey(key);
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email != null && email.matches(regex);
    }
}
