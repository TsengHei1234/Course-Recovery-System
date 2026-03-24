package com.crs.controller;

import com.crs.ejb.AuthService;
import com.crs.model.User;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private AuthService authService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Email and password are required.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        User user = authService.login(email.trim(), password);

        if (user == null) {
            request.setAttribute("errorMessage", "Invalid email or password, or account is inactive.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("roleName", user.getRoleName());
        session.setAttribute("loginUser", user); // optional

        String roleName = user.getRoleName();

//        if ("Course Administrator".equalsIgnoreCase(roleName)) {
//            response.sendRedirect("admin/dashboard");
//        } else if ("Academic Officer".equalsIgnoreCase(roleName)) {
//            response.sendRedirect("officer/dashboard");
//        } else {
//            session.invalidate();
//            request.setAttribute("errorMessage", "Unauthorized role.");
//            request.getRequestDispatcher("login.jsp").forward(request, response);
//        }

        
//        if ("Course Administrator".equalsIgnoreCase(roleName)) {
//            response.sendRedirect("adminDashboard.jsp");
//        } else if ("Academic Officer".equalsIgnoreCase(roleName)) {
//            response.sendRedirect("officerDashboard.jsp");
//        } else {
//            session.invalidate();
//            request.setAttribute("errorMessage", "Unauthorized role.");
//            request.getRequestDispatcher("login.jsp").forward(request, response);
//        }
        if ("Course Administrator".equalsIgnoreCase(roleName)) {
            response.sendRedirect("user-management");
        } else if ("Academic Officer".equalsIgnoreCase(roleName)) {
            response.sendRedirect("user-management");
        } else {
            session.invalidate();
            request.setAttribute("errorMessage", "Unauthorized role.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}