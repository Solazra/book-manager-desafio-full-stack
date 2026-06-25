package com.bookmanager.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Schema(description = "Nome de exibição", example = "Ana Silva") @NotBlank String username,
    @Schema(description = "E-mail único", example = "ana@test.com") @NotBlank @Email String email,
    @Schema(description = "Senha (mín. 6)", example = "secret123") @NotBlank @Size(min = 6) String password) {}
