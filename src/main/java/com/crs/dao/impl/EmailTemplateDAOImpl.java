package com.crs.dao.impl;

import com.crs.dao.EmailTemplateDAO;
import com.crs.model.EmailTemplate;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Stateless
public class EmailTemplateDAOImpl implements EmailTemplateDAO {

    private static final String FIND_ACTIVE_BY_CODE_SQL =
            "SELECT email_template_id, template_code, template_name, module, trigger_event, subject_template, body_template, is_active " +
            "FROM email_templates WHERE template_code = ? AND is_active = 1 LIMIT 1";

    @Override
    public EmailTemplate findActiveByCode(String templateCode) {
        EmailTemplate emailTemplate = null;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ACTIVE_BY_CODE_SQL)) {

            preparedStatement.setString(1, templateCode);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    emailTemplate = new EmailTemplate();
                    emailTemplate.setEmailTemplateId(resultSet.getInt("email_template_id"));
                    emailTemplate.setTemplateCode(resultSet.getString("template_code"));
                    emailTemplate.setTemplateName(resultSet.getString("template_name"));
                    emailTemplate.setModule(resultSet.getString("module"));
                    emailTemplate.setTriggerEvent(resultSet.getString("trigger_event"));
                    emailTemplate.setSubjectTemplate(resultSet.getString("subject_template"));
                    emailTemplate.setBodyTemplate(resultSet.getString("body_template"));
                    emailTemplate.setActive(resultSet.getBoolean("is_active"));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return emailTemplate;
    }
}
