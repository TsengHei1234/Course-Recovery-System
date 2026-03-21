package com.crs.controller;

import com.crs.ejb.AcademicReportService;
import com.crs.ejb.NotificationService;
import com.crs.model.AcademicReportData;
import com.crs.model.AcademicReportStudent;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/academic-reports")
public class AcademicReportsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private AcademicReportService academicReportService;

    @EJB
    private NotificationService notificationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        loadPage(request);
        request.getRequestDispatcher("academic-reports.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reportAction = trimToNull(request.getParameter("reportAction"));
        String selectedStudentId = trimToNull(request.getParameter("selectedStudentId"));
        Integer yearId = parseInteger(request.getParameter("yearId"));
        Integer semesterId = parseInteger(request.getParameter("semesterId"));
        String reportReady = trimToNull(request.getParameter("reportReady"));

        if (selectedStudentId == null) {
            request.setAttribute("toastType", "info");
            request.setAttribute("toastMessage", "Select a student first from the filtered list.");
            loadPage(request);
            request.getRequestDispatcher("academic-reports.jsp").forward(request, response);
            return;
        }

        AcademicReportData reportData = null;

        if ("generate".equalsIgnoreCase(reportAction)) {
            reportData = academicReportService.generateReport(selectedStudentId, yearId, semesterId);
            if (reportData == null || reportData.getStudent() == null) {
                request.setAttribute("toastType", "error");
                request.setAttribute("toastMessage", "Unable to generate the academic report for the selected student.");
            } else {
                request.setAttribute("generatedReport", reportData);
                request.setAttribute("toastType", "success");
                request.setAttribute("toastMessage", "Academic report generated for " + reportData.getStudent().getStudentName() + ".");
            }
        } else if ("email".equalsIgnoreCase(reportAction)) {
            if (!"true".equalsIgnoreCase(reportReady)) {
                request.setAttribute("toastType", "info");
                request.setAttribute("toastMessage", "Generate the academic report first before emailing it.");
            } else {
                reportData = academicReportService.generateReport(selectedStudentId, yearId, semesterId);
                if (reportData == null || reportData.getStudent() == null) {
                    request.setAttribute("toastType", "error");
                    request.setAttribute("toastMessage", "Unable to prepare the academic report email.");
                } else if (notificationService.sendAcademicReport(reportData)) {
                    request.setAttribute("generatedReport", reportData);
                    request.setAttribute("toastType", "success");
                    request.setAttribute("toastMessage", "Academic report emailed successfully to " + reportData.getStudent().getStudentName() + ".");
                } else {
                    request.setAttribute("generatedReport", reportData);
                    request.setAttribute("toastType", "error");
                    request.setAttribute("toastMessage", "Failed to send the academic report email.");
                }
            }
        }

        loadPage(request);
        request.getRequestDispatcher("academic-reports.jsp").forward(request, response);
    }

    private void loadPage(HttpServletRequest request) {
        String programmeCode = trimToNull(request.getParameter("programmeCode"));
        Integer intakeId = parseInteger(request.getParameter("intakeId"));
        Integer yearId = parseInteger(request.getParameter("yearId"));
        Integer semesterId = parseInteger(request.getParameter("semesterId"));
        String studentSearch = trimToNull(request.getParameter("studentSearch"));
        String selectedStudentId = trimToNull(request.getParameter("selectedStudentId"));

        request.setAttribute("programmeOptions", academicReportService.getProgrammeOptions());
        request.setAttribute("intakeOptions", academicReportService.getIntakeOptions());
        request.setAttribute("yearOptions", academicReportService.getYearOptions());
        request.setAttribute("semesterOptions", academicReportService.getSemesterOptions());
        request.setAttribute("candidateStudents", academicReportService.getStudents(
                programmeCode,
                intakeId,
                yearId,
                semesterId,
                studentSearch
        ));

        if (selectedStudentId != null) {
            AcademicReportStudent selectedStudent = academicReportService.getStudentById(selectedStudentId);
            request.setAttribute("selectedStudent", selectedStudent);
        }

        request.setAttribute("programmeCode", valueOrEmpty(programmeCode));
        request.setAttribute("intakeId", intakeId);
        request.setAttribute("yearId", yearId);
        request.setAttribute("semesterId", semesterId);
        request.setAttribute("studentSearch", valueOrEmpty(studentSearch));
        request.setAttribute("selectedStudentId", valueOrEmpty(selectedStudentId));

        request.setAttribute("pageTitle", "Academic Reports");
        request.setAttribute("breadcrumb1", "Reporting");
        request.setAttribute("breadcrumb2", "Academic Reports");
        request.setAttribute("currentPage", "reports");
    }

    private Integer parseInteger(String value) {
        try {
            return value == null || value.isBlank() ? null : Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
