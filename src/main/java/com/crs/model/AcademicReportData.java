package com.crs.model;

import java.io.Serializable;
import java.util.List;

public class AcademicReportData implements Serializable {
    private static final long serialVersionUID = 1L;

    private AcademicReportStudent student;
    private String reportYear;
    private String reportSemester;
    private double semesterGpa;
    private double cgpa;
    private List<AcademicReportCourseRow> courseRows;

    public AcademicReportStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicReportStudent student) {
        this.student = student;
    }

    public String getReportYear() {
        return reportYear;
    }

    public void setReportYear(String reportYear) {
        this.reportYear = reportYear;
    }

    public String getReportSemester() {
        return reportSemester;
    }

    public void setReportSemester(String reportSemester) {
        this.reportSemester = reportSemester;
    }

    public double getSemesterGpa() {
        return semesterGpa;
    }

    public void setSemesterGpa(double semesterGpa) {
        this.semesterGpa = semesterGpa;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public List<AcademicReportCourseRow> getCourseRows() {
        return courseRows;
    }

    public void setCourseRows(List<AcademicReportCourseRow> courseRows) {
        this.courseRows = courseRows;
    }
}
