package com.bookmanager.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String token,
    @Schema(description = "Tipo do token", example = "Bearer") String type,
    @Schema(description = "Tempo de expiração em segundos", example = "86400") long expiresIn) {}
