package com.simple.identity.controller;

import com.simple.identity.dto.*;
import com.simple.identity.entity.User;
import com.simple.identity.security.CustomUserDetails;
import com.simple.identity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/activate")
    public Map<String, Object> activateAccount(@RequestParam("token") String token) {
        return authService.activateAccount(token);
    }

    @GetMapping("/me")
    public User me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userDetails.getUser();
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> response = authService.logout(userDetails.getUsername(), token);
        SecurityContextHolder.clearContext();

        return response;
    }

    @GetMapping("/validate")
    public UserDto validate(@RequestHeader("Authorization") String authHeader) {
        return authService.validateToken(authHeader);
    }

    @PostMapping("/change-password")
    public ChangePasswordResponse changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return authService.changePassword(userDetails.getUsername(), request);
    }


}
