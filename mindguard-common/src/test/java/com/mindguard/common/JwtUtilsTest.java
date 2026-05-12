package com.mindguard.common;

import com.mindguard.common.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String secret = "test-secret-key-that-is-long-enough-for-hs256";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(secret, 86400000L); // 24h
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtUtils.generateToken(1L, "student01", "USER");
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void shouldParseToken() {
        String token = jwtUtils.generateToken(1L, "student01", "USER");
        assertTrue(jwtUtils.validateToken(token));
        assertEquals(1L, jwtUtils.getUserId(token));
        assertEquals("student01", jwtUtils.getUsername(token));
        assertEquals("USER", jwtUtils.getRole(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertFalse(jwtUtils.validateToken("invalid.token.here"));
    }

    @Test
    void shouldRejectExpiredToken() throws Exception {
        JwtUtils shortLived = new JwtUtils(secret, -1L); // already expired
        String expiredToken = shortLived.generateToken(1L, "test", "USER");
        Thread.sleep(10);
        assertFalse(shortLived.validateToken(expiredToken));
    }
}
