package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.ProfileForm;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.Client;
import com.shrabon.eventmanagement.model.User;
import com.shrabon.eventmanagement.repository.ClientRepository;
import com.shrabon.eventmanagement.repository.UserRepository;
import com.shrabon.eventmanagement.service.ClientService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> search(String query) {
        if (!StringUtils.hasText(query)) {
            return clientRepository.findAll();
        }
        return clientRepository.search(query.trim());
    }

    @Override
    public Client getById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + id));
    }

    @Override
    public Client getByUserId(Long userId) {
        return clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found for user: " + userId));
    }

    @Override
    public Client getByEmail(String email) {
        return clientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + email));
    }

    @Override
    @Transactional
    public Client updateProfile(Long userId, ProfileForm form) {
        Client client = getByUserId(userId);
        User user = client.getUser();
        user.setFullName(form.getFullName());
        user.setPhone(form.getPhone());
        if (StringUtils.hasText(form.getNewPassword())) {
            user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        }
        userRepository.save(user);

        client.setAddress(form.getAddress());
        client.setCity(form.getCity());
        client.setNid(form.getNid());
        return clientRepository.save(client);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Client client = getById(id);
        // Removing the user cascades to the client profile (and its bookings).
        clientRepository.delete(client);
        userRepository.delete(client.getUser());
    }

    @Override
    public long count() {
        return clientRepository.count();
    }
}
