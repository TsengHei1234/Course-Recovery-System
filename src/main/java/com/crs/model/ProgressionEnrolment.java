package com.crs.model;

public class ProgressionEnrolment {

    private int progressionId;
    private String studentId;
    private Integer currentYearId;
    private Integer currentSemesterId;
    private Integer nextYearId;
    private Integer nextSemesterId;

    private Double cgpa;
    private Integer failedCourseCount;
    private String status;
    private String reason;

    public int getProgressionId() {
        return progressionId;
    }

    public void setProgressionId(int progressionId) {
        this.progressionId = progressionId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getCurrentYearId() {
        return currentYearId;
    }

    public void setCurrentYearId(Integer currentYearId) {
        this.currentYearId = currentYearId;
    }

    public Integer getCurrentSemesterId() {
        return currentSemesterId;
    }

    public void setCurrentSemesterId(Integer currentSemesterId) {
        this.currentSemesterId = currentSemesterId;
    }

    public Integer getNextYearId() {
        return nextYearId;
    }

    public void setNextYearId(Integer nextYearId) {
        this.nextYearId = nextYearId;
    }

    public Integer getNextSemesterId() {
        return nextSemesterId;
    }

    public void setNextSemesterId(Integer nextSemesterId) {
        this.nextSemesterId = nextSemesterId;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getFailedCourseCount() {
        return failedCourseCount;
    }

    public void setFailedCourseCount(Integer failedCourseCount) {
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