package com.crs.controller;

import com.crs.ejb.RecoveryService;
import com.crs.model.RecoveryActiveRecord;
import com.crs.model.RecoveryPendingRecord;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/recovery-plans")
public class RecoveryPlansServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private RecoveryService recoveryService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roleName = (String) request.getSession().getAttribute("roleName");
        if (!"Academic Officer".equalsIgnoreCase(roleName)) {
            response.sendRedirect("user-management");
            return;
        }

        List<RecoveryPendingRecord> pendingList = recoveryService.getStudentsRequiringRecoveryAction();
        List<RecoveryActiveRecord> activeList = recoveryService.getStudentsWithExistingRecoveryAction();

        request.setAttribute("pendingList", pendingList);
        request.setAttribute("activeList", activeList);

        request.setAttribute("pageTitle", "Recovery Plans");
        request.setAttribute("breadcrumb1", "Academic Management");
        request.setAttribute("breadcrumb2", "Recovery Plans");
        request.setAttribute("breadcrumb3", "");
        request.setAttribute("currentPage", "recovery-hub");

        request.getRequestDispatcher("/recoveryPlans.jsp").forward(request, response);
    }
}