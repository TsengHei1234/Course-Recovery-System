package com.crs.ejb;

import jakarta.ejb.Local;
import java.util.Map;

@Local
public interface EmailService {
    void sendByTemplate(String templateCode, String toEmail, Map<String, String> values);
    String renderTemplate(String template, Map<String, String> values);
}