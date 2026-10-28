package com.jforce.order.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank @Size(max = 255) String username,
        @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 72) String password) { }
