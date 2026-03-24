package com.crs.model;

public class RecoveryPendingRecord {
    private String studentId;
    private String studentName;
    private String programName;
    private String currentYearName;
    private String currentSemesterName;
    private int failedComponentsCount;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getCurrentYearName() {
        return currentYearName;
    }

    public void setCurrentYearName(String currentYearName) {
        this.currentYearName = currentYearName;
    }

    public String getCurrentSemesterName() {
        return currentSemesterName;
    }

    public void setCurrentSemesterName(String currentSemesterName) {
        this.currentSemesterName = currentSemesterName;
    }

    public int getFailedComponentsCount() {
        return failedComponentsCount;
    }

    public void setFailedComponentsCount(int failedComponentsCount) {
        this.failedComponentsCount = failedComponentsCount;
    }
}