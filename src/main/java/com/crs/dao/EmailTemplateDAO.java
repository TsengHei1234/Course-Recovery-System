package com.crs.dao;

import com.crs.model.EmailTemplate;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface EmailTemplateDAO {
    List<EmailTemplate> findAll();
    EmailTemplate findById(int templateId);
    EmailTemplate findActiveByCode(String templateCode);
    void updateTemplate(EmailTemplate template);
}