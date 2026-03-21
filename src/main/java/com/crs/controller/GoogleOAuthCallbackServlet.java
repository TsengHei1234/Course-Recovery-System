package com.crs.controller;

import com.crs.util.GoogleOAuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/oauth2callback")
public class GoogleOAuthCallbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = request.getParameter("code");
        String error = request.getParameter("error");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (error != null) {
            out.println("<h2>Google OAuth Error</h2>");
            out.println("<p>" + error + "</p>");
            return;
        }

        if (code == null || code.isEmpty()) {
            out.println("<h2>No authorization code received.</h2>");
            return;
        }

        String tokenResponse = GoogleOAuthUtil.exchangeCodeForTokenRaw(code);

        out.println("<h2>Authorization Code Received</h2>");
        out.println("<p><b>Code:</b> " + code + "</p>");

        out.println("<h2>Token Response</h2>");
        out.println("<pre>" + tokenResponse + "</pre>");
    }
}