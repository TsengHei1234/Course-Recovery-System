package com.crs.model;

public class EligibilityRecord {

    private String studentId;
    private String studentName;
    private String studentEmail;

    private int progressionId;

    private String programmeName;
    private String intakeName;

    private String currentYearName;
    private String currentSemesterName;

    private String nextYearName;
    private String nextSemesterName;

    private double cgpa;
    private int failedCourseCount;

    private String status;
    private String reason;

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

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public int getProgressionId() {
        return progressionId;
    }

    public void setProgressionId(int progressionId) {
        this.progressionId = progressionId;
    }

    public String getProgrammeName() {
        return programmeName;
    }

    public void setProgrammeName(String programmeName) {
        this.programmeName = programmeName;
    }

    public String getIntakeName() {
        return intakeName;
    }

    public void setIntakeName(String intakeName) {
        this.intakeName = intakeName;
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

    public String getNextYearName() {
        return nextYearName;
    }

    public void setNextYearName(String nextYearName) {
        this.nextYearName = nextYearName;
    }

    public String getNextSemesterName() {
        return nextSemesterName;
    }

    public void setNextSemesterName(String nextSemesterName) {
        this.nextSemesterName = nextSemesterName;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public int getFailedCourseCount() {
        return failedCourseCount;
    }

    public void setFailedCourseCount(int failedCourseCount) {
        this.failedCourseCount = failedCourseCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}