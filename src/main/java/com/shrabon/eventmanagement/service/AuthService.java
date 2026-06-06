package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.RegisterForm;
import com.shrabon.eventmanagement.model.Client;

public interface AuthService {

    /** Register a new CLIENT account (with profile). */
    Client registerClient(RegisterForm form);

    boolean emailExists(String email);
}
