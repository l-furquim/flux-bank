package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.domain.exceptions.InvalidUserBirthdate;
import com.fluxbank.user_service.domain.exceptions.UnnauthorizedAccountCreation;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.model.UserDevice;
import com.fluxbank.user_service.domain.repository.UserDeviceRepository;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.interfaces.dto.CreateUserRequest;
import com.fluxbank.user_service.interfaces.dto.UserDeviceDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDeviceRepository userDeviceRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private RegisterUserService registerService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<UserDevice> userDeviceCaptor;

    @Test
    @DisplayName("Should create a user with success")
    void shouldCreateUserWithSuccess() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Lucas Furquim",
                "11111111111",
                "lucas@mail.com",
                "ishow123",
                LocalDate.of(2006, 3, 26), // Usuário com 18 anos
                "Address"
        );

        UserDeviceDto deviceInfo = new UserDeviceDto("Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        String encryptedPassword = "encrypted_password_hash";
        UUID userId = UUID.randomUUID();

        User userPersisted = User.builder()
                .id(userId)
                .cpf(request.cpf())
                .fullName(request.fullName())
                .email(request.email())
                .password(encryptedPassword)
                .birthDate(request.birthDate())
                .address(request.address())
                .build();

        when(userRepository.findByCpf(request.cpf())).thenReturn(Optional.empty());
        when(encoder.encode(request.password())).thenReturn(encryptedPassword);
        when(userRepository.createUser(any(User.class))).thenReturn(userPersisted);

        // Act
        registerService.register(request, deviceInfo);

        // Assert
        // Verifica se o usuário foi criado com os dados corretos
        verify(userRepository).createUser(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals(request.cpf(), capturedUser.getCpf());
        assertEquals(request.fullName(), capturedUser.getFullName());
        assertEquals(request.email(), capturedUser.getEmail());
        assertEquals(encryptedPassword, capturedUser.getPassword());
        assertEquals(request.birthDate(), capturedUser.getBirthDate());
        assertEquals(request.address(), capturedUser.getAddress());


        // Verifica se todos os mocks foram chamados
        verify(userRepository).findByCpf(request.cpf());
        verify(encoder).encode(request.password());
    }

    @Test
    @DisplayName("Should throw exception when user already exists")
    void shouldThrowExceptionWhenUserAlreadyExists() {

        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Lucas Furquim",
                "11111111111",
                "lucas@mail.com",
                "ishow123",
                LocalDate.of(2006, 3, 26),
                "Address"
        );

        UserDeviceDto deviceInfo = new UserDeviceDto("Mozilla/5.0");

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .cpf(request.cpf())
                .build();

        when(userRepository.findByCpf(request.cpf())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        UnnauthorizedAccountCreation exception = assertThrows(
                UnnauthorizedAccountCreation.class,
                () -> registerService.register(request, deviceInfo)
        );

        assertEquals("We only support 1 account per cpf", exception.getMessage());

        // Verifica que outros métodos não foram chamados
        verify(userRepository, never()).createUser(any(User.class));
        verify(userDeviceRepository, never()).createUserDevice(any(UserDevice.class));
        verify(encoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should throw exception when user is under 16 years old")
    void shouldThrowExceptionWhenUserIsUnder16() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Lucas Furquim",
                "11111111111",
                "lucas@mail.com",
                "ishow123",
                LocalDate.now().minusYears(15),
                "Address"
        );

        UserDeviceDto deviceInfo = new UserDeviceDto("Mozilla/5.0");

        when(userRepository.findByCpf(request.cpf())).thenReturn(Optional.empty());

        // Act & Assert
        InvalidUserBirthdate exception = assertThrows(
                InvalidUserBirthdate.class,
                () -> registerService.register(request, deviceInfo)
        );

        // Verifica que outros métodos não foram chamados
        verify(userRepository, never()).createUser(any(User.class));
        verify(userDeviceRepository, never()).createUserDevice(any(UserDevice.class));
        verify(encoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should create user when user is exactly 16 years old")
    void shouldCreateUserWhenUserIsExactly16() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Lucas Furquim",
                "11111111111",
                "lucas@mail.com",
                "ishow123",
                LocalDate.now().minusYears(16),
                "Address"
        );

        UserDeviceDto deviceInfo = new UserDeviceDto("Mozilla/5.0");
        String encryptedPassword = "encrypted_password_hash";
        UUID userId = UUID.randomUUID();

        User userPersisted = User.builder()
                .id(userId)
                .cpf(request.cpf())
                .fullName(request.fullName())
                .email(request.email())
                .password(encryptedPassword)
                .birthDate(request.birthDate())
                .address(request.address())
                .build();

        when(userRepository.findByCpf(request.cpf())).thenReturn(Optional.empty());
        when(encoder.encode(request.password())).thenReturn(encryptedPassword);
        when(userRepository.createUser(any(User.class))).thenReturn(userPersisted);

        // Act
        assertDoesNotThrow(() -> registerService.register(request, deviceInfo));

        // Assert
        verify(userRepository).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should validate all required fields are passed to user creation")
    void shouldValidateAllRequiredFieldsArePassedToUserCreation() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Complete Name",
                "33333333333",
                "complete@mail.com",
                "strongPassword123",
                LocalDate.of(1985, 12, 10),
                "Complete Address, 123"
        );

        UserDeviceDto deviceInfo = new UserDeviceDto("Complete User Agent String");
        String encryptedPassword = "super_encrypted_password";
        UUID userId = UUID.randomUUID();

        User userPersisted = User.builder()
                .id(userId)
                .cpf(request.cpf())
                .fullName(request.fullName())
                .email(request.email())
                .password(encryptedPassword)
                .birthDate(request.birthDate())
                .address(request.address())
                .build();

        when(userRepository.findByCpf(request.cpf())).thenReturn(Optional.empty());
        when(encoder.encode(request.password())).thenReturn(encryptedPassword);
        when(userRepository.createUser(any(User.class))).thenReturn(userPersisted);

        // Act
        registerService.register(request, deviceInfo);

        // Assert
        verify(userRepository).createUser(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertAll(
                () -> assertEquals(request.cpf(), capturedUser.getCpf()),
                () -> assertEquals(request.fullName(), capturedUser.getFullName()),
                () -> assertEquals(request.email(), capturedUser.getEmail()),
                () -> assertEquals(encryptedPassword, capturedUser.getPassword()),
                () -> assertEquals(request.birthDate(), capturedUser.getBirthDate()),
                () -> assertEquals(request.address(), capturedUser.getAddress())
        );
    }
}