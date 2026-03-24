package com.crs.dao;

import com.crs.model.EligibilityRecord;
import com.crs.model.ProgressionEnrolment;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface ProgressionEnrolmentDAO {

    List<EligibilityRecord> findAwaitingCheckRecords(String programme, String intake, String yearName, String semesterName, String search);

    List<EligibilityRecord> findPendingApprovalRecords(String programme, String intake, String yearName, String semesterName, String search);

    List<EligibilityRecord> findRecoveryQueueRecords(String programme, String intake, String yearName, String semesterName, String search);

    List<EligibilityRecord> findProcessedRecords(String programme, String intake, String yearName, String semesterName, String search);

    List<String> findProgrammeOptions();

    List<String> findIntakeOptions();

    List<String> findYearOptions();

    List<String> findSemesterOptions();

    double calculateCgpa(String studentId);

    int calculateFailedCourseCount(String studentId);

    void updateAfterCheck(int progressionId, double cgpa, int failedCourseCount, String status, String reason, int checkedBy);

    void approveEnrolment(int progressionId, int approvedBy);

    void sendToRecovery(int progressionId, int sentBy);

    ProgressionEnrolment findById(int progressionId);

    Integer[] findNextTerm(int currentYearId, int currentSemesterId);

    boolean existsProgressionForTerm(String studentId, int currentYearId, int currentSemesterId);

    void insertNextProgression(String studentId,
                               Integer currentYearId,
                               Integer currentSemesterId,
                               Integer nextYearId,
                               Integer nextSemesterId);
    
    void completeStudy(int progressionId, int approvedBy);
    
    String findYearNameById(Integer yearId);
    String findSemesterNameById(Integer semesterId);
}