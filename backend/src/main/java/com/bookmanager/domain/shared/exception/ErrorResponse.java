package com.bookmanager.domain.shared.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Resposta padronizada de erro da API")
public record ErrorResponse(
    @Schema(description = "Momento do erro") Instant timestamp,
    @Schema(description = "Código HTTP", example = "400") int status,
    @Schema(description = "Nome do status HTTP", example = "Bad Request") String error,
    @Schema(description = "Mensagem descritiva", example = "Título é obrigatório") String message,
    @Schema(description = "Caminho da requisição", example = "/books/create") String path,
    @Schema(description = "Erros por campo (validação)") Map<String, String> fieldErrors) {}
