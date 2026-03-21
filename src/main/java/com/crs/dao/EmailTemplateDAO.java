package com.crs.dao;

import com.crs.model.EmailTemplate;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface EmailTemplateDAO {
    List<String> findAllModules();
    List<EmailTemplate> findTemplates(String moduleFilter, String search);
    EmailTemplate findByCode(String templateCode);
    EmailTemplate findActiveByCode(String templateCode);
}
