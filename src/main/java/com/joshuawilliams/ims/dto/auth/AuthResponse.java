package com.joshuawilliams.ims.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String businessName;
}
