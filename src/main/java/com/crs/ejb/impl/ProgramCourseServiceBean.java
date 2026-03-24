package com.crs.ejb.impl;

import com.crs.dao.ProgramCourseDAO;
import com.crs.ejb.ProgramCourseService;
import com.crs.model.CourseRecord;
import com.crs.model.ProgrammeRecord;
import com.crs.model.ProgrammeStructureGroup;
import com.crs.model.ReferenceOption;
import com.crs.model.StudentCurriculumFocus;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class ProgramCourseServiceBean implements ProgramCourseService {

    @EJB
    private ProgramCourseDAO programCourseDAO;

    @Override
    public List<ProgrammeRecord> getProgrammes(Integer intakeId, String searchTerm) {
        return programCourseDAO.findProgrammes(intakeId, searchTerm);
    }

    @Override
    public List<ProgrammeRecord> getProgrammeOptions() {
        return programCourseDAO.findProgrammeOptions();
    }

    @Override
    public List<CourseRecord> getCourses(String searchTerm) {
        return programCourseDAO.findCourses(searchTerm);
    }

    @Override
    public List<ProgrammeStructureGroup> getProgrammeStructures(String programCode,
                                                                Integer intakeId,
                                                                Integer yearId,
                                                                Integer semesterId,
                                                                String courseSearch) {
        return programCourseDAO.findProgrammeStructures(programCode, intakeId, yearId, semesterId, courseSearch);
    }

    @Override
    public List<ReferenceOption> getAllIntakes() {
        return programCourseDAO.findAllIntakes();
    }

    @Override
    public List<ReferenceOption> getAllYears() {
        return programCourseDAO.findAllYears();
    }

    @Override
    public List<ReferenceOption> getAllSemesters() {
        return programCourseDAO.findAllSemesters();
    }

    @Override
    public StudentCurriculumFocus getStudentCurriculumFocus(String studentId) {
        return programCourseDAO.findStudentCurriculumFocus(studentId);
    }
}
