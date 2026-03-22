package com.crs.ejb.impl;

import com.crs.dao.ProgressionEnrolmentDAO;
import com.crs.ejb.EligibilityService;
import com.crs.model.EligibilityRecord;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class EligibilityServiceBean implements EligibilityService {

    @EJB
    private ProgressionEnrolmentDAO progressionEnrolmentDAO;

    @Override
    public List<EligibilityRecord> getAwaitingCheckRecords() {
        return progressionEnrolmentDAO.findAwaitingCheckRecords();
    }

    @Override
    public List<EligibilityRecord> getPendingApprovalRecords() {
        return progressionEnrolmentDAO.findPendingApprovalRecords();
    }

    @Override
    public List<EligibilityRecord> getRecoveryQueueRecords() {
        return progressionEnrolmentDAO.findRecoveryQueueRecords();
    }

    @Override
    public List<EligibilityRecord> getProcessedRecords() {
        return progressionEnrolmentDAO.findProcessedRecords();
    }
    
    @Override
    public void checkAllEligibility(int userId) {

        List<EligibilityRecord> list = progressionEnrolmentDAO.findAwaitingCheckRecords();

        for (EligibilityRecord record : list) {

            String status;
            String reason;

            if (record.getCgpa() >= 2.0 && record.getFailedCourseCount() <= 3) {
                status = "PENDING_APPROVAL";
                reason = "Meets CGPA and failed-course criteria.";
            } else {
                status = "PENDING_RECOVERY";
                reason = "Does not meet eligibility criteria.";
            }

            progressionEnrolmentDAO.updateAfterCheck(
                    record.getProgressionId(),
                    status,
                    reason,
                    userId
            );
        }
    }
    
    @Override
    public void approveEnrolment(int progressionId, int userId) {
        progressionEnrolmentDAO.approveEnrolment(progressionId, userId);
    }
}