package com.crs.controller;

import com.crs.dao.OTPDAO;
import com.crs.dao.UserDAO;
import com.crs.ejb.NotificationService;
import com.crs.model.User;
import com.crs.util.OTPUtil;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter OTP_EXPIRY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    @EJB
    private UserDAO userDAO;

    @EJB
    private OTPDAO otpDAO;

    @EJB
    private NotificationService notificationService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        User user = userDAO.findByEmail(email);

        if (user == null) {
            request.setAttribute("errorMessage", "Email not found.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        String otpCode = OTPUtil.generateOTP();
        otpDAO.saveOTP(user.getUserId(), otpCode);

        String otpExpiry = LocalDateTime.now().plusMinutes(5).format(OTP_EXPIRY_FORMATTER);
        boolean emailSent = notificationService.sendPasswordResetOtp(user, otpCode, otpExpiry);

        if (!emailSent) {
            request.setAttribute("errorMessage", "Failed to send OTP email.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        request.setAttribute("successMessage", "OTP has been sent to your email.");
        request.setAttribute("resetEmail", email);
        request.getRequestDispatcher("verifyOtp.jsp").forward(request, response);
    }
}
