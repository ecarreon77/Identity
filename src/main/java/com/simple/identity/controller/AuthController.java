package com.simple.identity.controller;

import com.simple.identity.dto.AuthResponse;
import com.simple.identity.dto.LoginRequest;
import com.simple.identity.dto.RegisterRequest;
import com.simple.identity.dto.RegistrationResponse;
import com.simple.identity.entity.User;
import com.simple.identity.security.CustomUserDetails;
import com.simple.identity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        authService.register(request);

        return new RegistrationResponse(
                200,
                "Registration successful"
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/activate")
    public Map<String, Object> activateAccount(@RequestParam("token") String token) {

        authService.activateAccount(token);

        return Map.of(
                "status", 200,
                "message", "Account activated successfully!"
        );
    }

    @GetMapping("/me")
    public User me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userDetails.getUser();
    }

    @PostMapping("/logout")
    public Map<String, String> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authService.logout(userDetails.getUsername());
        SecurityContextHolder.clearContext();

        return Map.of("message", "Logged out successfully");
    }


}
