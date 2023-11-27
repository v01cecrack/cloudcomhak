package com.example.serversdk.auth.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class JwtRequest {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
