package com.crs.ejb;

import com.crs.model.EligibilityRecord;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface EligibilityService {

    List<EligibilityRecord> getAwaitingCheckRecords();

    List<EligibilityRecord> getPendingApprovalRecords();

    List<EligibilityRecord> getRecoveryQueueRecords();

    List<EligibilityRecord> getProcessedRecords();
    
    void checkAllEligibility(int userId);
    
    void approveEnrolment(int progressionId, int userId);
}