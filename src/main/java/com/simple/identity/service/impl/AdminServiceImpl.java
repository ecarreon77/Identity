package com.simple.identity.service.impl;

import com.simple.identity.config.EmailClient;
import com.simple.identity.dto.EmailRequest;
import com.simple.identity.dto.RegisterRequest;
import com.simple.identity.dto.RegistrationResponse;
import com.simple.identity.entity.User;
import com.simple.identity.exception.EmailAlreadyExistsException;
import com.simple.identity.exception.EmailSendFailedException;
import com.simple.identity.exception.InvalidSexException;
import com.simple.identity.repository.BlacklistedTokenRepository;
import com.simple.identity.repository.UserRepository;
import com.simple.identity.security.JwtUtil;
import com.simple.identity.service.AdminService;
import com.simple.identity.service.DoctorEmailTemplateService;
import com.simple.identity.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailClient emailClient;
    private final DoctorEmailTemplateService emailTemplateService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Override
    public RegistrationResponse registerDoctor(RegisterRequest request) {

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
                .password(passwordEncoder.encode(request.getPassword())) // 🔐 encrypted
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role("DOCTOR") // 🔥 changed role
                .isActivated(false)
                .contactNumber(request.getContactNumber())
                .birthDate(request.getBirthDate())
                .sex(request.getSex())
                .build();

        String token = jwtUtil.generateToken(user);
        user.setAuthToken(token);

        // 🔥 Different email template for doctor
        String subject = emailTemplateService.getDoctorRegistrationSubject(user);
        String body = emailTemplateService.getDoctorRegistrationBody(user, token);

        EmailRequest emailRequest = EmailRequest.builder()
                .to(List.of(user.getEmail()))
                .subject(subject)
                .body(body)
                .html(true)
                .build();

        try {
            emailClient.sendEmail(emailRequest);
            logger.info("Doctor registration email sent successfully to {}", user.getEmail());
        } catch (Exception e) {
            throw new EmailSendFailedException(
                    "Failed to send registration email to " + user.getEmail());
        }

        userRepository.save(user);

        return new RegistrationResponse(
                200,
                "Doctor registered successfully!"
        );
    }


}
