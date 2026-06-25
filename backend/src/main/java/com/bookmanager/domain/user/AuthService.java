package com.bookmanager.domain.user;

import com.bookmanager.domain.auth.dto.AuthResponse;
import com.bookmanager.domain.auth.dto.LoginRequest;
import com.bookmanager.domain.auth.dto.RegisterRequest;
import com.bookmanager.domain.auth.dto.UserResponse;
import com.bookmanager.domain.shared.exception.BusinessException;
import com.bookmanager.domain.shared.mapper.UserMapper;
import com.bookmanager.domain.shared.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new BusinessException("E-mail já cadastrado", HttpStatus.CONFLICT);
        }
        var user = User.builder()
            .username(req.username())
            .email(req.email())
            .password(passwordEncoder.encode(req.password()))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        String token = tokenProvider.generateToken(req.email());
        return new AuthResponse(token, "Bearer", tokenProvider.getExpirationMs());
    }
}
