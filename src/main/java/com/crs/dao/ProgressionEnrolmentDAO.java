package com.crs.dao;

import com.crs.model.EligibilityRecord;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface ProgressionEnrolmentDAO {

    List<EligibilityRecord> findAwaitingCheckRecords();

    List<EligibilityRecord> findPendingApprovalRecords();

    List<EligibilityRecord> findRecoveryQueueRecords();

    List<EligibilityRecord> findProcessedRecords();
    
    void updateAfterCheck(int progressionId, String status, String reason, int checkedBy);
    void approveEnrolment(int progressionId, int approvedBy);
}