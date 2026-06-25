package com.bookmanager.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
    @Schema(description = "ID do usuário", example = "1") Long id,
    @Schema(description = "Nome de exibição", example = "Ana Silva") String username,
    @Schema(description = "E-mail", example = "ana@test.com") String email) {}
