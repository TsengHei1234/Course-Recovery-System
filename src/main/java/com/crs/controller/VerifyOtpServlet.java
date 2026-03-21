package com.crs.controller;

import com.crs.dao.OTPDAO;
import com.crs.dao.UserDAO;
import com.crs.model.User;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/verify-otp")
public class VerifyOtpServlet extends HttpServlet {

    @EJB
    private UserDAO userDAO;

    @EJB
    private OTPDAO otpDAO;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String otp = request.getParameter("otp");

        if (email == null || email.trim().isEmpty() ||
            otp == null || otp.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Email and OTP are required.");
            request.setAttribute("resetEmail", email);
            request.getRequestDispatcher("verifyOtp.jsp").forward(request, response);
            return;
        }

        User user = userDAO.findByEmail(email);

        if (user == null) {
            request.setAttribute("errorMessage", "User not found.");
            request.getRequestDispatcher("verifyOtp.jsp").forward(request, response);
            return;
        }

        boolean valid = otpDAO.verifyOTP(user.getUserId(), otp);

        if (!valid) {
            request.setAttribute("errorMessage", "Invalid or expired OTP.");
            request.setAttribute("resetEmail", email);
            request.getRequestDispatcher("verifyOtp.jsp").forward(request, response);
            return;
        }

        // OTP verified successfully, delete it so it cannot be reused
        otpDAO.deleteOTP(user.getUserId(), otp);

        HttpSession session = request.getSession();
        session.setAttribute("resetUserId", user.getUserId());

        response.sendRedirect("resetPassword.jsp");
    }
}