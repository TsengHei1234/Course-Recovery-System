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

@WebServlet("/add-user")
public class AddUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private UserManagementService userManagementService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Role> roleList = userManagementService.getAllRoles();

        request.setAttribute("roleList", roleList);
        request.setAttribute("pageTitle", "Add User");
        request.setAttribute("breadcrumb1", "Administration");
        request.setAttribute("breadcrumb2", "User Management");
        request.setAttribute("breadcrumb3", "Add User");
        request.setAttribute("currentPage", "users");

        request.getRequestDispatcher("addUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roleIdStr = request.getParameter("roleId");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (roleIdStr == null || roleIdStr.isBlank() ||
            name == null || name.isBlank() ||
            email == null || email.isBlank() ||
            password == null || password.isBlank()) {

            request.setAttribute("errorMessage", "All fields are required.");
            doGet(request, response);
            return;
        }

        User existingUser = userManagementService.getUserByEmail(email);
        if (existingUser != null) {
            request.setAttribute("errorMessage", "Email already exists.");
            doGet(request, response);
            return;
        }

        User user = new User();
        user.setRoleId(Integer.parseInt(roleIdStr));
        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setPassword(password.trim()); // 先明文，后面再换 hash
        user.setStatus("ACTIVE");

        userManagementService.createUser(user);

        response.sendRedirect("user-management");
    }
}