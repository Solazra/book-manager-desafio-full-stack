package com.bookmanager.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BookRequest(
    @Schema(example = "Clean Code") @NotBlank String title,
    @Schema(example = "Robert C. Martin") @NotBlank String author,
    @Schema(example = "2008", description = "Ano de publicação (1000–2100)") @Min(1000) @Max(2100) Integer year,
    @Schema(example = "Boas práticas de código") String description) {
}
