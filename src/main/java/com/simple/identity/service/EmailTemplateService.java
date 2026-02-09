package com.simple.identity.service;

import com.simple.identity.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    @Value("${email.registration.subject}")
    private String registrationSubject;

    @Value("${email.registration.body}")
    private String registrationBody;

    public String getRegistrationSubject(User user) {
        return registrationSubject
                .replace("{firstName}", user.getFirstName())
                .replace("{lastName}", user.getLastName());
    }

    public String getRegistrationBody(User user, String token) {
        return registrationBody
                .replace("{firstName}", user.getFirstName())
                .replace("{email}", user.getEmail())
                .replace("{token}", token);
    }

}
