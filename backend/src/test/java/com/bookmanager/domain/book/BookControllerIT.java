package com.bookmanager.domain.book;

import com.bookmanager.domain.auth.dto.AuthResponse;
import com.bookmanager.domain.auth.dto.LoginRequest;
import com.bookmanager.domain.auth.dto.RegisterRequest;
import com.bookmanager.domain.auth.dto.UserResponse;
import com.bookmanager.domain.book.dto.BookPageResponse;
import com.bookmanager.domain.book.dto.BookRequest;
import com.bookmanager.domain.book.dto.BookResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class BookControllerIT {

    private static final String JWT_SECRET = "test-secret-min-32-bytes-for-jwt-signing!!";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate rest;

    @Test
    void fluxoCompleto_registrarLoginCrudLivro() {
        register("alice@test.com", "Alice", "secret123");

        var token = login("alice@test.com", "secret123");
        var headers = authHeaders(token);

        var createReq = new BookRequest("Clean Code", "Robert Martin", 2008, "Boas práticas");
        var createResponse = rest.exchange(
            "/books/create",
            HttpMethod.POST,
            new HttpEntity<>(createReq, headers),
            BookResponse.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertEquals("Clean Code", createResponse.getBody().title());
        assertNotNull(createResponse.getHeaders().getLocation());
        assertTrue(Objects.requireNonNull(createResponse.getHeaders().getLocation()).getPath().contains("/books/"));

        Long bookId = createResponse.getBody().id();

        var listResponse = rest.exchange(
            "/books",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            BookPageResponse.class);

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertEquals(1, listResponse.getBody().totalElements());
        assertEquals(bookId, listResponse.getBody().content().getFirst().id());

        var updateReq = new BookRequest("Clean Code 2nd Ed", "Robert Martin", 2009, "Atualizado");
        var updateResponse = rest.exchange(
            "/books/" + bookId,
            HttpMethod.PUT,
            new HttpEntity<>(updateReq, headers),
            BookResponse.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Clean Code 2nd Ed", updateResponse.getBody().title());

        var deleteResponse = rest.exchange(
            "/books/" + bookId,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    void listarLivros_semToken_retorna401() {
        var response = rest.getForEntity("/books", String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void listarLivros_comTokenInvalido_retorna401() {
        var headers = authHeaders("token.invalido");
        var response = rest.exchange(
            "/books",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void listarLivros_comTokenExpirado_retorna401() {
        var headers = authHeaders(expiredToken());
        var response = rest.exchange(
            "/books",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void buscarLivro_deOutroUsuario_retorna200() {
        register("userA@test.com", "User A", "secret123");
        var tokenA = login("userA@test.com", "secret123");
        var headersA = authHeaders(tokenA);

        var createResponse = rest.exchange(
            "/books/create",
            HttpMethod.POST,
            new HttpEntity<>(new BookRequest("Livro A", "Autor A", 2020, null), headersA),
            BookResponse.class);
        Long bookIdA = createResponse.getBody().id();

        register("userB@test.com", "User B", "secret456");
        var tokenB = login("userB@test.com", "secret456");
        var headersB = authHeaders(tokenB);

        var response = rest.exchange(
            "/books/" + bookIdA,
            HttpMethod.GET,
            new HttpEntity<>(headersB),
            BookResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Livro A", response.getBody().title());
    }

    private void register(String email, String username, String password) {
        var response = rest.postForEntity(
            "/auth/register",
            new RegisterRequest(username, email, password),
            UserResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    private String login(String email, String password) {
        var response = rest.postForEntity(
            "/auth/login",
            new LoginRequest(email, password),
            AuthResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        return response.getBody().token();
    }

    private HttpHeaders authHeaders(String token) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private String expiredToken() {
        var key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        var now = System.currentTimeMillis();
        return Jwts.builder()
            .subject("expired@test.com")
            .issuedAt(new Date(now - 10_000))
            .expiration(new Date(now - 5_000))
            .signWith(key)
            .compact();
    }
}
