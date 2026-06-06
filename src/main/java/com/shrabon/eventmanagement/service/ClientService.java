package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.ProfileForm;
import com.shrabon.eventmanagement.model.Client;

import java.util.List;

public interface ClientService {

    List<Client> findAll();

    List<Client> search(String query);

    Client getById(Long id);

    Client getByUserId(Long userId);

    Client getByEmail(String email);

    Client updateProfile(Long userId, ProfileForm form);

    void delete(Long id);

    long count();
}
