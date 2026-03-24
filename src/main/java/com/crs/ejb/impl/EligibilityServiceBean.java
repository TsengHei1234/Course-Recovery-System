package com.crs.ejb.impl;

import com.crs.dao.ProgressionEnrolmentDAO;
import com.crs.dao.StudentDAO;
import com.crs.ejb.EmailService;
import com.crs.ejb.EligibilityService;
import com.crs.model.EligibilityRecord;
import com.crs.model.ProgressionEnrolment;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class EligibilityServiceBean implements EligibilityService {

    @EJB
    private ProgressionEnrolmentDAO progressionEnrolmentDAO;

    @EJB
    private StudentDAO studentDAO;

    @EJB
    private EmailService emailService;

    @Override
    public List<EligibilityRecord> getAwaitingCheckRecords(String programme, String intake, String yearName, String semesterName, String search) {
        return progressionEnrolmentDAO.findAwaitingCheckRecords(programme, intake, yearName, semesterName, search);
    }

    @Override
    public List<EligibilityRecord> getPendingApprovalRecords(String programme, String intake, String yearName, String semesterName, String search) {
        return progressionEnrolmentDAO.findPendingApprovalRecords(programme, intake, yearName, semesterName, search);
    }

    @Override
    public List<EligibilityRecord> getRecoveryQueueRecords(String programme, String intake, String yearName, String semesterName, String search) {
        return progressionEnrolmentDAO.findRecoveryQueueRecords(programme, intake, yearName, semesterName, search);
    }

    @Override
    public List<EligibilityRecord> getProcessedRecords(String programme, String intake, String yearName, String semesterName, String search) {
        return progressionEnrolmentDAO.findProcessedRecords(programme, intake, yearName, semesterName, search);
    }

    @Override
    public List<String> getProgrammeOptions() {
        return progressionEnrolmentDAO.findProgrammeOptions();
    }

    @Override
    public List<String> getIntakeOptions() {
        return progressionEnrolmentDAO.findIntakeOptions();
    }

    @Override
    public List<String> getYearOptions() {
        return progressionEnrolmentDAO.findYearOptions();
    }

    @Override
    public List<String> getSemesterOptions() {
        return progressionEnrolmentDAO.findSemesterOptions();
    }

    @Override
    public void checkAllEligibility(int userId, String programme, String intake, String yearName, String semesterName, String search) {
        List<EligibilityRecord> awaitingList =
                progressionEnrolmentDAO.findAwaitingCheckRecords(programme, intake, yearName, semesterName, search);

        for (EligibilityRecord record : awaitingList) {
            double cgpa = progressionEnrolmentDAO.calculateCgpa(record.getStudentId());
            int failedCount = progressionEnrolmentDAO.calculateFailedCourseCount(record.getStudentId());

            String status;
            String reason;

            if (cgpa >= 2.0 && failedCount <= 3) {
                status = "PENDING_APPROVAL";
                reason = "Meets CGPA and failed-course criteria.";
            } else {
                status = "PENDING_RECOVERY";

                if (cgpa < 2.0 && failedCount > 3) {
                    reason = "CGPA below 2.0 and failed courses more than 3.";
                } else if (cgpa < 2.0) {
                    reason = "CGPA below 2.0.";
                } else {
                    reason = "Failed courses more than 3.";
                }
            }

            progressionEnrolmentDAO.updateAfterCheck(
                    record.getProgressionId(),
                    cgpa,
                    failedCount,
                    status,
                    reason,
                    userId
            );
        }
    }

    @Override
    public void approveEnrolment(int progressionId, int userId) {

        ProgressionEnrolment pe = progressionEnrolmentDAO.findById(progressionId);

        if (pe == null) {
            return;
        }

        // final term: no next term exists
        if (pe.getNextYearId() == null || pe.getNextSemesterId() == null) {
            boolean hasFailed = pe.getFailedCourseCount() != null && pe.getFailedCourseCount() > 0;
            boolean lowCgpa = pe.getCgpa() != null && pe.getCgpa() < 2.0;

            if (hasFailed || lowCgpa) {
                progressionEnrolmentDAO.sendToRecovery(progressionId, userId);
                sendRecoveryEmail(pe, "Final term student still requires recovery before study completion.");
            } else {
                progressionEnrolmentDAO.completeStudy(progressionId, userId);
                sendEligibleEmail(pe, true);
            }
            return;
        }

        // normal approve
        progressionEnrolmentDAO.approveEnrolment(progressionId, userId);

        Integer newCurrentYearId = pe.getNextYearId();
        Integer newCurrentSemesterId = pe.getNextSemesterId();

        studentDAO.updateStudentTerm(
                pe.getStudentId(),
                newCurrentYearId,
                newCurrentSemesterId
        );

        boolean exists = progressionEnrolmentDAO.existsProgressionForTerm(
                pe.getStudentId(),
                newCurrentYearId,
                newCurrentSemesterId
        );

        if (!exists) {
            Integer[] nextTerm = progressionEnrolmentDAO.findNextTerm(
                    newCurrentYearId,
                    newCurrentSemesterId
            );

            Integer newNextYearId = nextTerm[0];
            Integer newNextSemesterId = nextTerm[1];

            progressionEnrolmentDAO.insertNextProgression(
                    pe.getStudentId(),
                    newCurrentYearId,
                    newCurrentSemesterId,
                    newNextYearId,
                    newNextSemesterId
            );
        }

        sendEligibleEmail(pe, false);
    }

    @Override
    public void approveSelected(List<Integer> progressionIds, int userId) {
        for (Integer progressionId : progressionIds) {
            approveEnrolment(progressionId, userId);
        }
    }

    @Override
    public void sendToRecovery(int progressionId, int userId) {
        ProgressionEnrolment pe = progressionEnrolmentDAO.findById(progressionId);

        if (pe == null) {
            return;
        }

        progressionEnrolmentDAO.sendToRecovery(progressionId, userId);
        sendRecoveryEmail(pe, "Student does not meet progression criteria and has been placed into recovery workflow.");
    }

    @Override
    public void sendSelectedToRecovery(List<Integer> progressionIds, int userId) {
        for (Integer progressionId : progressionIds) {
            sendToRecovery(progressionId, userId);
        }
    }

    private void sendEligibleEmail(ProgressionEnrolment pe, boolean completedStudy) {
        String toEmail = studentDAO.findStudentEmailByStudentId(pe.getStudentId());
        String studentName = studentDAO.findStudentNameByStudentId(pe.getStudentId());

        if (toEmail == null || toEmail.isBlank()) {
            return;
        }

        String programName = studentDAO.findProgramNameByStudentId(pe.getStudentId());
        String yearName = progressionEnrolmentDAO.findYearNameById(pe.getCurrentYearId());
        String semesterName = progressionEnrolmentDAO.findSemesterNameById(pe.getCurrentSemesterId());

        Map<String, String> values = new HashMap<>();
        values.put("studentName", studentName == null ? "Student" : studentName);
        values.put("studentId", pe.getStudentId());
        values.put("programName", programName == null ? "" : programName);
        values.put("yearName", yearName == null ? "" : yearName);
        values.put("semesterName", semesterName == null ? "" : semesterName);
        values.put("cgpa", pe.getCgpa() == null ? "" : String.format("%.2f", pe.getCgpa()));
        values.put("failedCourseCount", pe.getFailedCourseCount() == null ? "0" : String.valueOf(pe.getFailedCourseCount()));
        values.put("nextLevel", completedStudy ? "Completed Study" : "Next Semester");
        values.put("failedCourseList", "Please refer to CRS for the detailed failed course list.");

        if (completedStudy) {
            emailService.sendByTemplate("COMPLETED_STUDY", toEmail, values);
        } else if (pe.getFailedCourseCount() != null && pe.getFailedCourseCount() > 0) {
            emailService.sendByTemplate("ELIGIBLE_WITH_FAILED", toEmail, values);
        } else {
            emailService.sendByTemplate("ELIGIBLE_CLEAR", toEmail, values);
        }
    }

    private void sendRecoveryEmail(ProgressionEnrolment pe, String fallbackReason) {
        String toEmail = studentDAO.findStudentEmailByStudentId(pe.getStudentId());
        String studentName = studentDAO.findStudentNameByStudentId(pe.getStudentId());

        if (toEmail == null || toEmail.isBlank()) {
            return;
        }

        String programName = studentDAO.findProgramNameByStudentId(pe.getStudentId());
        String yearName = progressionEnrolmentDAO.findYearNameById(pe.getCurrentYearId());
        String semesterName = progressionEnrolmentDAO.findSemesterNameById(pe.getCurrentSemesterId());

        Map<String, String> values = new HashMap<>();
        values.put("studentName", studentName == null ? "Student" : studentName);
        values.put("studentId", pe.getStudentId());
        values.put("programName", programName == null ? "" : programName);
        values.put("yearName", yearName == null ? "" : yearName);
        values.put("semesterName", semesterName == null ? "" : semesterName);
        values.put("cgpa", pe.getCgpa() == null ? "" : String.format("%.2f", pe.getCgpa()));
        values.put("failedCourseCount", pe.getFailedCourseCount() == null ? "0" : String.valueOf(pe.getFailedCourseCount()));
        values.put("eligibilityReason", pe.getReason() == null || pe.getReason().isBlank() ? fallbackReason : pe.getReason());
        values.put("reason", pe.getReason() == null || pe.getReason().isBlank() ? fallbackReason : pe.getReason());

        emailService.sendByTemplate("NOT_ELIGIBLE_RECOVERY_PENDING", toEmail, values);
    }
}