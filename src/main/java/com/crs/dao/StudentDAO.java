package com.crs.dao;

import com.crs.model.ProgrammeRecord;
import com.crs.model.ReferenceOption;
import com.crs.model.StudentDirectoryRecord;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface StudentDAO {
    List<StudentDirectoryRecord> findStudents(String programCode,
                                             Integer intakeId,
                                             Integer yearId,
                                             Integer semesterId,
                                             String searchTerm);

    List<ProgrammeRecord> findProgrammeOptions();

    List<ReferenceOption> findAllIntakes();

    List<ReferenceOption> findAllYears();

    List<ReferenceOption> findAllSemesters();

    void updateStudentTerm(String studentId, int yearId, int semesterId);

    String findStudentEmailByStudentId(String studentId);

    String findStudentNameByStudentId(String studentId);

    String findProgramNameByStudentId(String studentId);
}
