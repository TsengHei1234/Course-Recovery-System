package com.crs.ejb.impl;

import com.crs.dao.EmailTemplateDAO;
import com.crs.ejb.NotificationService;
import com.crs.model.AcademicReportCourseRow;
import com.crs.model.AcademicReportData;
import com.crs.model.EmailTemplate;
import com.crs.model.User;
import com.crs.util.GmailApiUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class NotificationServiceBean implements NotificationService {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    @EJB
    private EmailTemplateDAO emailTemplateDAO;

    @Override
    public boolean sendPasswordResetOtp(User user, String otpCode, String otpExpiry) {
        Map<String, String> placeholderMap = new LinkedHashMap<>();
        placeholderMap.put("userName", safePlainValue(user == null ? null : user.getName()));
        placeholderMap.put("userEmail", safePlainValue(user == null ? null : user.getEmail()));
        placeholderMap.put("otpCode", safePlainValue(otpCode));
        placeholderMap.put("otpExpiry", safePlainValue(otpExpiry));

        return sendUsingTemplate(
                user == null ? null : user.getEmail(),
                "PASSWORD_RESET_OTP",
                placeholderMap,
                false
        );
    }

    @Override
    public boolean sendPasswordResetConfirmation(User user) {
        Map<String, String> placeholderMap = new LinkedHashMap<>();
        placeholderMap.put("userName", safePlainValue(user == null ? null : user.getName()));
        placeholderMap.put("userEmail", safePlainValue(user == null ? null : user.getEmail()));

        return sendUsingTemplate(
                user == null ? null : user.getEmail(),
                "PASSWORD_RESET_CONFIRMATION",
                placeholderMap,
                false
        );
    }

    @Override
    public boolean sendAcademicReport(AcademicReportData reportData) {
        if (reportData == null || reportData.getStudent() == null) {
            return false;
        }

        Map<String, String> placeholderMap = new LinkedHashMap<>();
        placeholderMap.put("studentName", escapeHtml(reportData.getStudent().getStudentName()));
        placeholderMap.put("studentId", escapeHtml(reportData.getStudent().getStudentId()));
        placeholderMap.put("programName", escapeHtml(reportData.getStudent().getProgramCode() + " - " + reportData.getStudent().getProgramName()));
        placeholderMap.put("intakeName", escapeHtml(reportData.getStudent().getIntakeName()));
        placeholderMap.put("reportYear", escapeHtml(reportData.getReportYear()));
        placeholderMap.put("reportSemester", escapeHtml(reportData.getReportSemester()));
        placeholderMap.put("semesterGpa", DECIMAL_FORMAT.format(reportData.getSemesterGpa()));
        placeholderMap.put("cgpa", DECIMAL_FORMAT.format(reportData.getCgpa()));
        placeholderMap.put("courseRowsHtml", buildCourseRowsHtml(reportData.getCourseRows()));

        return sendUsingTemplate(
                reportData.getStudent().getEmail(),
                "ACADEMIC_REPORT_SENT",
                placeholderMap,
                true
        );
    }

    private boolean sendUsingTemplate(String toEmail, String templateCode, Map<String, String> placeholderMap, boolean htmlEmail) {
        if (toEmail == null || toEmail.isBlank()) {
            return false;
        }

        EmailTemplate emailTemplate = emailTemplateDAO.findActiveByCode(templateCode);
        if (emailTemplate == null) {
            return false;
        }

        String mergedSubject = mergeTemplate(emailTemplate.getSubjectTemplate(), placeholderMap);
        String mergedBody = mergeTemplate(emailTemplate.getBodyTemplate(), placeholderMap);

        String sendResult = htmlEmail
                ? GmailApiUtil.sendHtmlEmail(toEmail, mergedSubject, mergedBody)
                : GmailApiUtil.sendPlainTextEmail(toEmail, mergedSubject, mergedBody);

        return "SUCCESS".equals(sendResult);
    }

    private String mergeTemplate(String templateText, Map<String, String> placeholderMap) {
        String mergedText = templateText == null ? "" : templateText;

        for (Map.Entry<String, String> entry : placeholderMap.entrySet()) {
            mergedText = mergedText.replace("{" + entry.getKey() + "}", entry.getValue() == null ? "-" : entry.getValue());
        }

        return mergedText;
    }

    private String buildCourseRowsHtml(List<AcademicReportCourseRow> courseRows) {
        if (courseRows == null || courseRows.isEmpty()) {
            return "<tr><td colspan=\"5\" style=\"padding:14px 16px;border:1px solid #e2e8f0;color:#64748b;text-align:center;\">No course records found.</td></tr>";
        }

        StringBuilder htmlBuilder = new StringBuilder();
        for (AcademicReportCourseRow row : courseRows) {
            htmlBuilder.append("<tr>")
                    .append("<td style=\"padding:14px 16px;border:1px solid #e2e8f0;\">").append(escapeHtml(row.getCourseCode())).append("</td>")
                    .append("<td style=\"padding:14px 16px;border:1px solid #e2e8f0;\">").append(escapeHtml(row.getCourseName())).append("</td>")
                    .append("<td style=\"padding:14px 16px;border:1px solid #e2e8f0;text-align:center;\">").append(row.getCreditHour()).append("</td>")
                    .append("<td style=\"padding:14px 16px;border:1px solid #e2e8f0;text-align:center;\">").append(escapeHtml(row.getGrade())).append("</td>")
                    .append("<td style=\"padding:14px 16px;border:1px solid #e2e8f0;text-align:center;\">").append(DECIMAL_FORMAT.format(row.getGradePoint())).append("</td>")
                    .append("</tr>");
        }
        return htmlBuilder.toString();
    }

    private String safePlainValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String escapeHtml(String value) {
        String safeValue = value == null || value.isBlank() ? "-" : value;
        return safeValue
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
