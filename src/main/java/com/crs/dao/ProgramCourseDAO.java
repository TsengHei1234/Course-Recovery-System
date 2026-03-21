package com.crs.dao;

import com.crs.model.CourseRecord;
import com.crs.model.ProgrammeRecord;
import com.crs.model.ProgrammeStructureGroup;
import com.crs.model.ReferenceOption;
import com.crs.model.StudentCurriculumFocus;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface ProgramCourseDAO {
    List<ProgrammeRecord> findProgrammes(Integer intakeId, String searchTerm);
    List<ProgrammeRecord> findProgrammeOptions();
    List<CourseRecord> findCourses(String searchTerm);
    List<ProgrammeStructureGroup> findProgrammeStructures(String programCode,
                                                          Integer intakeId,
                                                          Integer yearId,
                                                          Integer semesterId,
                                                          String courseSearch);
    List<ReferenceOption> findAllIntakes();
    List<ReferenceOption> findAllYears();
    List<ReferenceOption> findAllSemesters();
    StudentCurriculumFocus findStudentCurriculumFocus(String studentId);
}
