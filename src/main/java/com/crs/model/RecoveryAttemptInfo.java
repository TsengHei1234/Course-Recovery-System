package com.crs.model;

public class RecoveryAttemptInfo {
    private String studentId;
    private int studentCourseId;
    private int programCourseId;
    private int attemptNo;
    private int courseComponentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getStudentCourseId() {
        return studentCourseId;
    }

    public void setStudentCourseId(int studentCourseId) {
        this.studentCourseId = studentCourseId;
    }

    public int getProgramCourseId() {
        return programCourseId;
    }

    public void setProgramCourseId(int programCourseId) {
        this.programCourseId = programCourseId;
    }

    public int getAttemptNo() {
        return attemptNo;
    }

    public void setAttemptNo(int attemptNo) {
        this.attemptNo = attemptNo;
    }

    public int getCourseComponentId() {
        return courseComponentId;
    }

    public void setCourseComponentId(int courseComponentId) {
        this.courseComponentId = courseComponentId;
    }
}