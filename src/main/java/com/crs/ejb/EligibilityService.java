package com.crs.ejb;

import com.crs.model.EligibilityRecord;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface EligibilityService {

    List<EligibilityRecord> getAwaitingCheckRecords(String programme, String intake, String yearName, String semesterName, String search);

    List<EligibilityRecord> getPendingApprovalRecords(String programme, String intake, String yearName, String semesterName, String search);

    List<EligibilityRecord> getRecoveryQueueRecords(String programme, String intake, String yearName, String semesterName, String search);

    List<EligibilityRecord> getProcessedRecords(String programme, String intake, String yearName, String semesterName, String search);

    List<String> getProgrammeOptions();

    List<String> getIntakeOptions();

    List<String> getYearOptions();

    List<String> getSemesterOptions();

    void checkAllEligibility(int userId, String programme, String intake, String yearName, String semesterName, String search);

    void approveEnrolment(int progressionId, int userId);

    void approveSelected(List<Integer> progressionIds, int userId);

    void sendToRecovery(int progressionId, int userId);

    void sendSelectedToRecovery(List<Integer> progressionIds, int userId);
}