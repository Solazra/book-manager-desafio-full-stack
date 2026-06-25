package com.bookmanager.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Schema(description = "E-mail cadastrado", example = "ana@test.com") @NotBlank @Email String email,
    @Schema(description = "Senha", example = "secret123") @NotBlank String password) {}
