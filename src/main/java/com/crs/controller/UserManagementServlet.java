package com.crs.controller;

import com.crs.model.User;
import com.crs.ejb.UserManagementService;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/user-management")
public class UserManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private UserManagementService userManagementService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	List<User> userList = userManagementService.getAllUsers();

        request.setAttribute("userList", userList);
        request.setAttribute("pageTitle", "User Management");
        request.setAttribute("pageSubtitle", "Manage user accounts, roles, and account status.");
        request.setAttribute("currentPage", "users");

        request.getRequestDispatcher("userManagement.jsp").forward(request, response);
    }
}