package com.simple.identity.controller;

import com.simple.identity.config.EmailClient;
import com.simple.identity.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-email")
@RequiredArgsConstructor
public class TestEmailController {

    private final EmailClient emailClient;

    @PostMapping
    public String testEmail(@RequestBody EmailRequest request) {
        return emailClient.sendEmail(request);
    }


}
