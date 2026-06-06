package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.model.WebsiteContent;
import com.shrabon.eventmanagement.repository.WebsiteContentRepository;
import com.shrabon.eventmanagement.service.WebsiteContentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class WebsiteContentServiceImpl implements WebsiteContentService {

    private final WebsiteContentRepository repository;

    public WebsiteContentServiceImpl(WebsiteContentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<WebsiteContent> findAll() {
        return repository.findAll();
    }

    @Override
    public String get(String key, String defaultValue) {
        return repository.findByContentKey(key)
                .map(WebsiteContent::getContentValue)
                .orElse(defaultValue);
    }

    @Override
    public Map<String, String> asMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (WebsiteContent c : repository.findAll()) {
            map.put(c.getContentKey(), c.getContentValue());
        }
        return map;
    }

    @Override
    @Transactional
    public void save(String key, String value) {
        WebsiteContent content = repository.findByContentKey(key)
                .orElseGet(() -> new WebsiteContent(key, null));
        content.setContentValue(value);
        repository.save(content);
    }
}
