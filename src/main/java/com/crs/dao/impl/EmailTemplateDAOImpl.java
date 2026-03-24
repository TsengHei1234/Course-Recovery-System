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

    private static final String FIND_ALL_SQL =
            "SELECT email_template_id, template_code, template_name, module, trigger_event, " +
            "       subject_template, body_template, is_active " +
            "FROM email_templates " +
            "ORDER BY module, template_name";

    private static final String FIND_BY_ID_SQL =
            "SELECT email_template_id, template_code, template_name, module, trigger_event, " +
            "       subject_template, body_template, is_active " +
            "FROM email_templates " +
            "WHERE email_template_id = ?";

    private static final String FIND_ACTIVE_BY_CODE_SQL =
            "SELECT email_template_id, template_code, template_name, module, trigger_event, " +
            "       subject_template, body_template, is_active " +
            "FROM email_templates " +
            "WHERE template_code = ? AND is_active = 1";

    private static final String UPDATE_TEMPLATE_SQL =
            "UPDATE email_templates " +
            "SET template_name = ?, module = ?, trigger_event = ?, " +
            "    subject_template = ?, body_template = ?, is_active = ? " +
            "WHERE email_template_id = ?";

    @Override
    public List<EmailTemplate> findAll() {
        List<EmailTemplate> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {

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
        EmailTemplate template = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ACTIVE_BY_CODE_SQL)) {

            ps.setString(1, templateCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    template = mapRow(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findActiveByCode()");
            e.printStackTrace();
        }

        return template;
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

    private EmailTemplate mapRow(ResultSet rs) throws Exception {
        EmailTemplate t = new EmailTemplate();
        t.setEmailTemplateId(rs.getInt("email_template_id"));
        t.setTemplateCode(rs.getString("template_code"));
        t.setTemplateName(rs.getString("template_name"));
        t.setModule(rs.getString("module"));
        t.setTriggerEvent(rs.getString("trigger_event"));
        t.setSubjectTemplate(rs.getString("subject_template"));
        t.setBodyTemplate(rs.getString("body_template"));
        t.setActive(rs.getInt("is_active") == 1);
        return t;
    }
}