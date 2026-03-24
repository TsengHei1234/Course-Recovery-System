package com.crs.ejb;

import com.crs.model.RecoveryActiveRecord;
import com.crs.model.RecoveryAttemptInfo;
import com.crs.model.RecoveryPlanDetail;
import com.crs.model.RecoveryComponentRecord;
import com.crs.model.RecoveryPendingRecord;
import com.crs.model.RecoveryWorkspaceStudent;
import com.crs.model.StudentPlanMilestone;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface RecoveryService {

    List<RecoveryPendingRecord> getStudentsRequiringRecoveryAction();

    List<RecoveryActiveRecord> getStudentsWithExistingRecoveryAction();

    RecoveryWorkspaceStudent getWorkspaceStudent(String studentId);

    List<RecoveryComponentRecord> getComponentResults(String studentId);

    String getPlanStatusByComponent(int studentCourseComponentId);

    List<StudentPlanMilestone> getMilestonesByComponent(int studentCourseComponentId);

    void saveMilestones(int studentCourseId,
                        int studentCourseComponentId,
                        String[] descriptions,
                        String[] durations,
                        int userId,
                        boolean completed);
    
    RecoveryPlanDetail getRecoveryPlanDetailById(int studentPlanId);

    List<StudentPlanMilestone> getMilestonesByPlanId(int studentPlanId);

    void updateMilestone(int studentPlanId, int milestoneId, String description, int durationDays);

    void completeMilestone(int studentPlanId, int milestoneId, int userId);
    
    void removeRecoveryPlan(int studentPlanId);
    
    void completeRecoveryPlanWithResult(int studentPlanId, double resultMark, int userId);
    
    Integer prepareNewRecoveryAttemptComponent(int studentCourseComponentId);
    
    RecoveryAttemptInfo findAttemptInfoByStudentCourseComponentId(int studentCourseComponentId);
}