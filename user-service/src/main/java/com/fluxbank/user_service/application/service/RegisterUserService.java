package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.interfaces.dto.CreateUserRequest;
import com.fluxbank.user_service.interfaces.dto.UserDeviceDto;
import com.fluxbank.user_service.application.usecase.RegisterUserUsecase;
import com.fluxbank.user_service.domain.exceptions.InvalidUserBirthdate;
import com.fluxbank.user_service.domain.exceptions.UnnauthorizedAccountCreation;
import com.fluxbank.user_service.domain.model.User;
import com.fluxbank.user_service.domain.model.UserDevice;
import com.fluxbank.user_service.domain.repository.UserDeviceRepository;
import com.fluxbank.user_service.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Service
public class RegisterUserService implements RegisterUserUsecase {

    private final UserRepository userRepository;
    private final UserDeviceRepository userDeviceRepository;

    public RegisterUserService(UserRepository userRepository, UserDeviceRepository userDeviceRepository) {
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
    }

    @Override
    public void register(CreateUserRequest request, UserDeviceDto deviceInfo) {

        boolean userAlreadyExists = userRepository.findByCpf(request.cpf()).isPresent();

        if(userAlreadyExists) {
            throw new UnnauthorizedAccountCreation("We only support 1 account per cpf");
        }

        LocalDate birthDate = request.birthDate();
        LocalDate today = LocalDate.now();

        int userAge = Period.between(birthDate, today).getYears();

        if(userAge < 16) {
            throw new InvalidUserBirthdate();
        }

        User user = User
                .builder()
                .cpf(request.cpf())
                .email(request.email())
                .fullName(request.fullName())
                .birthDate(request.birthDate())
                .address(request.address())
                .build();

        User userPersisted = userRepository.createUser(user);

        UserDevice device = UserDevice.builder()
                .userId(userPersisted.getId())
                .userAgent(deviceInfo.userAgent())
                .build();

        userDeviceRepository.createUserDevice(device);
    }
}
