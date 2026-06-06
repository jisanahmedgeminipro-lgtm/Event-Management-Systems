package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.ContactForm;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.ContactMessage;
import com.shrabon.eventmanagement.repository.ContactMessageRepository;
import com.shrabon.eventmanagement.service.ContactService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContactServiceImpl implements ContactService {

    private final ContactMessageRepository repository;

    public ContactServiceImpl(ContactMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public ContactMessage save(ContactForm form) {
        ContactMessage message = new ContactMessage();
        message.setName(form.getName());
        message.setEmail(form.getEmail());
        message.setPhone(form.getPhone());
        message.setSubject(form.getSubject());
        message.setMessage(form.getMessage());
        message.setHandled(false);
        return repository.save(message);
    }

    @Override
    public List<ContactMessage> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public ContactMessage markHandled(Long id) {
        ContactMessage message = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found: " + id));
        message.setHandled(true);
        return repository.save(message);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public long countUnread() {
        return repository.countByHandledFalse();
    }
}
