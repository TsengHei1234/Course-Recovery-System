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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/eligibility")
public class EligibilityEnrolmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private EligibilityService eligibilityService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String programme = trim(request.getParameter("programme"));
        String intake = trim(request.getParameter("intake"));
        String yearName = trim(request.getParameter("yearName"));
        String semesterName = trim(request.getParameter("semesterName"));
        String search = trim(request.getParameter("search"));

        List<EligibilityRecord> awaitingList =
                eligibilityService.getAwaitingCheckRecords(programme, intake, yearName, semesterName, search);
        List<EligibilityRecord> pendingApprovalList =
                eligibilityService.getPendingApprovalRecords(programme, intake, yearName, semesterName, search);
        List<EligibilityRecord> recoveryQueueList =
                eligibilityService.getRecoveryQueueRecords(programme, intake, yearName, semesterName, search);
        List<EligibilityRecord> processedList =
                eligibilityService.getProcessedRecords(programme, intake, yearName, semesterName, search);

        request.setAttribute("awaitingList", awaitingList);
        request.setAttribute("pendingApprovalList", pendingApprovalList);
        request.setAttribute("recoveryQueueList", recoveryQueueList);
        request.setAttribute("processedList", processedList);

        request.setAttribute("programmeOptions", eligibilityService.getProgrammeOptions());
        request.setAttribute("intakeOptions", eligibilityService.getIntakeOptions());
        request.setAttribute("yearOptions", eligibilityService.getYearOptions());
        request.setAttribute("semesterOptions", eligibilityService.getSemesterOptions());

        request.setAttribute("filterProgramme", programme);
        request.setAttribute("filterIntake", intake);
        request.setAttribute("filterYearName", yearName);
        request.setAttribute("filterSemesterName", semesterName);
        request.setAttribute("filterSearch", search);

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
        Integer userId = (Integer) request.getSession().getAttribute("userId");

        String programme = trim(request.getParameter("programme"));
        String intake = trim(request.getParameter("intake"));
        String yearName = trim(request.getParameter("yearName"));
        String semesterName = trim(request.getParameter("semesterName"));
        String search = trim(request.getParameter("search"));

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if ("checkAll".equals(action)) {
            eligibilityService.checkAllEligibility(userId, programme, intake, yearName, semesterName, search);

            request.getSession().setAttribute("successMessage", "Eligibility check completed successfully.");
            response.sendRedirect(buildRedirectUrl(programme, intake, yearName, semesterName, search));
            return;
        }

        if ("approveOne".equals(action)) {
            String progressionIdStr = request.getParameter("progressionId");

            if (progressionIdStr != null && !progressionIdStr.isBlank()) {
                int progressionId = Integer.parseInt(progressionIdStr);
                eligibilityService.approveEnrolment(progressionId, userId);

                request.getSession().setAttribute("successMessage", "Enrolment approved successfully.");
            }

            response.sendRedirect(buildRedirectUrl(programme, intake, yearName, semesterName, search));
            return;
        }

        if ("approveSelected".equals(action)) {
            List<Integer> ids = parseIds(request.getParameter("progressionIds"));
            if (!ids.isEmpty()) {
                eligibilityService.approveSelected(ids, userId);

                request.getSession().setAttribute("successMessage", "Selected enrolments approved successfully.");
            }

            response.sendRedirect(buildRedirectUrl(programme, intake, yearName, semesterName, search));
            return;
        }

        if ("sendOne".equals(action)) {
            String progressionIdStr = request.getParameter("progressionId");

            if (progressionIdStr != null && !progressionIdStr.isBlank()) {
                int progressionId = Integer.parseInt(progressionIdStr);
                eligibilityService.sendToRecovery(progressionId, userId);

                request.getSession().setAttribute("successMessage", "Student sent to recovery successfully.");
            }

            response.sendRedirect(buildRedirectUrl(programme, intake, yearName, semesterName, search));
            return;
        }

        if ("sendSelected".equals(action)) {
            List<Integer> ids = parseIds(request.getParameter("progressionIds"));
            if (!ids.isEmpty()) {
                eligibilityService.sendSelectedToRecovery(ids, userId);

                request.getSession().setAttribute("successMessage", "Selected students sent to recovery successfully.");
            }

            response.sendRedirect(buildRedirectUrl(programme, intake, yearName, semesterName, search));
            return;
        }

        doGet(request, response);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private List<Integer> parseIds(String progressionIds) {
        List<Integer> ids = new ArrayList<>();

        if (progressionIds == null || progressionIds.isBlank()) {
            return ids;
        }

        String[] parts = progressionIds.split(",");
        for (String part : parts) {
            String p = part.trim();
            if (!p.isEmpty()) {
                ids.add(Integer.parseInt(p));
            }
        }

        return ids;
    }

    private String buildRedirectUrl(String programme, String intake, String yearName, String semesterName, String search) {
        StringBuilder url = new StringBuilder("eligibility");
        boolean first = true;

        first = appendParam(url, "programme", programme, first);
        first = appendParam(url, "intake", intake, first);
        first = appendParam(url, "yearName", yearName, first);
        first = appendParam(url, "semesterName", semesterName, first);
        appendParam(url, "search", search, first);

        return url.toString();
    }

    private boolean appendParam(StringBuilder url, String key, String value, boolean first) {
        if (value != null && !value.isBlank()) {
            url.append(first ? "?" : "&")
               .append(key)
               .append("=")
               .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            return false;
        }
        return first;
    }
}