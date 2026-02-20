package com.simple.identity.service;

import com.simple.identity.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DoctorEmailTemplateService {

    @Value("${email.doctor.registration.subject}")
    private String doctorRegistrationSubject;

    @Value("${email.doctor.registration.body}")
    private String doctorRegistrationBody;

    public String getDoctorRegistrationSubject(User user) {
        return doctorRegistrationSubject
                .replace("{firstName}", user.getFirstName())
                .replace("{lastName}", user.getLastName());
    }

    public String getDoctorRegistrationBody(User user, String token) {
        return doctorRegistrationBody
                .replace("{firstName}", user.getFirstName())
                .replace("{email}", user.getEmail())
                .replace("{token}", token);
    }

}
