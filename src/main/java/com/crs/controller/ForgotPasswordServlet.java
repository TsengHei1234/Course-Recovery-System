package com.crs.controller;

import com.crs.dao.UserDAO;
import com.crs.util.GmailApiUtil;
import com.crs.dao.OTPDAO;
import com.crs.model.User;
import com.crs.util.OTPUtil;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    @EJB
    private UserDAO userDAO;

    @EJB
    private OTPDAO otpDAO;

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

        String otp = OTPUtil.generateOTP();

        otpDAO.saveOTP(user.getUserId(), otp);

        String subject = "CRS Password Reset OTP";
        String body = "Your OTP code is: " + otp + "\nThis code will expire in 5 minutes.";

        String sendResult = GmailApiUtil.sendOtpEmail(email, subject, body);
        System.out.println("OTP email result: " + sendResult);

        if (!"SUCCESS".equals(sendResult)) {
            request.setAttribute("errorMessage", "Failed to send OTP email.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        request.setAttribute("successMessage", "OTP has been sent to your email.");
        request.setAttribute("resetEmail", email);
        request.getRequestDispatcher("verifyOtp.jsp").forward(request, response);
    }
}