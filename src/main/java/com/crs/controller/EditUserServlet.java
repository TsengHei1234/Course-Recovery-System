package com.crs.controller;

import com.crs.ejb.UserManagementService;
import com.crs.model.Role;
import com.crs.model.User;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/edit-user")
public class EditUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private UserManagementService userManagementService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.isBlank()) {
            response.sendRedirect("user-management");
            return;
        }

        int userId = Integer.parseInt(userIdStr);
        User user = userManagementService.getUserById(userId);
        List<Role> roleList = userManagementService.getAllRoles();

        if (user == null) {
            response.sendRedirect("user-management");
            return;
        }

        request.setAttribute("user", user);
        request.setAttribute("roleList", roleList);
        request.setAttribute("pageTitle", "Edit User");
        request.setAttribute("breadcrumb1", "Administration");
        request.setAttribute("breadcrumb2", "User Management");
        request.setAttribute("breadcrumb3", "Edit User");
        request.setAttribute("currentPage", "users");

        request.getRequestDispatcher("/editUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdStr = request.getParameter("userId");
        String roleIdStr = request.getParameter("roleId");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String status = request.getParameter("status");

        if (userIdStr == null || userIdStr.isBlank() ||
            roleIdStr == null || roleIdStr.isBlank() ||
            name == null || name.isBlank() ||
            email == null || email.isBlank() ||
            status == null || status.isBlank()) {

            request.setAttribute("errorMessage", "All fields are required.");
            doGet(request, response);
            return;
        }

        int userId = Integer.parseInt(userIdStr);

        User existingUser = userManagementService.getUserByEmail(email);
        if (existingUser != null && existingUser.getUserId() != userId) {
            request.setAttribute("errorMessage", "Email already exists.");
            doGet(request, response);
            return;
        }

        User user = new User();
        user.setUserId(userId);
        user.setRoleId(Integer.parseInt(roleIdStr));
        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setStatus(status.trim());

        userManagementService.updateUser(user);

        request.getSession().setAttribute("successMessage", "User updated successfully.");
        response.sendRedirect("user-management");
    }
}