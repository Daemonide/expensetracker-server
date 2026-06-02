package com.daemonide.expensetracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDTO {

    private String refreshToken;
}