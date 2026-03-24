package com.crs.ejb;

import com.crs.model.CourseRecord;
import com.crs.model.ProgrammeRecord;
import com.crs.model.ProgrammeStructureGroup;
import com.crs.model.ReferenceOption;
import com.crs.model.StudentCurriculumFocus;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface ProgramCourseService {
    List<ProgrammeRecord> getProgrammes(Integer intakeId, String searchTerm);
    List<ProgrammeRecord> getProgrammeOptions();
    List<CourseRecord> getCourses(String searchTerm);
    List<ProgrammeStructureGroup> getProgrammeStructures(String programCode,
                                                         Integer intakeId,
                                                         Integer yearId,
                                                         Integer semesterId,
                                                         String courseSearch);
    List<ReferenceOption> getAllIntakes();
    List<ReferenceOption> getAllYears();
    List<ReferenceOption> getAllSemesters();
    StudentCurriculumFocus getStudentCurriculumFocus(String studentId);
}
