package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.model.WebsiteContent;

import java.util.List;
import java.util.Map;

public interface WebsiteContentService {

    List<WebsiteContent> findAll();

    String get(String key, String defaultValue);

    Map<String, String> asMap();

    void save(String key, String value);
}
