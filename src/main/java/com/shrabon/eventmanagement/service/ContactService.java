package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.ContactForm;
import com.shrabon.eventmanagement.model.ContactMessage;

import java.util.List;

public interface ContactService {

    ContactMessage save(ContactForm form);

    List<ContactMessage> findAll();

    ContactMessage markHandled(Long id);

    void delete(Long id);

    long countUnread();
}
