package com.crs.controller;

import com.crs.ejb.ProgramCourseService;
import com.crs.model.StudentCurriculumFocus;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/programs-courses")
public class ProgramCoursesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private ProgramCourseService programCourseService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String programmeSearch = trimToNull(request.getParameter("programmeSearch"));
        Integer programmeIntakeId = parseInteger(request.getParameter("programmeIntakeId"));

        String courseSearch = trimToNull(request.getParameter("courseSearch"));

        String structureProgramCode = trimToNull(request.getParameter("structureProgramCode"));
        Integer structureIntakeId = parseInteger(request.getParameter("structureIntakeId"));
        Integer structureYearId = parseInteger(request.getParameter("structureYearId"));
        Integer structureSemesterId = parseInteger(request.getParameter("structureSemesterId"));
        String structureCourseSearch = trimToNull(request.getParameter("structureCourseSearch"));

        String focusStudentId = trimToNull(request.getParameter("studentId"));
        StudentCurriculumFocus focusStudent = null;

        if (focusStudentId != null) {
            focusStudent = programCourseService.getStudentCurriculumFocus(focusStudentId);

            if (focusStudent != null) {
                if (structureProgramCode == null) {
                    structureProgramCode = focusStudent.getProgramCode();
                }
                if (structureIntakeId == null) {
                    structureIntakeId = focusStudent.getIntakeId();
                }
                if (structureYearId == null) {
                    structureYearId = focusStudent.getYearId();
                }
                if (structureSemesterId == null) {
                    structureSemesterId = focusStudent.getSemesterId();
                }
            }
        }

        request.setAttribute("programmeList", programCourseService.getProgrammes(programmeIntakeId, programmeSearch));
        request.setAttribute("courseList", programCourseService.getCourses(courseSearch));
        request.setAttribute("structureList", programCourseService.getProgrammeStructures(
                structureProgramCode,
                structureIntakeId,
                structureYearId,
                structureSemesterId,
                structureCourseSearch
        ));

        request.setAttribute("programmeOptions", programCourseService.getProgrammeOptions());
        request.setAttribute("intakeOptions", programCourseService.getAllIntakes());
        request.setAttribute("yearOptions", programCourseService.getAllYears());
        request.setAttribute("semesterOptions", programCourseService.getAllSemesters());
        request.setAttribute("focusStudent", focusStudent);

        request.setAttribute("programmeSearch", valueOrEmpty(programmeSearch));
        request.setAttribute("programmeIntakeId", programmeIntakeId);
        request.setAttribute("courseSearch", valueOrEmpty(courseSearch));
        request.setAttribute("structureProgramCode", valueOrEmpty(structureProgramCode));
        request.setAttribute("structureIntakeId", structureIntakeId);
        request.setAttribute("structureYearId", structureYearId);
        request.setAttribute("structureSemesterId", structureSemesterId);
        request.setAttribute("structureCourseSearch", valueOrEmpty(structureCourseSearch));
        request.setAttribute("focusStudentId", valueOrEmpty(focusStudentId));

        request.setAttribute("pageTitle", "Programs & Courses");
        request.setAttribute("breadcrumb1", "Reference");
        request.setAttribute("breadcrumb2", "Programs & Courses");
        request.setAttribute("currentPage", "courses");

        request.getRequestDispatcher("programs-courses.jsp").forward(request, response);
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
