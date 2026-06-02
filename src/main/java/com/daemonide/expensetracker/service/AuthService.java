package com.daemonide.expensetracker.service;

import com.daemonide.expensetracker.dto.AuthResponseDTO;
import com.daemonide.expensetracker.dto.LoginRequestDTO;
import com.daemonide.expensetracker.dto.RefreshRequestDTO;
import com.daemonide.expensetracker.dto.RegisterRequestDTO;
import com.daemonide.expensetracker.exception.InvalidLoginException;
import com.daemonide.expensetracker.exception.UserAlreadyExistsException;
import com.daemonide.expensetracker.model.AppUser;
import com.daemonide.expensetracker.model.RefreshToken;
import com.daemonide.expensetracker.repository.RefreshTokenRepository;
import com.daemonide.expensetracker.repository.UserRepository;
import com.daemonide.expensetracker.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final TurnstileService turnstileService;
    private final RefreshTokenRepository refreshTokenRepository;

    public String register(RegisterRequestDTO request) {
        if (!turnstileService.verify(request.getCaptchaToken())) {
            throw new InvalidLoginException("Captcha verification failed");
        }
        if (!PasswordValidator.isValid(request.getPassword())) {
            throw new RuntimeException(
                    "Password must contain uppercase, lowercase, number, special character and be at least 8 characters long"
            );
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("This username is taken");
        }

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        userRepository.save(user);

        return "User Registered";
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        if (!turnstileService.verify(request.getCaptchaToken())) {
            throw new InvalidLoginException("Captcha verification failed");
        }
        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidLoginException("Invalid Username"));

        boolean matches = encoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!matches) {
            throw new InvalidLoginException("Invalid Password");
        }

        String accessToken = jwtService.generateToken(user);

        String refreshTokenValue =
                jwtService.generateRefreshToken(
                        user.getUsername()
                );

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);

        refreshToken.setExpiryDate(
                jwtService
                        .extractExpiration(refreshTokenValue)
                        .toInstant()
        );

        refreshTokenRepository.save(refreshToken);

        return new AuthResponseDTO(
                accessToken,
                refreshTokenValue
        );
    }

    public AuthResponseDTO refresh(
            RefreshRequestDTO request
    ) {

        RefreshToken tokenEntity =
                refreshTokenRepository
                        .findByToken(
                                request.getRefreshToken()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Invalid refresh token"
                                )
                        );


        if (
                tokenEntity.getExpiryDate()
                        .isBefore(Instant.now())
        ) {
            throw new RuntimeException(
                    "Refresh token expired"
            );
        }

        AppUser user =
                tokenEntity.getUser();

        String newAccessToken =
                jwtService.generateToken(user);

        return new AuthResponseDTO(
                newAccessToken,
                request.getRefreshToken()
        );
    }

    public void logout(
            String refreshTokenValue
    ) {

        RefreshToken token =
                refreshTokenRepository
                        .findByToken(refreshTokenValue)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Invalid refresh token"
                                )
                        );

        refreshTokenRepository.delete(token);
    }
}