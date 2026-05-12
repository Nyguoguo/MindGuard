package com.mindguard.common;

import com.mindguard.common.dto.Result;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void successShouldReturn200() {
        Result<String> result = Result.success("hello");
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("hello", result.getData());
    }

    @Test
    void successWithoutDataShouldReturn200() {
        Result<Void> result = Result.success();
        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }

    @Test
    void errorShouldReturnGivenCode() {
        Result<Void> result = Result.error(401, "Token已过期");
        assertEquals(401, result.getCode());
        assertEquals("Token已过期", result.getMessage());
    }

    @Test
    void failShouldReturn500() {
        Result<Void> result = Result.fail("服务器异常");
        assertEquals(500, result.getCode());
        assertEquals("服务器异常", result.getMessage());
    }
}
