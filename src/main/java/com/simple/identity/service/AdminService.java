package com.simple.identity.service;

import com.simple.identity.dto.RegisterRequest;
import com.simple.identity.dto.RegistrationResponse;

public interface AdminService {

    RegistrationResponse registerDoctor(RegisterRequest request);

}
