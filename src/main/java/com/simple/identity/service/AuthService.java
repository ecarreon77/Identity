package com.simple.identity.service;

import com.simple.identity.dto.AuthResponse;
import com.simple.identity.dto.LoginRequest;
import com.simple.identity.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void activateAccount(String token);
    void logout(String email);

}
