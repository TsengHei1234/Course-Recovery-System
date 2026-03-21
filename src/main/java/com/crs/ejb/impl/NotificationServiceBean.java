package com.crs.ejb.impl;

import com.crs.dao.EmailTemplateDAO;
import com.crs.ejb.NotificationService;
import com.crs.model.AcademicReportData;
import com.crs.model.EmailTemplate;
import com.crs.model.User;
import com.crs.util.GmailApiUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

@Stateless
public class NotificationServiceBean implements NotificationService {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    @EJB
    private EmailTemplateDAO emailTemplateDAO;

    @Override
    public boolean sendPasswordResetOtp(User user, String otpCode, String otpExpiry) {
        Map<String, String> placeholderMap = new LinkedHashMap<>();
        placeholderMap.put("userName", safeValue(user == null ? null : user.getName()));
        placeholderMap.put("userEmail", safeValue(user == null ? null : user.getEmail()));
        placeholderMap.put("otpCode", safeValue(otpCode));
        placeholderMap.put("otpExpiry", safeValue(otpExpiry));

        return sendUsingTemplate(
                user == null ? null : user.getEmail(),
                "PASSWORD_RESET_OTP",
                placeholderMap
        );
    }

    @Override
    public boolean sendPasswordResetConfirmation(User user) {
        Map<String, String> placeholderMap = new LinkedHashMap<>();
        placeholderMap.put("userName", safeValue(user == null ? null : user.getName()));
        placeholderMap.put("userEmail", safeValue(user == null ? null : user.getEmail()));

        return sendUsingTemplate(
                user == null ? null : user.getEmail(),
                "PASSWORD_RESET_CONFIRMATION",
                placeholderMap
        );
    }

    @Override
    public boolean sendAcademicReport(AcademicReportData reportData) {
        if (reportData == null || reportData.getStudent() == null) {
            return false;
        }

        Map<String, String> placeholderMap = new LinkedHashMap<>();
        placeholderMap.put("studentName", safeValue(reportData.getStudent().getStudentName()));
        placeholderMap.put("studentId", safeValue(reportData.getStudent().getStudentId()));
        placeholderMap.put("programName", safeValue(reportData.getStudent().getProgramCode() + " - " + reportData.getStudent().getProgramName()));
        placeholderMap.put("reportYear", safeValue(reportData.getReportYear()));
        placeholderMap.put("reportSemester", safeValue(reportData.getReportSemester()));
        placeholderMap.put("semesterGpa", DECIMAL_FORMAT.format(reportData.getSemesterGpa()));
        placeholderMap.put("cgpa", DECIMAL_FORMAT.format(reportData.getCgpa()));

        return sendUsingTemplate(
                reportData.getStudent().getEmail(),
                "ACADEMIC_REPORT_SENT",
                placeholderMap
        );
    }

    private boolean sendUsingTemplate(String toEmail, String templateCode, Map<String, String> placeholderMap) {
        if (toEmail == null || toEmail.isBlank()) {
            return false;
        }

        EmailTemplate emailTemplate = emailTemplateDAO.findActiveByCode(templateCode);
        if (emailTemplate == null) {
            return false;
        }

        String mergedSubject = mergeTemplate(emailTemplate.getSubjectTemplate(), placeholderMap);
        String mergedBody = mergeTemplate(emailTemplate.getBodyTemplate(), placeholderMap);

        String sendResult = GmailApiUtil.sendPlainTextEmail(toEmail, mergedSubject, mergedBody);
        return "SUCCESS".equals(sendResult);
    }

    private String mergeTemplate(String templateText, Map<String, String> placeholderMap) {
        String mergedText = templateText == null ? "" : templateText;

        for (Map.Entry<String, String> entry : placeholderMap.entrySet()) {
            mergedText = mergedText.replace("{" + entry.getKey() + "}", safeValue(entry.getValue()));
        }

        return mergedText;
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
