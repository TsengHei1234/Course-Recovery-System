package com.crs.model;

import java.sql.Timestamp;

public class StudentPlanMilestone {
    private int studentPlanMilestoneId;
    private int studentPlanId;
    private int milestoneNo;
    private String milestoneDescription;
    private int durationDays;
    private String status;
    private Timestamp completedAt;

    public int getStudentPlanMilestoneId() {
        return studentPlanMilestoneId;
    }

    public void setStudentPlanMilestoneId(int studentPlanMilestoneId) {
        this.studentPlanMilestoneId = studentPlanMilestoneId;
    }

    public int getStudentPlanId() {
        return studentPlanId;
    }

    public void setStudentPlanId(int studentPlanId) {
        this.studentPlanId = studentPlanId;
    }

    public int getMilestoneNo() {
        return milestoneNo;
    }

    public void setMilestoneNo(int milestoneNo) {
        this.milestoneNo = milestoneNo;
    }

    public String getMilestoneDescription() {
        return milestoneDescription;
    }

    public void setMilestoneDescription(String milestoneDescription) {
        this.milestoneDescription = milestoneDescription;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }
}