package com.bookmanager.domain.auth.dto;

public record AuthResponse(String token, String type, long expiresIn) {}
