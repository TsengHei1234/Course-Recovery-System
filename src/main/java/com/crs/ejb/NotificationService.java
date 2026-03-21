package com.crs.ejb;

import com.crs.model.AcademicReportData;
import com.crs.model.User;

public interface NotificationService {
    boolean sendPasswordResetOtp(User user, String otpCode, String otpExpiry);
    boolean sendPasswordResetConfirmation(User user);
    boolean sendAcademicReport(AcademicReportData reportData);
}
