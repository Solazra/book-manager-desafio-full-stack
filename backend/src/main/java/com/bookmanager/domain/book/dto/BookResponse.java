package com.bookmanager.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record BookResponse(
    @Schema(description = "ID do livro", example = "1") Long id,
    @Schema(example = "Clean Code") String title,
    @Schema(example = "Robert C. Martin") String author,
    @Schema(example = "2008", description = "Ano de publicação") Integer year,
    @Schema(example = "Boas práticas de código") String description,
    @Schema(description = "Data de criação") Instant createdAt,
    @Schema(description = "Data da última atualização") Instant updatedAt) {
}
