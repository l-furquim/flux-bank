package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.domain.exceptions.UnauthorizedAuthException;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.model.UserDevice;
import com.fluxbank.user_service.domain.repository.UserDeviceRepository;
import com.fluxbank.user_service.domain.repository.UserRepository;
import com.fluxbank.user_service.domain.service.CacheService;
import com.fluxbank.user_service.domain.service.TokenService;
import com.fluxbank.user_service.interfaces.dto.AuthUserRequest;
import com.fluxbank.user_service.interfaces.dto.AuthUserResponse;
import com.fluxbank.user_service.interfaces.dto.UserTokenData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUserServiceTest {

    @Mock
    private UserDeviceRepository userDeviceRepository;

    @Mock
    private UserRepository repository;

    @Mock
    private TokenService tokenService;

    @Mock
    private CacheService cacheService;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthUserService authUserService;

    private User mockUser;
    private AuthUserRequest mockRequest;
    private String userAgent;
    private String token;
    private Instant expirationDate;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(UUID.randomUUID())
                .cpf("12345678901")
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        mockRequest = new AuthUserRequest("12345678901", "plainPassword");
        userAgent = "Agent";
        token = "generated-jwt-token";
        expirationDate =  Instant.now().plusMillis(3600000); // 1 hour from now
    }

    @Test
    void auth_ShouldReturnAuthUserResponse_WhenValidCredentialsAndExistingDevice() {
        // Arrange
        UUID deviceId = UUID.randomUUID();

        UserDevice existingDevice = UserDevice.builder()
                .id(deviceId)
                .userId(UUID.randomUUID())
                .userAgent(userAgent)
                .build();

        when(repository.findByCpf(mockRequest.cpf())).thenReturn(Optional.of(mockUser));
        when(encoder.matches(mockRequest.password(), mockUser.getPassword())).thenReturn(true);
        when(tokenService.generateToken(mockUser)).thenReturn(token);
        when(userDeviceRepository.findByUserId(mockUser.getId())).thenReturn(List.of(existingDevice));
        when(tokenService.getExpirationDate(token)).thenReturn(expirationDate);

        // Act
        AuthUserResponse response = authUserService.auth(mockRequest, userAgent);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.token());
        assertNotNull(response.tokenData());
        assertEquals(mockUser.getId(), response.tokenData().getUserId());
        assertEquals(mockUser.getEmail(), response.tokenData().getEmail());
        assertEquals(deviceId.toString(), response.tokenData().getDeviceId());
        assertEquals(expirationDate, response.tokenData().getExpiresAt());

        verify(repository).findByCpf(mockRequest.cpf());
        verify(encoder).matches(mockRequest.password(), mockUser.getPassword());
        verify(tokenService).generateToken(mockUser);
        verify(userDeviceRepository).findByUserId(mockUser.getId());
        verify(tokenService).getExpirationDate(token);
        verify(cacheService).cacheToken(eq(token), any(UserTokenData.class));
        verify(userDeviceRepository, never()).createUserDevice(any());
    }

    @Test
    void auth_ShouldCreateNewDevice_WhenNoExistingDeviceFound() {
        // Arrange
        UUID deviceId = UUID.randomUUID();

        UserDevice newDevice = UserDevice.builder()
                .id(deviceId)
                .userId(UUID.randomUUID())
                .userAgent(userAgent)
                .build();

        when(repository.findByCpf(mockRequest.cpf())).thenReturn(Optional.of(mockUser));
        when(encoder.matches(mockRequest.password(), mockUser.getPassword())).thenReturn(true);
        when(tokenService.generateToken(mockUser)).thenReturn(token);
        when(userDeviceRepository.findByUserId(mockUser.getId())).thenReturn(Collections.emptyList());
        when(userDeviceRepository.createUserDevice(any(UserDevice.class))).thenReturn(newDevice);
        when(tokenService.getExpirationDate(token)).thenReturn(expirationDate);

        // Act
        AuthUserResponse response = authUserService.auth(mockRequest, userAgent);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.token());
        assertEquals(deviceId.toString(), response.tokenData().getDeviceId());

        verify(userDeviceRepository).createUserDevice(argThat(device ->
                device.getUserId().equals(mockUser.getId()) &&
                        device.getUserAgent().equals(userAgent)
        ));
        verify(cacheService).cacheToken(eq(token), any(UserTokenData.class));
    }


    @Test
    void auth_ShouldUseExistingDevice_WhenDeviceWithDifferentUserAgentExists() {
        // Arrange

        UUID userId = UUID.randomUUID();
        UUID deviceWithSameAgentId = UUID.randomUUID();

        UserDevice deviceWithDifferentAgent = UserDevice.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .userAgent("Different User Agent")
                .build();

        UserDevice deviceWithSameAgent = UserDevice.builder()
                .id(deviceWithSameAgentId)
                .userId(userId)
                .userAgent(userAgent)
                .build();

        UserDevice newDevice = UserDevice.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .userAgent(userAgent)
                .build();

        when(repository.findByCpf(mockRequest.cpf())).thenReturn(Optional.of(mockUser));
        when(encoder.matches(mockRequest.password(), mockUser.getPassword())).thenReturn(true);
        when(tokenService.generateToken(mockUser)).thenReturn(token);
        when(userDeviceRepository.findByUserId(mockUser.getId())).thenReturn(Arrays.asList(deviceWithDifferentAgent, deviceWithSameAgent));
        when(tokenService.getExpirationDate(token)).thenReturn(expirationDate);

        // Act
        AuthUserResponse response = authUserService.auth(mockRequest, userAgent);

        // Assert
        assertNotNull(response);
        assertEquals(deviceWithSameAgentId.toString(), response.tokenData().getDeviceId());

        verify(userDeviceRepository, never()).createUserDevice(any(UserDevice.class));
    }

    @Test
    void auth_ShouldThrowUnauthorizedAuthException_WhenUserNotFound() {
        // Arrange
        when(repository.findByCpf(mockRequest.cpf())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UnauthorizedAuthException.class, () -> {
            authUserService.auth(mockRequest, userAgent);
        });

        verify(repository).findByCpf(mockRequest.cpf());
        verify(encoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateToken(any());
        verify(userDeviceRepository, never()).findByUserId(UUID.randomUUID());
        verify(cacheService, never()).cacheToken(anyString(), any());
    }

    @Test
    void auth_ShouldThrowUnauthorizedAuthException_WhenPasswordDoesNotMatch() {
        // Arrange
        when(repository.findByCpf(mockRequest.cpf())).thenReturn(Optional.of(mockUser));
        when(encoder.matches(mockRequest.password(), mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAuthException.class, () -> {
            authUserService.auth(mockRequest, userAgent);
        });

        verify(repository).findByCpf(mockRequest.cpf());
        verify(encoder).matches(mockRequest.password(), mockUser.getPassword());
        verify(tokenService, never()).generateToken(any());
        verify(userDeviceRepository, never()).findByUserId(UUID.randomUUID());
        verify(cacheService, never()).cacheToken(anyString(), any());
    }

    @Test
    void auth_ShouldCacheTokenWithCorrectData() {
        // Arrange
        UserDevice existingDevice = UserDevice.builder()
                .id(UUID.randomUUID())
                .userId(mockUser.getId())
                .userAgent(userAgent)
                .build();

        when(repository.findByCpf(mockRequest.cpf())).thenReturn(Optional.of(mockUser));
        when(encoder.matches(mockRequest.password(), mockUser.getPassword())).thenReturn(true);
        when(tokenService.generateToken(mockUser)).thenReturn(token);
        when(userDeviceRepository.findByUserId(mockUser.getId())).thenReturn(List.of(existingDevice));
        when(tokenService.getExpirationDate(token)).thenReturn(expirationDate);

        // Act
        authUserService.auth(mockRequest, userAgent);

        // Assert
        verify(cacheService).cacheToken(eq(token), argThat(tokenData ->
                tokenData.getUserId().equals(mockUser.getId()) &&
                        tokenData.getEmail().equals(mockUser.getEmail()) &&
                        tokenData.getDeviceId().equals(existingDevice.getId().toString()) &&
                        tokenData.getExpiresAt().equals(expirationDate) &&
                        tokenData.getIssuedAt() != null
        ));
    }

    @Test
    void auth_ShouldHandleNullUserAgent() {
        // Arrange
        UserDevice newDevice = UserDevice.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .userAgent(null)
                .build();

        when(repository.findByCpf(mockRequest.cpf())).thenReturn(Optional.of(mockUser));
        when(encoder.matches(mockRequest.password(), mockUser.getPassword())).thenReturn(true);
        when(tokenService.generateToken(mockUser)).thenReturn(token);
        when(userDeviceRepository.findByUserId(mockUser.getId())).thenReturn(Collections.emptyList());
        when(userDeviceRepository.createUserDevice(any(UserDevice.class))).thenReturn(newDevice);
        when(tokenService.getExpirationDate(token)).thenReturn(expirationDate);

        // Act
        AuthUserResponse response = authUserService.auth(mockRequest, null);

        // Assert
        assertNotNull(response);
        verify(userDeviceRepository).createUserDevice(argThat(device ->
                device.getUserAgent() == null
        ));
    }

    @Test
    void auth_ShouldVerifyTokenDataIssuedAtIsRecent() {
        // Arrange
        UserDevice existingDevice = UserDevice.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .userAgent(userAgent)
                .build();

        when(repository.findByCpf(mockRequest.cpf())).thenReturn(Optional.of(mockUser));
        when(encoder.matches(mockRequest.password(), mockUser.getPassword())).thenReturn(true);
        when(tokenService.generateToken(mockUser)).thenReturn(token);
        when(userDeviceRepository.findByUserId(mockUser.getId())).thenReturn(List.of(existingDevice));
        when(tokenService.getExpirationDate(token)).thenReturn(expirationDate);

        Instant beforeAuth = Instant.now().minusSeconds(1);

        // Act
        AuthUserResponse response = authUserService.auth(mockRequest, userAgent);

        Instant afterAuth = Instant.now().plusSeconds(1);

        // Assert
        assertNotNull(response.tokenData().getIssuedAt());
        assertTrue(response.tokenData().getIssuedAt().isAfter(beforeAuth));
        assertTrue(response.tokenData().getIssuedAt().isBefore(afterAuth));
    }
}