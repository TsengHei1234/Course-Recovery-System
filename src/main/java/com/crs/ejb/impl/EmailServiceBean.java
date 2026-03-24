package com.crs.ejb.impl;

import com.crs.dao.EmailTemplateDAO;
import com.crs.ejb.EmailService;
import com.crs.model.EmailTemplate;
import com.crs.util.GmailApiUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.Map;

@Stateless
public class EmailServiceBean implements EmailService {

    @EJB
    private EmailTemplateDAO emailTemplateDAO;

    @Override
    public void sendByTemplate(String templateCode, String toEmail, Map<String, String> values) {
        EmailTemplate template = emailTemplateDAO.findActiveByCode(templateCode);

        if (template == null || toEmail == null || toEmail.isBlank()) {
            return;
        }

        String subject = renderTemplate(template.getSubjectTemplate(), values);
        String body = renderTemplate(template.getBodyTemplate(), values);

        String result = GmailApiUtil.sendOtpEmail(toEmail, subject, body);
        System.out.println("Email send result [" + templateCode + "]: " + result);
    }

    @Override
    public String renderTemplate(String template, Map<String, String> values) {
        if (template == null) {
            return "";
        }

        String result = template;

        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? "" : entry.getValue();
                result = result.replace("{" + key + "}", value);
            }
        }

        return result;
    }
}