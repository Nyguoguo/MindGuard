package com.mindguard.auth.controller;

import com.mindguard.auth.service.AuthService;
import com.mindguard.common.dto.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<Map<String, String>> register(@RequestBody Map<String, String> body) {
        String token = authService.register(
                body.get("username"),
                body.get("password"),
                body.getOrDefault("nickname", body.get("username")));
        return Result.success(Map.of("token", token));
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String token = authService.login(
                body.get("username"),
                body.get("password"));
        return Result.success(Map.of("token", token));
    }
}
