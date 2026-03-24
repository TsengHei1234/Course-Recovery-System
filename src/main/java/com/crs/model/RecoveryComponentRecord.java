package com.crs.model;

public class RecoveryComponentRecord {
    private int studentCourseId;
    private int studentCourseComponentId;
    private String courseName;
    private String componentName;
    private double weightPercent;
    private double mark;
    private String componentStatus;
    private int attemptNo;
    private Integer existingPlanId;
    private String existingPlanStatus;

    public int getStudentCourseId() {
        return studentCourseId;
    }

    public void setStudentCourseId(int studentCourseId) {
        this.studentCourseId = studentCourseId;
    }

    public int getStudentCourseComponentId() {
        return studentCourseComponentId;
    }

    public void setStudentCourseComponentId(int studentCourseComponentId) {
        this.studentCourseComponentId = studentCourseComponentId;
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

    public double getWeightPercent() {
        return weightPercent;
    }

    public void setWeightPercent(double weightPercent) {
        this.weightPercent = weightPercent;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public String getComponentStatus() {
        return componentStatus;
    }

    public void setComponentStatus(String componentStatus) {
        this.componentStatus = componentStatus;
    }

    public int getAttemptNo() {
        return attemptNo;
    }

    public void setAttemptNo(int attemptNo) {
        this.attemptNo = attemptNo;
    }
    
    public Integer getExistingPlanId() {
        return existingPlanId;
    }

    public void setExistingPlanId(Integer existingPlanId) {
        this.existingPlanId = existingPlanId;
    }

    public String getExistingPlanStatus() {
        return existingPlanStatus;
    }

    public void setExistingPlanStatus(String existingPlanStatus) {
        this.existingPlanStatus = existingPlanStatus;
    }
}