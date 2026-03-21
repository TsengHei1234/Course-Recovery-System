package com.crs.dao;

import com.crs.model.AcademicReportCourseRow;
import com.crs.model.AcademicReportStudent;
import com.crs.model.SelectionOption;

import java.util.List;

public interface AcademicReportDAO {
    List<SelectionOption> findProgrammeOptions();
    List<SelectionOption> findIntakeOptions();
    List<SelectionOption> findYearOptions();
    List<SelectionOption> findSemesterOptions();
    List<AcademicReportStudent> findStudents(String programmeCode, Integer intakeId, Integer yearId, Integer semesterId, String studentSearch);
    AcademicReportStudent findStudentById(String studentId);
    List<AcademicReportCourseRow> findLatestTermCourseRows(String studentId, int yearId, int semesterId);
    double findSemesterGpa(String studentId, int yearId, int semesterId);
    double findCgpa(String studentId);
    String findYearNameById(int yearId);
    String findSemesterNameById(int semesterId);
}
