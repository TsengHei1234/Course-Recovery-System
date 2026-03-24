package com.crs.dao.impl;

import com.crs.dao.EmailTemplateDAO;
import com.crs.model.EmailTemplate;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class EmailTemplateDAOImpl implements EmailTemplateDAO {

    private static final String BASE_SELECT =
            "SELECT et.email_template_id, et.template_code, et.template_name, et.module, et.trigger_event, " +
            "et.subject_template, et.body_template, et.is_active, et.updated_by, et.updated_at, " +
            "u.name AS updated_by_name " +
            "FROM email_templates et " +
            "LEFT JOIN users u ON et.updated_by = u.user_id ";

    private static final String FIND_MODULES_SQL =
            "SELECT DISTINCT module FROM email_templates ORDER BY module";

    private static final String UPDATE_TEMPLATE_SQL =
            "UPDATE email_templates " +
            "SET template_name = ?, module = ?, trigger_event = ?, " +
            "    subject_template = ?, body_template = ?, is_active = ? " +
            "WHERE email_template_id = ?";

    @Override
    public List<String> findAllModules() {
        List<String> modules = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_MODULES_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                modules.add(resultSet.getString("module"));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return modules;
    }

    @Override
    public List<EmailTemplate> findTemplates(String moduleFilter, String search) {
        List<EmailTemplate> templateList = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(BASE_SELECT)
                .append("WHERE 1=1 ");

        if (moduleFilter != null && !moduleFilter.isBlank()) {
            sqlBuilder.append("AND et.module = ? ");
            parameters.add(moduleFilter.trim());
        }

        if (search != null && !search.isBlank()) {
            sqlBuilder.append("AND (UPPER(et.template_name) LIKE ? OR UPPER(et.template_code) LIKE ? OR UPPER(et.trigger_event) LIKE ?) ");
            String searchPattern = "%" + search.trim().toUpperCase() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        sqlBuilder.append("ORDER BY et.module, et.template_name, et.email_template_id");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {

            bindParameters(preparedStatement, parameters);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    templateList.add(mapRow(resultSet));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return templateList;
    }

    @Override
    public EmailTemplate findByCode(String templateCode) {
        if (templateCode == null || templateCode.isBlank()) {
            return null;
        }

        String sql = BASE_SELECT + "WHERE et.template_code = ? LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, templateCode.trim());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    public List<EmailTemplate> findAll() {
        List<EmailTemplate> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY et.module, et.template_name, et.email_template_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            System.out.println("ERROR in findAll() email templates");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public EmailTemplate findById(int templateId) {
        EmailTemplate template = null;
        String sql = BASE_SELECT + "WHERE et.email_template_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, templateId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    template = mapRow(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findById() email template");
            e.printStackTrace();
        }

        return template;
    }

    @Override
    public EmailTemplate findActiveByCode(String templateCode) {
        if (templateCode == null || templateCode.isBlank()) {
            return null;
        }

        String sql = BASE_SELECT + "WHERE et.template_code = ? AND et.is_active = 1 LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, templateCode.trim());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    public void updateTemplate(EmailTemplate template) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_TEMPLATE_SQL)) {

            ps.setString(1, template.getTemplateName());
            ps.setString(2, template.getModule());
            ps.setString(3, template.getTriggerEvent());
            ps.setString(4, template.getSubjectTemplate());
            ps.setString(5, template.getBodyTemplate());
            ps.setInt(6, template.isActive() ? 1 : 0);
            ps.setInt(7, template.getEmailTemplateId());
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in updateTemplate()");
            e.printStackTrace();
        }
    }

    private void bindParameters(PreparedStatement preparedStatement, List<Object> parameters) throws Exception {
        for (int index = 0; index < parameters.size(); index++) {
            preparedStatement.setObject(index + 1, parameters.get(index));
        }
    }

    private EmailTemplate mapRow(ResultSet resultSet) throws Exception {
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setEmailTemplateId(resultSet.getInt("email_template_id"));
        emailTemplate.setTemplateCode(resultSet.getString("template_code"));
        emailTemplate.setTemplateName(resultSet.getString("template_name"));
        emailTemplate.setModule(resultSet.getString("module"));
        emailTemplate.setTriggerEvent(resultSet.getString("trigger_event"));
        emailTemplate.setSubjectTemplate(resultSet.getString("subject_template"));
        emailTemplate.setBodyTemplate(resultSet.getString("body_template"));
        emailTemplate.setActive(resultSet.getBoolean("is_active"));

        int updatedByValue = resultSet.getInt("updated_by");
        if (!resultSet.wasNull()) {
            emailTemplate.setUpdatedBy(updatedByValue);
        }

        emailTemplate.setUpdatedByName(resultSet.getString("updated_by_name"));
        emailTemplate.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return emailTemplate;
    }
}
