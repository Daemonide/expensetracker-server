package com.daemonide.expensetracker.controller;

import com.daemonide.expensetracker.dto.*;
import com.daemonide.expensetracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class UserController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequestDTO request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponseDTO refresh(
            @RequestBody RefreshRequestDTO request
    ) {
        return authService.refresh(request);
    }


    @PostMapping("/logout")
    public String logout(
            @RequestBody LogoutRequestDTO request
    ) {

        authService.logout(
                request.getRefreshToken()
        );

        return "Logged out";
    }
}
