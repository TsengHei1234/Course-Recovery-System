package com.crs.controller;

import com.crs.dao.UserDAO;
import com.crs.ejb.NotificationService;
import com.crs.model.User;
import com.crs.util.PasswordUtil;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private UserDAO userDAO;

    @EJB
    private NotificationService notificationService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer resetUserId = (Integer) session.getAttribute("resetUserId");

        if (resetUserId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null || newPassword.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Both password fields are required.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        userDAO.updatePassword(resetUserId, PasswordUtil.hashPassword(newPassword));
        User resetUser = userDAO.findById(resetUserId);
        boolean emailSent = notificationService.sendPasswordResetConfirmation(resetUser);

        session.removeAttribute("resetUserId");

        if (emailSent) {
            request.setAttribute("successMessage", "Password has been reset successfully. Please login again.");
        } else {
            request.setAttribute("successMessage", "Password has been reset successfully, but the confirmation email could not be sent. Please login again.");
        }
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
