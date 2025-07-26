package com.adx.agent.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String code; // Verification code
    // Add password field if needed for traditional login
    // private String password;
} 