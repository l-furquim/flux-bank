package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.domain.enums.KeyType;
import com.fluxbank.user_service.domain.exceptions.DuplicatedKeyException;
import com.fluxbank.user_service.domain.exceptions.InvalidPixKeyCreationException;
import com.fluxbank.user_service.domain.exceptions.UserNotFoundException;
import com.fluxbank.user_service.domain.model.PixKey;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.repository.PixKeyRepository;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.interfaces.dto.CreatePixKeyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePixKeyServiceTest {

    @Mock
    private PixKeyRepository pixKeyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreatePixKeyService createPixKeyService;

    private UUID userId;
    private User user;
    private String userIdString;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userIdString = userId.toString();
        user = User.builder()
                .id(userId)
                .cpf("12345678901")
                .fullName("João Silva")
                .email("joao@email.com")
                .password("password123")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("Rua A, 123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create CPF PIX key successfully")
    void shouldCreateCpfPixKeySuccessfully() {
        // Given
        CreatePixKeyRequest request = new CreatePixKeyRequest( null,KeyType.CPF);

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));
        when(pixKeyRepository.findByUserId(userId)).thenReturn(List.of());

        // When
        createPixKeyService.create(request, userIdString);

        // Then
        verify(pixKeyRepository).createPixKey(argThat(pixKey ->
                pixKey.getType().equals(KeyType.CPF) &&
                        pixKey.getValue().equals(user.getCpf()) &&
                        pixKey.getOwnerId().equals(userId)
        ));
    }

    @Test
    @DisplayName("Should create EMAIL PIX key successfully")
    void shouldCreateEmailPixKeySuccessfully() {
        // Given
        String email = "test@email.com";
        CreatePixKeyRequest request = new CreatePixKeyRequest( email,KeyType.EMAIL);

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));
        when(pixKeyRepository.findByUserId(userId)).thenReturn(List.of());

        // When
        createPixKeyService.create(request, userIdString);

        // Then
        verify(pixKeyRepository).createPixKey(argThat(pixKey ->
                pixKey.getType().equals(KeyType.EMAIL) &&
                        pixKey.getValue().equals(email) &&
                        pixKey.getOwnerId().equals(userId)
        ));
    }

    @Test
    @DisplayName("Should create TELEPHONE PIX key successfully")
    void shouldCreateTelephonePixKeySuccessfully() {
        // Given
        String telephone = "11999999999";
        CreatePixKeyRequest request = new CreatePixKeyRequest( telephone,KeyType.TEL);

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));
        when(pixKeyRepository.findByUserId(userId)).thenReturn(List.of());

        // When
        createPixKeyService.create(request, userIdString);

        // Then
        verify(pixKeyRepository).createPixKey(argThat(pixKey ->
                pixKey.getType().equals(KeyType.TEL) &&
                        pixKey.getValue().equals(telephone) &&
                        pixKey.getOwnerId().equals(userId)
        ));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        // Given
        CreatePixKeyRequest request = new CreatePixKeyRequest( null,KeyType.CPF);

        when(userRepository.getUserData(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () ->
                createPixKeyService.create(request, userIdString)
        );

        verify(pixKeyRepository, never()).createPixKey(any());
    }

    @Test
    @DisplayName("Should throw InvalidPixKeyCreationException when CPF key has non-null value")
    void shouldThrowInvalidPixKeyCreationExceptionWhenCpfHasValue() {
        // Given
        CreatePixKeyRequest request = new CreatePixKeyRequest("some_value",KeyType.CPF );

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(InvalidPixKeyCreationException.class, () ->
                createPixKeyService.create(request, userIdString)
        );

        verify(pixKeyRepository, never()).createPixKey(any());
    }

    @Test
    @DisplayName("Should throw DuplicatedKeyException when CPF key already exists")
    void shouldThrowDuplicatedKeyExceptionWhenCpfKeyAlreadyExists() {
        // Given
        CreatePixKeyRequest request = new CreatePixKeyRequest( null,KeyType.CPF);

        PixKey existingCpfKey = PixKey.builder()
                .type(KeyType.CPF)
                .value(user.getCpf())
                .ownerId(userId)
                .build();

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));
        when(pixKeyRepository.findByUserId(userId)).thenReturn(List.of(existingCpfKey));

        // When & Then
        DuplicatedKeyException exception = assertThrows(DuplicatedKeyException.class, () ->
                createPixKeyService.create(request, userIdString)
        );

        assertEquals("You already have a pix key with your cpf.", exception.getMessage());
        verify(pixKeyRepository, never()).createPixKey(any());
    }

    @Test
    @DisplayName("Should throw InvalidPixKeyCreationException when telephone is null")
    void shouldThrowInvalidPixKeyCreationExceptionWhenTelephoneIsNull() {
        // Given
        CreatePixKeyRequest request = new CreatePixKeyRequest( null,KeyType.TEL);

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(InvalidPixKeyCreationException.class, () ->
                createPixKeyService.create(request, userIdString)
        );

        verify(pixKeyRepository, never()).createPixKey(any());
    }

    @Test
    @DisplayName("Should throw InvalidPixKeyCreationException when telephone does not have 11 digits")
    void shouldThrowInvalidPixKeyCreationExceptionWhenTelephoneHasInvalidLength() {
        // Given
        CreatePixKeyRequest request = new CreatePixKeyRequest( "1199999999",KeyType.TEL); // 10 dígitos

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(InvalidPixKeyCreationException.class, () ->
                createPixKeyService.create(request, userIdString)
        );

        verify(pixKeyRepository, never()).createPixKey(any());
    }

    @Test
    @DisplayName("Should throw DuplicatedKeyException when telephone key already exists")
    void shouldThrowDuplicatedKeyExceptionWhenTelephoneKeyAlreadyExists() {
        // Given
        String telephone = "11999999999";
        CreatePixKeyRequest request = new CreatePixKeyRequest( telephone,KeyType.TEL);

        PixKey existingTelKey = PixKey.builder()
                .type(KeyType.TEL)
                .value(telephone)
                .ownerId(userId)
                .build();

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));
        when(pixKeyRepository.findByUserId(userId)).thenReturn(List.of(existingTelKey));

        // When & Then
        DuplicatedKeyException exception = assertThrows(DuplicatedKeyException.class, () ->
                createPixKeyService.create(request, userIdString)
        );

        assertEquals("You already have a telephone key with this value.", exception.getMessage());
        verify(pixKeyRepository, never()).createPixKey(any());
    }

    @Test
    @DisplayName("Should throw InvalidPixKeyCreationException when email is invalid")
    void shouldThrowInvalidPixKeyCreationExceptionWhenEmailIsInvalid() {
        // Given
        CreatePixKeyRequest request = new CreatePixKeyRequest( "invalid_email",KeyType.EMAIL);

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(InvalidPixKeyCreationException.class, () ->
                createPixKeyService.create(request, userIdString)
        );

        verify(pixKeyRepository, never()).createPixKey(any());
    }

    @Test
    @DisplayName("Should throw InvalidPixKeyCreationException when email is null")
    void shouldThrowInvalidPixKeyCreationExceptionWhenEmailIsNull() {
        // Given
        CreatePixKeyRequest request = new CreatePixKeyRequest( null,KeyType.EMAIL);

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(InvalidPixKeyCreationException.class, () ->
                createPixKeyService.create(request, userIdString)
        );

        verify(pixKeyRepository, never()).createPixKey(any());
    }

    @Test
    @DisplayName("Should throw DuplicatedKeyException when email key already exists")
    void shouldThrowDuplicatedKeyExceptionWhenEmailKeyAlreadyExists() {
        // Given
        String email = "test@email.com";
        CreatePixKeyRequest request = new CreatePixKeyRequest( email,KeyType.EMAIL);

        PixKey existingEmailKey = PixKey.builder()
                .type(KeyType.EMAIL)
                .value(email)
                .ownerId(userId)
                .build();

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));
        when(pixKeyRepository.findByUserId(userId)).thenReturn(List.of(existingEmailKey));

        // When & Then
        DuplicatedKeyException exception = assertThrows(DuplicatedKeyException.class, () ->
                createPixKeyService.create(request, userIdString)
        );

        assertEquals("You already have a email key with this value.", exception.getMessage());
        verify(pixKeyRepository, never()).createPixKey(any());
    }

    @Test
    @DisplayName("Should accept different valid email formats")
    void shouldAcceptDifferentValidEmailFormats() {
        // Given
        String[] validEmails = {
                "test@email.com",
                "user.name@domain.com",
                "user-name@domain.co.uk",
                "user_name@domain.org",
                "123@domain.com"
        };

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));
        when(pixKeyRepository.findByUserId(userId)).thenReturn(List.of());

        // When & Then
        for (String email : validEmails) {
            CreatePixKeyRequest request = new CreatePixKeyRequest( email,KeyType.EMAIL);

            assertDoesNotThrow(() -> createPixKeyService.create(request, userIdString));
        }

        verify(pixKeyRepository, times(validEmails.length)).createPixKey(any());
    }

    @Test
    @DisplayName("Should reject invalid email formats")
    void shouldRejectInvalidEmailFormats() {
        // Given
        String[] invalidEmails = {
                "invalid-email",
                "@domain.com",
                "user@",
                "user@domain",
                "user..name@domain.com",
                "user@domain..com"
        };

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));

        // When & Then
        for (String email : invalidEmails) {
            CreatePixKeyRequest request = new CreatePixKeyRequest( email,KeyType.EMAIL);

            assertThrows(InvalidPixKeyCreationException.class, () ->
                    createPixKeyService.create(request, userIdString)
            );
        }

        verify(pixKeyRepository, never()).createPixKey(any());
    }

    @Test
    @DisplayName("Should allow creating telephone key when CPF key exists")
    void shouldAllowCreateTelephoneKeyWhenCpfKeyExists() {
        // Given
        String telephone = "11999999999";
        CreatePixKeyRequest request = new CreatePixKeyRequest( telephone,KeyType.TEL);

        PixKey existingCpfKey = PixKey.builder()
                .type(KeyType.CPF)
                .value(user.getCpf())
                .ownerId(userId)
                .build();

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));
        when(pixKeyRepository.findByUserId(userId)).thenReturn(List.of(existingCpfKey));

        // When
        assertDoesNotThrow(() -> createPixKeyService.create(request, userIdString));

        // Then
        verify(pixKeyRepository).createPixKey(argThat(pixKey ->
                pixKey.getType().equals(KeyType.TEL) &&
                        pixKey.getValue().equals(telephone) &&
                        pixKey.getOwnerId().equals(userId)
        ));
    }

    @Test
    @DisplayName("Should allow creating email key when telephone key exists")
    void shouldAllowCreateEmailKeyWhenTelephoneKeyExists() {
        // Given
        String email = "test@email.com";
        CreatePixKeyRequest request = new CreatePixKeyRequest( email,KeyType.EMAIL);

        PixKey existingTelKey = PixKey.builder()
                .type(KeyType.TEL)
                .value("11999999999")
                .ownerId(userId)
                .build();

        when(userRepository.getUserData(userId)).thenReturn(Optional.of(user));
        when(pixKeyRepository.findByUserId(userId)).thenReturn(List.of(existingTelKey));

        // When
        assertDoesNotThrow(() -> createPixKeyService.create(request, userIdString));

        // Then
        verify(pixKeyRepository).createPixKey(argThat(pixKey ->
                pixKey.getType().equals(KeyType.EMAIL) &&
                        pixKey.getValue().equals(email) &&
                        pixKey.getOwnerId().equals(userId)
        ));
    }
}