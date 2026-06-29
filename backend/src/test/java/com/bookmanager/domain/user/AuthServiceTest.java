package com.bookmanager.domain.user;

import com.bookmanager.domain.auth.dto.LoginRequest;
import com.bookmanager.domain.auth.dto.RegisterRequest;
import com.bookmanager.domain.auth.dto.UserResponse;
import com.bookmanager.domain.shared.exception.BusinessException;
import com.bookmanager.domain.shared.mapper.UserMapper;
import com.bookmanager.domain.shared.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_sucesso_salvaSenhaCodificadaERetornaUserResponse() {
        var req = new RegisterRequest("Ana", "ana@test.com", "secret123");
        when(userRepository.existsByEmail("ana@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-pass");

        var savedUser = User.builder()
            .id(1L)
            .username("Ana")
            .email("ana@test.com")
            .password("encoded-pass")
            .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var expected = new UserResponse(1L, "Ana", "ana@test.com");
        when(userMapper.toResponse(savedUser)).thenReturn(expected);

        var result = authService.register(req);

        assertEquals(expected, result);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded-pass", userCaptor.getValue().getPassword());
        verify(passwordEncoder).encode("secret123");
    }

    @Test
    void register_emailExistente_lanca409() {
        var req = new RegisterRequest("Ana", "ana@test.com", "secret123");
        when(userRepository.existsByEmail("ana@test.com")).thenReturn(true);

        var ex = assertThrows(BusinessException.class, () -> authService.register(req));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_sucesso_autenticaERetornaToken() {
        var req = new LoginRequest("ana@test.com", "secret123");
        when(tokenProvider.generateToken("ana@test.com")).thenReturn("jwt-token");
        when(tokenProvider.getExpirationMs()).thenReturn(86400000L);

        var result = authService.login(req);

        verify(authenticationManager).authenticate(
            new UsernamePasswordAuthenticationToken("ana@test.com", "secret123"));
        assertEquals("jwt-token", result.token());
        assertEquals("Bearer", result.type());
        assertEquals(86400000L, result.expiresIn());
    }

    @Test
    void login_credenciaisInvalidas_propagaBadCredentialsException() {
        var req = new LoginRequest("ana@test.com", "wrong");
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
        verify(tokenProvider, never()).generateToken(any());
    }
}
