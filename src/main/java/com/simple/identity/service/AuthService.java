package com.simple.identity.service;

import com.simple.identity.dto.AuthResponse;
import com.simple.identity.dto.LoginRequest;
import com.simple.identity.dto.RegisterRequest;
import com.simple.identity.dto.RegistrationResponse;

import java.util.Map;

public interface AuthService {

    RegistrationResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    Map<String, Object> activateAccount(String token);
    Map<String, Object> logout(String email);

}
