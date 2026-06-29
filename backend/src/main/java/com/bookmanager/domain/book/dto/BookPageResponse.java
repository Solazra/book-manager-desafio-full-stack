package com.bookmanager.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record BookPageResponse(
    @Schema(description = "Livros da página atual") List<BookResponse> content,
    @Schema(description = "Índice da página (0-based)", example = "0") int page,
    @Schema(description = "Tamanho da página", example = "10") int size,
    @Schema(description = "Total de elementos", example = "42") long totalElements,
    @Schema(description = "Total de páginas", example = "5") int totalPages,
    @Schema(description = "Última página?", example = "false") boolean last) {
}
