package com.mindguard.auth.service;

import com.mindguard.auth.entity.User;
import com.mindguard.auth.mapper.UserMapper;
import com.mindguard.auth.service.impl.AuthServiceImpl;
import com.mindguard.common.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    private AuthService authService;
    private final JwtUtils jwtUtils = new JwtUtils(
            "test-secret-key-that-is-long-enough-for-hs256-algorithm", 86400000L);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userMapper, jwtUtils, encoder);
    }

    @Test
    void shouldRegisterSuccessfully() {
        when(userMapper.insert(any(User.class))).thenReturn(1);
        when(userMapper.selectCount(any())).thenReturn(0L);

        String token = authService.register("student01", "pass123", "小明");
        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void shouldFailRegisterDuplicateUsername() {
        when(userMapper.selectCount(any())).thenReturn(1L);

        assertThrows(IllegalArgumentException.class, () ->
                authService.register("student01", "pass123", "小明"));
    }

    @Test
    void shouldLoginSuccessfully() {
        String rawPassword = "pass123";
        User user = new User();
        user.setId(1L);
        user.setUsername("student01");
        user.setPassword(encoder.encode(rawPassword));
        user.setRole("USER");
        user.setStatus(1);

        when(userMapper.selectOne(any())).thenReturn(user);

        String token = authService.login("student01", rawPassword);
        assertNotNull(token);
        assertEquals(1L, jwtUtils.getUserId(token));
    }

    @Test
    void shouldFailLoginWrongPassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("student01");
        user.setPassword(encoder.encode("correct123"));
        user.setStatus(1);

        when(userMapper.selectOne(any())).thenReturn(user);

        assertThrows(SecurityException.class, () ->
                authService.login("student01", "wrong123"));
    }

    @Test
    void shouldFailLoginDisabledUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("student01");
        user.setPassword(encoder.encode("pass123"));
        user.setStatus(0);

        when(userMapper.selectOne(any())).thenReturn(user);

        assertThrows(SecurityException.class, () ->
                authService.login("student01", "pass123"));
    }
}
