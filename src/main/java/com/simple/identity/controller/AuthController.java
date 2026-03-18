package com.simple.identity.controller;

import com.simple.identity.dto.*;
import com.simple.identity.entity.User;
import com.simple.identity.exception.RateLimitExceededException;
import com.simple.identity.security.CustomUserDetails;
import com.simple.identity.service.AuthService;
import com.simple.identity.service.RateLimitService;
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
    private final RateLimitService rateLimitService;

    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        if (rateLimitService.isRateLimited(request.getEmail())) {
            throw new RateLimitExceededException("Too many login attempts. Please try again later.");
        }
        AuthResponse authResponse = authService.login(request);
        rateLimitService.recordLoginAttempt(request.getEmail());
        return authResponse;
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
