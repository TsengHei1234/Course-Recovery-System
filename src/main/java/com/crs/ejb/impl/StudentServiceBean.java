package com.crs.ejb.impl;

import com.crs.dao.StudentDAO;
import com.crs.ejb.StudentService;
import com.crs.model.ProgrammeRecord;
import com.crs.model.ReferenceOption;
import com.crs.model.StudentDirectoryRecord;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class StudentServiceBean implements StudentService {

    @EJB
    private StudentDAO studentDAO;

    @Override
    public List<StudentDirectoryRecord> getStudents(String programCode,
                                                    Integer intakeId,
                                                    Integer yearId,
                                                    Integer semesterId,
                                                    String searchTerm) {
        return studentDAO.findStudents(programCode, intakeId, yearId, semesterId, searchTerm);
    }

    @Override
    public List<ProgrammeRecord> getProgrammeOptions() {
        return studentDAO.findProgrammeOptions();
    }

    @Override
    public List<ReferenceOption> getAllIntakes() {
        return studentDAO.findAllIntakes();
    }

    @Override
    public List<ReferenceOption> getAllYears() {
        return studentDAO.findAllYears();
    }

    @Override
    public List<ReferenceOption> getAllSemesters() {
        return studentDAO.findAllSemesters();
    }
}
