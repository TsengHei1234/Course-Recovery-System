package com.crs.controller;

import com.crs.ejb.UserManagementService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/user-status")
public class UserStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private UserManagementService userManagementService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdStr = request.getParameter("userId");
        String status = request.getParameter("status");

        if (userIdStr != null && !userIdStr.trim().isEmpty()
                && status != null && !status.trim().isEmpty()) {

            int userId = Integer.parseInt(userIdStr);
            userManagementService.updateUserStatus(userId, status);
        }

        response.sendRedirect("user-management");
    }
}