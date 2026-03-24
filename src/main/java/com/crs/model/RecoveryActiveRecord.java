package com.crs.model;

public class RecoveryActiveRecord {
    private int studentPlanId;
    private String studentId;
    private String studentName;
    private String courseName;
    private String componentName;
    private String status;

    public int getStudentPlanId() {
        return studentPlanId;
    }

    public void setStudentPlanId(int studentPlanId) {
        this.studentPlanId = studentPlanId;
    }

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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}