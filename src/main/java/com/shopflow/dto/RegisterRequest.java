package com.shopflow.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(

        @NotBlank String email,
        @NotBlank String password
){}
