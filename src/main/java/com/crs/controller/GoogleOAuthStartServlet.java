package com.crs.controller;

import com.crs.util.GoogleOAuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/google-auth-start")
public class GoogleOAuthStartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String authUrl = GoogleOAuthUtil.buildAuthorizationUrl();
        response.sendRedirect(authUrl);
    }
}