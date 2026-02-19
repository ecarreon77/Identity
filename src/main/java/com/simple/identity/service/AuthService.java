package com.simple.identity.service;

import com.simple.identity.dto.*;

import java.util.Map;

public interface AuthService {

    RegistrationResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    Map<String, Object> activateAccount(String token);
    Map<String, Object> logout(String email, String token);
    UserDto validateToken(String token);
    ChangePasswordResponse changePassword(String email, ChangePasswordRequest request);

}
