package com.crs.ejb.impl;

import com.crs.dao.RecoveryDAO;
import com.crs.model.RecoveryAttemptInfo;
import com.crs.model.RecoveryPlanDetail;
import com.crs.ejb.RecoveryService;
import com.crs.model.RecoveryActiveRecord;
import com.crs.model.RecoveryComponentRecord;
import com.crs.model.RecoveryPendingRecord;
import com.crs.model.RecoveryWorkspaceStudent;
import com.crs.model.StudentPlanMilestone;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.ArrayList;
import java.util.List;

import com.crs.dao.StudentDAO;
import com.crs.ejb.EmailService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class RecoveryServiceBean implements RecoveryService {

    @EJB
    private RecoveryDAO recoveryDAO;
    
    @EJB
    private StudentDAO studentDAO;

    @EJB
    private EmailService emailService;

    @Override
    public List<RecoveryPendingRecord> getStudentsRequiringRecoveryAction() {
        return recoveryDAO.findStudentsRequiringRecoveryAction();
    }

    @Override
    public List<RecoveryActiveRecord> getStudentsWithExistingRecoveryAction() {
        return recoveryDAO.findStudentsWithExistingRecoveryAction();
    }

    @Override
    public RecoveryWorkspaceStudent getWorkspaceStudent(String studentId) {
        return recoveryDAO.findWorkspaceStudent(studentId);
    }

    @Override
    public List<RecoveryComponentRecord> getComponentResults(String studentId) {
        return recoveryDAO.findComponentResults(studentId);
    }

    @Override
    public String getPlanStatusByComponent(int studentCourseComponentId) {
        return recoveryDAO.findPlanStatusByStudentCourseComponentId(studentCourseComponentId);
    }

    @Override
    public List<StudentPlanMilestone> getMilestonesByComponent(int studentCourseComponentId) {
        return recoveryDAO.findMilestonesByStudentCourseComponentId(studentCourseComponentId);
    }

    @Override
    public void saveMilestones(int studentCourseId,
                               int studentCourseComponentId,
                               String[] descriptions,
                               String[] durations,
                               int userId,
                               boolean completed) {

        List<String> cleanedDescriptions = new ArrayList<>();
        List<Integer> cleanedDurations = new ArrayList<>();

        if (descriptions != null && durations != null) {
            for (int i = 0; i < descriptions.length && i < durations.length; i++) {
                String desc = descriptions[i] != null ? descriptions[i].trim() : "";
                String dur = durations[i] != null ? durations[i].trim() : "";

                if (!desc.isEmpty() && !dur.isEmpty()) {
                    cleanedDescriptions.add(desc);
                    cleanedDurations.add(Integer.parseInt(dur));
                }
            }
        }

        if (cleanedDescriptions.isEmpty()) {
            return;
        }

        String planStatus = completed ? "COMPLETED" : "ONGOING";
        String milestoneStatus = completed ? "COMPLETED" : "PENDING";

        Integer existingPlanId = recoveryDAO.findPlanIdByStudentCourseComponentId(studentCourseComponentId);
        boolean isNewPlan = (existingPlanId == null);

        Integer planId = existingPlanId;

        if (planId == null) {
            planId = recoveryDAO.createStudentPlan(
                    studentCourseId,
                    studentCourseComponentId,
                    userId,
                    planStatus
            );
        } else {
            recoveryDAO.updateStudentPlan(planId, userId, planStatus);
        }

        recoveryDAO.deleteMilestonesByPlanId(planId);

        for (int i = 0; i < cleanedDescriptions.size(); i++) {
            recoveryDAO.insertMilestone(
                    planId,
                    i + 1,
                    cleanedDescriptions.get(i),
                    cleanedDurations.get(i),
                    milestoneStatus,
                    completed
            );
        }

        // send email after successful save
        if (planId != null) {
            if (isNewPlan) {
                sendRecoveryPlanAssignedEmail(planId, studentCourseId, cleanedDescriptions, cleanedDurations);
            } else {
                sendRecoveryPlanUpdatedEmail(planId, studentCourseId, cleanedDescriptions, cleanedDurations);
            }
        }
    }
    
    @Override
    public RecoveryPlanDetail getRecoveryPlanDetailById(int studentPlanId) {
        return recoveryDAO.findRecoveryPlanDetailById(studentPlanId);
    }

    @Override
    public List<StudentPlanMilestone> getMilestonesByPlanId(int studentPlanId) {
        return recoveryDAO.findMilestonesByPlanId(studentPlanId);
    }

    @Override
    public void updateMilestone(int studentPlanId, int milestoneId, String description, int durationDays) {
        RecoveryPlanDetail plan = recoveryDAO.findRecoveryPlanDetailById(studentPlanId);
        StudentPlanMilestone milestone = recoveryDAO.findMilestoneById(milestoneId);

        if (plan == null || milestone == null) {
            return;
        }

        if (milestone.getStudentPlanId() != studentPlanId) {
            return;
        }

        if ("COMPLETED".equalsIgnoreCase(plan.getStatus())) {
            return;
        }

        if ("COMPLETED".equalsIgnoreCase(milestone.getStatus())) {
            return;
        }

        if (description == null || description.isBlank() || durationDays <= 0) {
            return;
        }

        recoveryDAO.updateMilestone(milestoneId, description.trim(), durationDays);

        // send updated-plan email after milestone edit
        sendRecoveryPlanUpdatedEmailByPlanId(studentPlanId);
    }

    @Override
    public void completeMilestone(int studentPlanId, int milestoneId, int userId) {
        RecoveryPlanDetail plan = recoveryDAO.findRecoveryPlanDetailById(studentPlanId);
        StudentPlanMilestone milestone = recoveryDAO.findMilestoneById(milestoneId);

        if (plan == null || milestone == null) {
            return;
        }

        if (milestone.getStudentPlanId() != studentPlanId) {
            return;
        }

        if ("COMPLETED".equalsIgnoreCase(plan.getStatus())) {
            return;
        }

        if ("COMPLETED".equalsIgnoreCase(milestone.getStatus())) {
            return;
        }

        recoveryDAO.completeMilestone(milestoneId);

        // 先不要在這裡把 plan completed
        // plan 要等 officer 輸入 final recovery mark 後才 completed
    }
    
    @Override
    public void removeRecoveryPlan(int studentPlanId) {
        RecoveryPlanDetail plan = recoveryDAO.findRecoveryPlanDetailById(studentPlanId);

        if (plan == null) {
            return;
        }

        // completed plan not allowed to remove
        if ("COMPLETED".equalsIgnoreCase(plan.getStatus())) {
            return;
        }

        recoveryDAO.deleteStudentPlanById(studentPlanId);
    }
    
    @Override
    public void completeRecoveryPlanWithResult(int studentPlanId, double resultMark, int userId) {
        RecoveryPlanDetail plan = recoveryDAO.findRecoveryPlanDetailById(studentPlanId);

        if (plan == null) {
            return;
        }

        // already completed
        if ("COMPLETED".equalsIgnoreCase(plan.getStatus())) {
            return;
        }

        // invalid mark
        if (resultMark < 0 || resultMark > 100) {
            return;
        }

        // all milestones must be completed first
        if (recoveryDAO.hasPendingMilestones(studentPlanId)) {
            return;
        }

        // result for this recovery component
        String componentGrade = toGrade(resultMark);
        double componentGradePoint = toGradePoint(resultMark);

        Integer studentCourseComponentId = recoveryDAO.findStudentCourseComponentIdByPlanId(studentPlanId);
        if (studentCourseComponentId == null) {
            return;
        }

        // 1. complete student_plan + update this component result
        recoveryDAO.completeRecoveryPlanWithResult(
                studentPlanId,
                studentCourseComponentId,
                resultMark,
                componentGrade,
                componentGradePoint,
                userId
        );

        // 2. update parent student_course result as well
        Integer studentCourseId = recoveryDAO.findStudentCourseIdByPlanId(studentPlanId);
        if (studentCourseId == null) {
            return;
        }

        boolean hasFailedComponent = recoveryDAO.hasFailedComponentInStudentCourse(studentCourseId);

        String courseGrade;
        double courseGradePoint;

        if (hasFailedComponent) {
            // still fail at course level if any component still fails
            courseGrade = "F";
            courseGradePoint = 0.00;
        } else {
            // all components passed -> calculate weighted overall mark for this course
            double weightedMark = recoveryDAO.calculateWeightedMarkForStudentCourse(studentCourseId);
            courseGrade = toGrade(weightedMark);
            courseGradePoint = toGradePoint(weightedMark);
        }

        recoveryDAO.updateStudentCourseResult(studentCourseId, courseGrade, courseGradePoint);

        // 3. if current term has no more unfinished recovery work, send back to eligibility re-check
        if (!recoveryDAO.hasOutstandingRecoveryForCurrentTerm(plan.getStudentId())) {
            recoveryDAO.updateCurrentRecoveryProgressionToAwaitingRecheck(plan.getStudentId());
        }
    }

    private String toGrade(double mark) {
        if (mark >= 80) return "A";
        if (mark >= 75) return "A-";
        if (mark >= 70) return "B+";
        if (mark >= 65) return "B";
        if (mark >= 60) return "B-";
        if (mark >= 55) return "C+";
        if (mark >= 50) return "C";
        if (mark >= 45) return "D";
        if (mark >= 40) return "D-";
        return "F";
    }

    private double toGradePoint(double mark) {
        if (mark >= 80) return 4.00;
        if (mark >= 75) return 3.67;
        if (mark >= 70) return 3.33;
        if (mark >= 65) return 3.00;
        if (mark >= 60) return 2.67;
        if (mark >= 55) return 2.33;
        if (mark >= 50) return 2.00;
        if (mark >= 45) return 1.67;
        if (mark >= 40) return 1.00;
        return 0.00;
    }
    
    @Override
    public Integer prepareNewRecoveryAttemptComponent(int studentCourseComponentId) {
        return recoveryDAO.prepareNewRecoveryAttemptComponent(studentCourseComponentId);
    }
    
    @Override
    public RecoveryAttemptInfo findAttemptInfoByStudentCourseComponentId(int studentCourseComponentId) {
        return recoveryDAO.findAttemptInfoByStudentCourseComponentId(studentCourseComponentId);
    }
    
    private void sendRecoveryPlanAssignedEmail(int studentPlanId,
            int studentCourseId,
            List<String> descriptions,
            List<Integer> durations) {

		RecoveryPlanDetail detail = recoveryDAO.findRecoveryPlanDetailById(studentPlanId);
			if (detail == null) {
			return;
		}
		
		String toEmail = studentDAO.findStudentEmailByStudentId(detail.getStudentId());
			if (toEmail == null || toEmail.isBlank()) {
			return;
		}
		
		int attemptNo = recoveryDAO.findAttemptNoByStudentCourseId(studentCourseId);
		
		Map<String, String> values = new HashMap<>();
		values.put("studentName", detail.getStudentName() == null ? "Student" : detail.getStudentName());
		values.put("studentId", detail.getStudentId());
		values.put("programName", detail.getProgramName() == null ? "" : detail.getProgramName());
		values.put("courseName", detail.getCourseName() == null ? "" : detail.getCourseName());
		values.put("attemptNo", String.valueOf(attemptNo));
		values.put("failedComponentList", detail.getComponentName() == null ? "" : detail.getComponentName());
		values.put("planStartDate", LocalDate.now().toString());
		values.put("planEndDate", calculatePlanEndDate(durations));
		values.put("milestoneList", buildMilestoneList(descriptions, durations));
		
		emailService.sendByTemplate("RECOVERY_PLAN_ASSIGNED", toEmail, values);
	}
		
	private void sendRecoveryPlanUpdatedEmail(int studentPlanId,
           int studentCourseId,
           List<String> descriptions,
           List<Integer> durations) {
		
		RecoveryPlanDetail detail = recoveryDAO.findRecoveryPlanDetailById(studentPlanId);
			if (detail == null) {
			return;
		}
		
		String toEmail = studentDAO.findStudentEmailByStudentId(detail.getStudentId());
			if (toEmail == null || toEmail.isBlank()) {
			return;
		}
		
		int attemptNo = recoveryDAO.findAttemptNoByStudentCourseId(studentCourseId);
		
		Map<String, String> values = new HashMap<>();
		values.put("studentName", detail.getStudentName() == null ? "Student" : detail.getStudentName());
		values.put("studentId", detail.getStudentId());
		values.put("programName", detail.getProgramName() == null ? "" : detail.getProgramName());
		values.put("courseName", detail.getCourseName() == null ? "" : detail.getCourseName());
		values.put("attemptNo", String.valueOf(attemptNo));
		values.put("failedComponentList", detail.getComponentName() == null ? "" : detail.getComponentName());
		values.put("planStartDate", LocalDate.now().toString());
		values.put("planEndDate", calculatePlanEndDate(durations));
		values.put("milestoneList", buildMilestoneList(descriptions, durations));
		
		emailService.sendByTemplate("RECOVERY_PLAN_UPDATED", toEmail, values);
	}
	
	private String buildMilestoneList(List<String> descriptions, List<Integer> durations) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < descriptions.size(); i++) {
			sb.append(i + 1)
			.append(". ")
			.append(descriptions.get(i))
			.append(" (")
			.append(durations.get(i))
			.append(" day(s))");
			
			if (i < descriptions.size() - 1) {
				
			}
		}
		
		return sb.toString();
		}
		
		private String calculatePlanEndDate(List<Integer> durations) {
		int totalDays = 0;
		
		for (Integer d : durations) {
			if (d != null) {
				totalDays += d;
			}
		}
		
		return LocalDate.now().plusDays(totalDays).toString();
	}
		
	private void sendRecoveryPlanUpdatedEmailByPlanId(int studentPlanId) {
	    RecoveryPlanDetail detail = recoveryDAO.findRecoveryPlanDetailById(studentPlanId);
	    if (detail == null) {
	        return;
	    }

	    String toEmail = studentDAO.findStudentEmailByStudentId(detail.getStudentId());
	    if (toEmail == null || toEmail.isBlank()) {
	        return;
	    }

	    Integer studentCourseId = recoveryDAO.findStudentCourseIdByPlanId(studentPlanId);
	    if (studentCourseId == null) {
	        return;
	    }

	    int attemptNo = recoveryDAO.findAttemptNoByStudentCourseId(studentCourseId);

	    List<StudentPlanMilestone> milestones = recoveryDAO.findMilestonesByPlanId(studentPlanId);

	    List<String> descriptions = new ArrayList<>();
	    List<Integer> durations = new ArrayList<>();

	    if (milestones != null) {
	        for (StudentPlanMilestone m : milestones) {
	            descriptions.add(m.getMilestoneDescription());
	            durations.add(m.getDurationDays());
	        }
	    }

	    Map<String, String> values = new HashMap<>();
	    values.put("studentName", detail.getStudentName() == null ? "Student" : detail.getStudentName());
	    values.put("studentId", detail.getStudentId());
	    values.put("programName", detail.getProgramName() == null ? "" : detail.getProgramName());
	    values.put("courseName", detail.getCourseName() == null ? "" : detail.getCourseName());
	    values.put("attemptNo", String.valueOf(attemptNo));
	    values.put("failedComponentList", detail.getComponentName() == null ? "" : detail.getComponentName());
	    values.put("planStartDate", LocalDate.now().toString());
	    values.put("planEndDate", calculatePlanEndDate(durations));
	    values.put("milestoneList", buildMilestoneList(descriptions, durations));

	    emailService.sendByTemplate("RECOVERY_PLAN_UPDATED", toEmail, values);
	}
}