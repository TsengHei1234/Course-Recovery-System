package com.crs.controller;

import com.crs.ejb.StudentService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/students")
public class StudentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private StudentService studentService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String programmeCode = trimToNull(request.getParameter("programmeCode"));
        Integer intakeId = parseInteger(request.getParameter("intakeId"));
        Integer yearId = parseInteger(request.getParameter("yearId"));
        Integer semesterId = parseInteger(request.getParameter("semesterId"));
        String studentSearch = trimToNull(request.getParameter("studentSearch"));

        request.setAttribute("studentList", studentService.getStudents(
                programmeCode,
                intakeId,
                yearId,
                semesterId,
                studentSearch
        ));
        request.setAttribute("programmeOptions", studentService.getProgrammeOptions());
        request.setAttribute("intakeOptions", studentService.getAllIntakes());
        request.setAttribute("yearOptions", studentService.getAllYears());
        request.setAttribute("semesterOptions", studentService.getAllSemesters());

        request.setAttribute("programmeCode", valueOrEmpty(programmeCode));
        request.setAttribute("intakeId", intakeId);
        request.setAttribute("yearId", yearId);
        request.setAttribute("semesterId", semesterId);
        request.setAttribute("studentSearch", valueOrEmpty(studentSearch));

        request.setAttribute("pageTitle", "Students");
        request.setAttribute("breadcrumb1", "Reference");
        request.setAttribute("breadcrumb2", "Students");
        request.setAttribute("currentPage", "students");

        request.getRequestDispatcher("students.jsp").forward(request, response);
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
