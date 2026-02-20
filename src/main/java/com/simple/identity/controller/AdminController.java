package com.simple.identity.controller;

import com.simple.identity.dto.RegisterRequest;
import com.simple.identity.dto.RegistrationResponse;
import com.simple.identity.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register-doctor")
    public RegistrationResponse registerDoctor(@RequestBody RegisterRequest request) {
        return adminService.registerDoctor(request);
    }

}
