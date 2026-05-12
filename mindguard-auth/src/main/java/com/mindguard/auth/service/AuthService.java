package com.mindguard.auth.service;

public interface AuthService {
    String register(String username, String password, String nickname);
    String login(String username, String password);
}
