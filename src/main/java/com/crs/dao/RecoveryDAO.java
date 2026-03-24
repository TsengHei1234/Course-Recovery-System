package com.crs.dao;

import com.crs.model.RecoveryActiveRecord;
import com.crs.model.RecoveryPlanDetail;
import com.crs.model.RecoveryComponentRecord;
import com.crs.model.RecoveryPendingRecord;
import com.crs.model.RecoveryWorkspaceStudent;
import com.crs.model.StudentPlanMilestone;
import com.crs.model.RecoveryAttemptInfo;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface RecoveryDAO {

    List<RecoveryPendingRecord> findStudentsRequiringRecoveryAction();

    List<RecoveryActiveRecord> findStudentsWithExistingRecoveryAction();

    RecoveryWorkspaceStudent findWorkspaceStudent(String studentId);

    List<RecoveryComponentRecord> findComponentResults(String studentId);

    Integer findPlanIdByStudentCourseComponentId(int studentCourseComponentId);

    String findPlanStatusByStudentCourseComponentId(int studentCourseComponentId);

    List<StudentPlanMilestone> findMilestonesByStudentCourseComponentId(int studentCourseComponentId);

    int createStudentPlan(int studentCourseId, int studentCourseComponentId, int createdBy, String status);

    void updateStudentPlan(int studentPlanId, int updatedBy, String status);

    void deleteMilestonesByPlanId(int studentPlanId);

    void insertMilestone(int studentPlanId,
                         int milestoneNo,
                         String description,
                         int durationDays,
                         String status,
                         boolean completed);
    
    RecoveryPlanDetail findRecoveryPlanDetailById(int studentPlanId);

    List<StudentPlanMilestone> findMilestonesByPlanId(int studentPlanId);

    StudentPlanMilestone findMilestoneById(int milestoneId);

    void updateMilestone(int milestoneId, String description, int durationDays);

    void completeMilestone(int milestoneId);

    boolean hasPendingMilestones(int studentPlanId);

    void markPlanCompleted(int studentPlanId, int updatedBy);
    
    void deleteStudentPlanById(int studentPlanId);
    
    boolean hasOutstandingRecoveryForCurrentTerm(String studentId);

    void updateCurrentRecoveryProgressionToAwaitingRecheck(String studentId);
    
    void completeRecoveryPlanWithResult(int studentPlanId, int studentCourseComponentId, double mark, String grade, double gradePoint, int updatedBy);
    
    Integer findStudentCourseComponentIdByPlanId(int studentPlanId);
    
    RecoveryAttemptInfo findAttemptInfoByStudentCourseComponentId(int studentCourseComponentId);

    Integer findStudentCourseIdByAttempt(String studentId, int programCourseId, int attemptNo);

    int insertStudentCourseAttempt(String studentId, int programCourseId, int attemptNo);

    Integer findStudentCourseComponentIdByAttemptComponent(String studentId, int programCourseId, int attemptNo, int courseComponentId);

    int insertStudentCourseComponentAttempt(int studentCourseId, int courseComponentId);

    Integer prepareNewRecoveryAttemptComponent(int studentCourseComponentId);
    
    void cloneComponentsFromPreviousAttempt(int oldStudentCourseId, int newStudentCourseId);
    
    Integer findStudentCourseIdByPlanId(int studentPlanId);

    void updateStudentCourseResult(int studentCourseId, String grade, double gradePoint);

    boolean hasFailedComponentInStudentCourse(int studentCourseId);

    double calculateWeightedMarkForStudentCourse(int studentCourseId);
    
    int findAttemptNoByStudentCourseId(int studentCourseId);
}