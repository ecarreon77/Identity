package com.simple.identity.service.impl;

import com.simple.identity.config.EmailClient;
import com.simple.identity.dto.*;
import com.simple.identity.entity.User;
import com.simple.identity.exception.*;
import com.simple.identity.repository.UserRepository;
import com.simple.identity.security.JwtUtil;
import com.simple.identity.service.AuthService;
import com.simple.identity.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailClient emailClient;
    private final EmailTemplateService emailTemplateService;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Override
    public RegistrationResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        if (request.getSex() != null &&
                !request.getSex().equalsIgnoreCase("MALE") &&
                !request.getSex().equalsIgnoreCase("FEMALE")) {
            throw new InvalidSexException("Sex must be either 'Male' or 'Female'");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role("USER")
                .isActivated(false)
                .contactNumber(request.getContactNumber())
                .birthDate(request.getBirthDate())
                .sex(request.getSex())
                .build();

        String token = jwtUtil.generateToken(user);
        user.setAuthToken(token);

        String subject = emailTemplateService.getRegistrationSubject(user);
        String body = emailTemplateService.getRegistrationBody(user, token);

        EmailRequest emailRequest = EmailRequest.builder()
                .to(List.of(user.getEmail()))
                .subject(subject)
                .body(body)
                .html(true)
                .build();

        try {
            emailClient.sendEmail(emailRequest);
            logger.info("Registration email sent successfully to {}", user.getEmail());
        } catch (Exception e) {
            throw new EmailSendFailedException(
                    "Failed to send registration email to " + user.getEmail() + ". Please try again later!");
        }

        userRepository.save(user);
        return new RegistrationResponse(
                200,
                "Registration successful!"
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Boolean.TRUE.equals(user.getIsActivated())) {
            throw new AccountNotActivatedException(
                    "Account not activated. Please check your email."
            );
        }

        String token = jwtUtil.generateToken(user);

        user.setAuthToken(token);
        userRepository.save(user);

        return new AuthResponse(token);
    }

    @Override
    public Map<String, Object> activateAccount(String token) {

        String email;
        try {
            email = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            throw new InvalidActivationTokenException("Invalid activation token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!token.equals(user.getAuthToken()) || !jwtUtil.isTokenValid(token, user)) {
            throw new InvalidActivationTokenException("Activation token invalid or expired");
        }

        if (Boolean.TRUE.equals(user.getIsActivated())) {
            throw new AccountAlreadyActivatedException("Account is already activated");
        }

        user.setIsActivated(true);
        userRepository.save(user);

        return Map.of(
                "status", 200,
                "message", "Account activated successfully!"
        );
    }

    @Override
    public Map<String, Object> logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAuthToken(null);
        userRepository.save(user);

        return Map.of(
                "status", 200,
                "message", "Logged out successfully!"
        );
    }

}
