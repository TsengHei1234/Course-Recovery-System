package com.crs.controller;

import com.crs.ejb.EligibilityService;
import com.crs.model.EligibilityRecord;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/eligibility")
public class EligibilityEnrolmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private EligibilityService eligibilityService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<EligibilityRecord> awaitingList = eligibilityService.getAwaitingCheckRecords();
        List<EligibilityRecord> pendingApprovalList = eligibilityService.getPendingApprovalRecords();
        List<EligibilityRecord> recoveryQueueList = eligibilityService.getRecoveryQueueRecords();
        List<EligibilityRecord> processedList = eligibilityService.getProcessedRecords();

        request.setAttribute("awaitingList", awaitingList);
        request.setAttribute("pendingApprovalList", pendingApprovalList);
        request.setAttribute("recoveryQueueList", recoveryQueueList);
        request.setAttribute("processedList", processedList);

        request.setAttribute("pageTitle", "Eligibility & Enrolment");
        request.setAttribute("breadcrumb1", "Academic Management");
        request.setAttribute("breadcrumb2", "Eligibility & Enrolment");
        request.setAttribute("breadcrumb3", "");
        request.setAttribute("currentPage", "eligibility");

        request.getRequestDispatcher("/eligibility.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("checkAll".equals(action)) {
            Integer userId = (Integer) request.getSession().getAttribute("userId");

            if (userId != null) {
                eligibilityService.checkAllEligibility(userId);
            }

            response.sendRedirect("eligibility");
            return;
        }

        if ("approveOne".equals(action)) {
            Integer userId = (Integer) request.getSession().getAttribute("userId");
            String progressionIdStr = request.getParameter("progressionId");

            if (userId != null && progressionIdStr != null && !progressionIdStr.isBlank()) {
                int progressionId = Integer.parseInt(progressionIdStr);
                eligibilityService.approveEnrolment(progressionId, userId);
            }

            response.sendRedirect("eligibility");
            return;
        }

        doGet(request, response);
    }
}