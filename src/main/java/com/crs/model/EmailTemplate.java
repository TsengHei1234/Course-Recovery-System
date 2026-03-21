package com.crs.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EmailTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    private int emailTemplateId;
    private String templateCode;
    private String templateName;
    private String module;
    private String triggerEvent;
    private String subjectTemplate;
    private String bodyTemplate;
    private boolean active;
    private Integer updatedBy;
    private String updatedByName;
    private Timestamp updatedAt;

    private List<String> placeholders = new ArrayList<>();
    private String previewSubject;
    private String previewBodyHtml;
    private String bodyTemplateEscaped;
    private boolean htmlTemplate;
    private String updatedAtDisplay;

    public int getEmailTemplateId() {
        return emailTemplateId;
    }

    public void setEmailTemplateId(int emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(String triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public String getSubjectTemplate() {
        return subjectTemplate;
    }

    public void setSubjectTemplate(String subjectTemplate) {
        this.subjectTemplate = subjectTemplate;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(List<String> placeholders) {
        this.placeholders = placeholders;
    }

    public String getPreviewSubject() {
        return previewSubject;
    }

    public void setPreviewSubject(String previewSubject) {
        this.previewSubject = previewSubject;
    }

    public String getPreviewBodyHtml() {
        return previewBodyHtml;
    }

    public void setPreviewBodyHtml(String previewBodyHtml) {
        this.previewBodyHtml = previewBodyHtml;
    }

    public String getBodyTemplateEscaped() {
        return bodyTemplateEscaped;
    }

    public void setBodyTemplateEscaped(String bodyTemplateEscaped) {
        this.bodyTemplateEscaped = bodyTemplateEscaped;
    }

    public boolean isHtmlTemplate() {
        return htmlTemplate;
    }

    public void setHtmlTemplate(boolean htmlTemplate) {
        this.htmlTemplate = htmlTemplate;
    }

    public String getUpdatedAtDisplay() {
        return updatedAtDisplay;
    }

    public void setUpdatedAtDisplay(String updatedAtDisplay) {
        this.updatedAtDisplay = updatedAtDisplay;
    }
}
