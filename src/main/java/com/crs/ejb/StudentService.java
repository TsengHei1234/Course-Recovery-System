package com.crs.ejb;

import com.crs.model.ProgrammeRecord;
import com.crs.model.ReferenceOption;
import com.crs.model.StudentDirectoryRecord;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface StudentService {
    List<StudentDirectoryRecord> getStudents(String programCode,
                                             Integer intakeId,
                                             Integer yearId,
                                             Integer semesterId,
                                             String searchTerm);

    List<ProgrammeRecord> getProgrammeOptions();

    List<ReferenceOption> getAllIntakes();

    List<ReferenceOption> getAllYears();

    List<ReferenceOption> getAllSemesters();
}
