package com.crs.ejb.impl;

import com.crs.dao.EmailTemplateDAO;
import com.crs.ejb.EmailTemplateService;
import com.crs.model.EmailTemplate;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
public class EmailTemplateServiceBean implements EmailTemplateService {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([A-Za-z0-9_]+)}");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<\\s*[a-zA-Z][^>]*>");
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("dd MMM yyyy, hh:mm a");

    @EJB
    private EmailTemplateDAO emailTemplateDAO;

    @Override
    public List<String> getModuleOptions() {
        return emailTemplateDAO.findAllModules();
    }

    @Override
    public List<EmailTemplate> getTemplates(String moduleFilter, String search) {
        List<EmailTemplate> templateList = emailTemplateDAO.findTemplates(moduleFilter, search);

        for (EmailTemplate emailTemplate : templateList) {
            enrichTemplate(emailTemplate);
        }

        return templateList;
    }

    @Override
    public EmailTemplate resolveSelectedTemplate(String selectedCode, List<EmailTemplate> filteredTemplates) {
        if (filteredTemplates == null || filteredTemplates.isEmpty()) {
            return null;
        }

        if (selectedCode != null && !selectedCode.isBlank()) {
            for (EmailTemplate emailTemplate : filteredTemplates) {
                if (selectedCode.equals(emailTemplate.getTemplateCode())) {
                    return emailTemplate;
                }
            }
        }

        return filteredTemplates.get(0);
    }

    private void enrichTemplate(EmailTemplate emailTemplate) {
        if (emailTemplate == null) {
            return;
        }

        emailTemplate.setPlaceholders(extractPlaceholders(emailTemplate));
        emailTemplate.setHtmlTemplate(isHtmlTemplate(emailTemplate.getBodyTemplate()));
        emailTemplate.setPreviewSubject(mergeTemplate(emailTemplate.getSubjectTemplate(), buildSampleValues()));
        emailTemplate.setPreviewBodyHtml(buildPreviewBodyHtml(emailTemplate.getBodyTemplate(), emailTemplate.isHtmlTemplate()));
        emailTemplate.setBodyTemplateEscaped(escapeHtml(safeValue(emailTemplate.getBodyTemplate())));
        emailTemplate.setUpdatedAtDisplay(formatTimestamp(emailTemplate.getUpdatedAt()));
    }

    private List<String> extractPlaceholders(EmailTemplate emailTemplate) {
        Set<String> placeholderSet = new LinkedHashSet<>();
        capturePlaceholders(emailTemplate == null ? null : emailTemplate.getSubjectTemplate(), placeholderSet);
        capturePlaceholders(emailTemplate == null ? null : emailTemplate.getBodyTemplate(), placeholderSet);
        return new ArrayList<>(placeholderSet);
    }

    private void capturePlaceholders(String templateText, Set<String> placeholderSet) {
        if (templateText == null || templateText.isBlank()) {
            return;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(templateText);
        while (matcher.find()) {
            placeholderSet.add(matcher.group(1));
        }
    }

    private boolean isHtmlTemplate(String bodyTemplate) {
        if (bodyTemplate == null || bodyTemplate.isBlank()) {
            return false;
        }

        return HTML_TAG_PATTERN.matcher(bodyTemplate).find();
    }

    private String buildPreviewBodyHtml(String bodyTemplate, boolean htmlTemplate) {
        String mergedBody = mergeTemplate(bodyTemplate, buildSampleValues());

        if (htmlTemplate) {
            return mergedBody;
        }

        return escapeHtml(mergedBody).replace("\r\n", "\n").replace("\n", "<br/>");
    }

    private String mergeTemplate(String templateText, Map<String, String> placeholderMap) {
        String mergedText = safeValue(templateText);

        for (Map.Entry<String, String> entry : placeholderMap.entrySet()) {
            mergedText = mergedText.replace("{" + entry.getKey() + "}", safeValue(entry.getValue()));
        }

        return mergedText;
    }

    private Map<String, String> buildSampleValues() {
        Map<String, String> sampleValues = new LinkedHashMap<>();
        sampleValues.put("userName", "Brandon Lim");
        sampleValues.put("userEmail", "student@example.com");
        sampleValues.put("roleName", "Course Administrator");
        sampleValues.put("studentName", "Suy Tseng Hei");
        sampleValues.put("studentId", "TP067009");
        sampleValues.put("programName", "DIT - Diploma in IT");
        sampleValues.put("yearName", "Year 2");
        sampleValues.put("semesterName", "Semester 1");
        sampleValues.put("cgpa", "3.33");
        sampleValues.put("failedCourseCount", "2");
        sampleValues.put("nextLevel", "Year 2 / Semester 2");
        sampleValues.put("failedCourseList", listToText(Arrays.asList("Database Systems", "Computer Networks")));
        sampleValues.put("eligibilityReason", "CGPA is below the minimum progression threshold.");
        sampleValues.put("courseName", "Database Systems");
        sampleValues.put("attemptNo", "1");
        sampleValues.put("failedComponentList", listToText(Arrays.asList("Assignment 1", "Final Exam")));
        sampleValues.put("planStartDate", "2026-03-20");
        sampleValues.put("planEndDate", "2026-04-10");
        sampleValues.put("milestoneList", listToText(Arrays.asList("Review SQL joins and ERD corrections — 5 days", "Redo lab practice set — 3 days")));
        sampleValues.put("reportYear", "Year 2");
        sampleValues.put("reportSemester", "Semester 1");
        sampleValues.put("semesterGpa", "3.33");
        sampleValues.put("intakeName", "May 2025");
        sampleValues.put("otpCode", "428613");
        sampleValues.put("otpExpiry", "22 Mar 2026, 05:30 AM");
        sampleValues.put("courseRowsHtml", buildCourseRowsHtml());
        sampleValues.put("courseCardsHtml", buildCourseCardsHtml());
        return sampleValues;
    }

    private String buildCourseRowsHtml() {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append(buildRow("DBS2102", "Database Systems", "3", "B+", "3.33"));
        htmlBuilder.append(buildRow("NET2101", "Computer Networks", "3", "A-", "3.67"));
        htmlBuilder.append(buildRow("WAD2104", "Web Application Development", "3", "B", "3.00"));
        return htmlBuilder.toString();
    }

    private String buildRow(String code, String course, String creditHour, String grade, String gradePoint) {
        return "<tr>" +
                "<td style=\"padding:14px 16px;border:1px solid #cbd5e1;font-size:13px;color:#0f172a;\">" + escapeHtml(code) + "</td>" +
                "<td style=\"padding:14px 16px;border:1px solid #cbd5e1;font-size:13px;color:#0f172a;\">" + escapeHtml(course) + "</td>" +
                "<td style=\"padding:14px 16px;border:1px solid #cbd5e1;font-size:13px;color:#0f172a;text-align:center;\">" + escapeHtml(creditHour) + "</td>" +
                "<td style=\"padding:14px 16px;border:1px solid #cbd5e1;font-size:13px;color:#0f172a;text-align:center;\">" + escapeHtml(grade) + "</td>" +
                "<td style=\"padding:14px 16px;border:1px solid #cbd5e1;font-size:13px;color:#0f172a;text-align:center;\">" + escapeHtml(gradePoint) + "</td>" +
                "</tr>";
    }

    private String buildCourseCardsHtml() {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append(buildCourseCard("DBS2102", "Database Systems", "3", "B+", "3.33"));
        htmlBuilder.append(buildCourseCard("NET2101", "Computer Networks", "3", "A-", "3.67"));
        htmlBuilder.append(buildCourseCard("WAD2104", "Web Application Development", "3", "B", "3.00"));
        return htmlBuilder.toString();
    }

    private String buildCourseCard(String code, String course, String creditHour, String grade, String gradePoint) {
        return "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse:collapse;margin:0 0 12px;\">" +
                "<tr><td style=\"padding:14px 16px;border:1px solid #dbe5f0;border-radius:14px;background:#f8fafc;\">" +
                "<div style=\"font-size:16px;font-weight:700;color:#0f172a;\">" + escapeHtml(code) + "</div>" +
                "<div style=\"margin-top:4px;font-size:14px;color:#334155;\">" + escapeHtml(course) + "</div>" +
                "<div style=\"margin-top:10px;font-size:13px;line-height:1.8;color:#475569;\">" +
                "Credit Hour: <strong>" + escapeHtml(creditHour) + "</strong><br/>" +
                "Grade: <strong>" + escapeHtml(grade) + "</strong><br/>" +
                "Grade Point: <strong>" + escapeHtml(gradePoint) + "</strong>" +
                "</div></td></tr></table>";
    }

    private String listToText(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "-";
        }

        StringBuilder textBuilder = new StringBuilder();
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                textBuilder.append("\n");
            }
            textBuilder.append("- ").append(values.get(index));
        }
        return textBuilder.toString();
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "System default";
        }
        return DISPLAY_FORMAT.format(timestamp);
    }

    private String safeValue(String value) {
        return value == null ? "" : value;
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
