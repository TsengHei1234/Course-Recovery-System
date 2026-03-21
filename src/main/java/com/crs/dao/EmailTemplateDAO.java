package com.crs.dao;

import com.crs.model.EmailTemplate;

public interface EmailTemplateDAO {
    EmailTemplate findActiveByCode(String templateCode);
}
