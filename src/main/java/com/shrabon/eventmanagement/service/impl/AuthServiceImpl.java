package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.RegisterForm;
import com.shrabon.eventmanagement.model.Client;
import com.shrabon.eventmanagement.model.User;
import com.shrabon.eventmanagement.model.enums.Role;
import com.shrabon.eventmanagement.repository.ClientRepository;
import com.shrabon.eventmanagement.repository.UserRepository;
import com.shrabon.eventmanagement.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           ClientRepository clientRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Client registerClient(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (!form.isPasswordMatching()) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        User user = new User();
        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail().toLowerCase().trim());
        user.setPhone(form.getPhone());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(Role.CLIENT);
        user.setEnabled(true);
        user = userRepository.save(user);

        Client client = new Client();
        client.setUser(user);
        client.setAddress(form.getAddress());
        client.setCity(form.getCity());
        client.setNid(form.getNid());
        return clientRepository.save(client);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
