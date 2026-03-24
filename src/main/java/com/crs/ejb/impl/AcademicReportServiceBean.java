package com.crs.ejb.impl;

import com.crs.dao.AcademicReportDAO;
import com.crs.ejb.AcademicReportService;
import com.crs.model.AcademicReportData;
import com.crs.model.AcademicReportStudent;
import com.crs.model.SelectionOption;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class AcademicReportServiceBean implements AcademicReportService {

    @EJB
    private AcademicReportDAO academicReportDAO;

    @Override
    public List<SelectionOption> getProgrammeOptions() {
        return academicReportDAO.findProgrammeOptions();
    }

    @Override
    public List<SelectionOption> getIntakeOptions() {
        return academicReportDAO.findIntakeOptions();
    }

    @Override
    public List<SelectionOption> getYearOptions() {
        return academicReportDAO.findYearOptions();
    }

    @Override
    public List<SelectionOption> getSemesterOptions() {
        return academicReportDAO.findSemesterOptions();
    }

    @Override
    public List<AcademicReportStudent> getStudents(String programmeCode, Integer intakeId, Integer yearId, Integer semesterId, String studentSearch) {
        return academicReportDAO.findStudents(programmeCode, intakeId, yearId, semesterId, studentSearch);
    }

    @Override
    public AcademicReportStudent getStudentById(String studentId) {
        return academicReportDAO.findStudentById(studentId);
    }

    @Override
    public AcademicReportData generateReport(String studentId, Integer yearId, Integer semesterId) {
        AcademicReportStudent selectedStudent = academicReportDAO.findStudentById(studentId);

        if (selectedStudent == null) {
            return null;
        }

        int resolvedYearId = yearId != null ? yearId : selectedStudent.getYearId();
        int resolvedSemesterId = semesterId != null ? semesterId : selectedStudent.getSemesterId();

        AcademicReportData reportData = new AcademicReportData();
        reportData.setStudent(selectedStudent);
        reportData.setReportYear(academicReportDAO.findYearNameById(resolvedYearId));
        reportData.setReportSemester(academicReportDAO.findSemesterNameById(resolvedSemesterId));
        reportData.setSemesterGpa(academicReportDAO.findSemesterGpa(studentId, resolvedYearId, resolvedSemesterId));
        reportData.setCgpa(academicReportDAO.findCgpa(studentId));
        reportData.setCourseRows(academicReportDAO.findLatestTermCourseRows(studentId, resolvedYearId, resolvedSemesterId));

        return reportData;
    }
}
