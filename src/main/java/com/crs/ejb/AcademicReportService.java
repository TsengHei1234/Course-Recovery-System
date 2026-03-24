package com.crs.ejb;

import com.crs.model.AcademicReportData;
import com.crs.model.AcademicReportStudent;
import com.crs.model.SelectionOption;

import java.util.List;

public interface AcademicReportService {
    List<SelectionOption> getProgrammeOptions();
    List<SelectionOption> getIntakeOptions();
    List<SelectionOption> getYearOptions();
    List<SelectionOption> getSemesterOptions();
    List<AcademicReportStudent> getStudents(String programmeCode, Integer intakeId, Integer yearId, Integer semesterId, String studentSearch);
    AcademicReportStudent getStudentById(String studentId);
    AcademicReportData generateReport(String studentId, Integer yearId, Integer semesterId);
}
