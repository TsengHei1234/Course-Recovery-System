package com.crs.ejb;

import com.crs.model.EmailTemplate;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface EmailTemplateService {
    List<String> getModuleOptions();
    List<EmailTemplate> getTemplates(String moduleFilter, String search);
    EmailTemplate resolveSelectedTemplate(String selectedCode, List<EmailTemplate> filteredTemplates);
}
