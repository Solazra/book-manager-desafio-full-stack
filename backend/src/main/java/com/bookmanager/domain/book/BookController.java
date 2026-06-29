package com.bookmanager.domain.book;

import com.bookmanager.domain.book.dto.BookPageResponse;
import com.bookmanager.domain.book.dto.BookRequest;
import com.bookmanager.domain.book.dto.BookResponse;
import com.bookmanager.domain.shared.exception.ErrorResponse;
import com.bookmanager.domain.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Livros", description = "CRUD de livros (leitura compartilhada; edição e exclusão apenas pelo dono)")
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Listar livros (paginado, com busca opcional por título)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de livros"),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public BookPageResponse list(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Filtro parcial por título", example = "Clean")
            @RequestParam(required = false) String title,
            @Parameter(description = "Índice da página (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página (máx. 100)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Ordenação: campo,direção (ex: title,asc)", example = "createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        return bookService.findAll(title, BookSort.toPageable(page, size, sort));
    }

    @Operation(summary = "Criar livro")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Livro criado"),
        @ApiResponse(responseCode = "400", description = "Payload inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<BookResponse> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody BookRequest req) {
        var created = bookService.create(user.getId(), req);
        return ResponseEntity.created(URI.create("/books/" + created.id())).body(created);
    }

    @Operation(summary = "Buscar livro por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Livro"),
        @ApiResponse(responseCode = "404", description = "Não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public BookResponse get(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(summary = "Atualizar livro")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado"),
        @ApiResponse(responseCode = "400", description = "Payload inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public BookResponse update(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody BookRequest req) {
        return bookService.update(user.getId(), id, req);
    }

    @Operation(summary = "Remover livro")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Removido"),
        @ApiResponse(responseCode = "404", description = "Não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id) {
        bookService.delete(user.getId(), id);
    }
}
