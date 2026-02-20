package com.simple.identity.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    public String testEmail(@RequestBody EmailRequest request) throws JsonProcessingException {
        return emailClient.sendEmail(request);
    }


}
