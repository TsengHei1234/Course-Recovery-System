package com.crs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProgrammeStructureGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private String programCode;
    private String programName;
    private int intakeId;
    private String intakeName;
    private int yearId;
    private String yearName;
    private int semesterId;
    private String semesterName;
    private List<CourseRecord> courses = new ArrayList<>();

    public ProgrammeStructureGroup() {
    }

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(int intakeId) {
        this.intakeId = intakeId;
    }

    public String getIntakeName() {
        return intakeName;
    }

    public void setIntakeName(String intakeName) {
        this.intakeName = intakeName;
    }

    public int getYearId() {
        return yearId;
    }

    public void setYearId(int yearId) {
        this.yearId = yearId;
    }

    public String getYearName() {
        return yearName;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public List<CourseRecord> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseRecord> courses) {
        this.courses = courses;
    }
}
