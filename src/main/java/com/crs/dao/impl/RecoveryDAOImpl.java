package com.crs.dao.impl;

import com.crs.dao.RecoveryDAO;
import com.crs.model.RecoveryAttemptInfo;
import com.crs.model.RecoveryPlanDetail;
import com.crs.model.RecoveryActiveRecord;
import com.crs.model.RecoveryComponentRecord;
import com.crs.model.RecoveryPendingRecord;
import com.crs.model.RecoveryWorkspaceStudent;
import com.crs.model.StudentPlanMilestone;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class RecoveryDAOImpl implements RecoveryDAO {

	private static final String FIND_PENDING_SQL =
	        "SELECT " +
	        "    s.student_id, " +
	        "    s.student_name, " +
	        "    p.program_name, " +
	        "    y.year_name, " +
	        "    sem.semester_name, " +
	        "    COUNT(DISTINCT CASE " +
	        "        WHEN (COALESCE(scc.grade_point, 0) < 2.00 OR UPPER(COALESCE(scc.grade, '')) = 'F') " +
	        "         AND (sp.student_plan_id IS NULL OR UPPER(COALESCE(sp.status, '')) = 'COMPLETED') " +
	        "        THEN scc.student_course_component_id END) AS failed_components_count " +
	        "FROM students s " +
	        "JOIN programs p ON s.program_id = p.program_id " +
	        "JOIN years y ON s.year_id = y.year_id " +
	        "JOIN semesters sem ON s.semester_id = sem.semester_id " +
	        "LEFT JOIN (" +
	        "   SELECT sc1.* FROM student_courses sc1 " +
	        "   JOIN (" +
	        "       SELECT student_id, program_course_id, MAX(attempt_no) AS max_attempt " +
	        "       FROM student_courses " +
	        "       GROUP BY student_id, program_course_id" +
	        "   ) latest " +
	        "   ON sc1.student_id = latest.student_id " +
	        "   AND sc1.program_course_id = latest.program_course_id " +
	        "   AND sc1.attempt_no = latest.max_attempt" +
	        ") sc ON sc.student_id = s.student_id " +
	        "LEFT JOIN student_course_components scc ON scc.student_course_id = sc.student_course_id " +
	        "LEFT JOIN student_plans sp ON sp.student_course_component_id = scc.student_course_component_id " +
	        "WHERE EXISTS (" +
	        "   SELECT 1 " +
	        "   FROM progression_enrolments pe " +
	        "   WHERE pe.student_id = s.student_id " +
	        "     AND (" +
	        "         pe.status = 'SENT_TO_RECOVERY' " +
	        "         OR (pe.status = 'APPROVED' AND COALESCE(pe.failed_course_count, 0) > 0)" +
	        "     )" +
	        ") " +
	        "GROUP BY s.student_id, s.student_name, p.program_name, y.year_name, sem.semester_name " +
	        "HAVING COUNT(DISTINCT CASE " +
	        "        WHEN (COALESCE(scc.grade_point, 0) < 2.00 OR UPPER(COALESCE(scc.grade, '')) = 'F') " +
	        "         AND (sp.student_plan_id IS NULL OR UPPER(COALESCE(sp.status, '')) = 'COMPLETED') " +
	        "        THEN scc.student_course_component_id END) > 0 " +
	        "ORDER BY s.student_id";

    private static final String FIND_ACTIVE_SQL =
            "SELECT " +
            "    sp.student_plan_id, " +
            "    s.student_id, " +
            "    s.student_name, " +
            "    c.course_name, " +
            "    cc.course_component_name, " +
            "    sp.status " +
            "FROM student_plans sp " +
            "JOIN student_courses sc ON sp.student_course_id = sc.student_course_id " +
            "JOIN students s ON sc.student_id = s.student_id " +
            "JOIN student_course_components scc ON sp.student_course_component_id = scc.student_course_component_id " +
            "JOIN course_components cc ON scc.course_component_id = cc.course_component_id " +
            "JOIN program_courses pc ON sc.program_course_id = pc.program_course_id " +
            "JOIN courses c ON pc.course_id = c.course_id " +
            "ORDER BY s.student_id, sp.updated_at DESC, sp.student_plan_id DESC";

    private static final String FIND_WORKSPACE_STUDENT_SQL =
            "SELECT " +
            "    s.student_id, s.student_name, p.program_name, y.year_name, sem.semester_name " +
            "FROM students s " +
            "JOIN programs p ON s.program_id = p.program_id " +
            "JOIN years y ON s.year_id = y.year_id " +
            "JOIN semesters sem ON s.semester_id = sem.semester_id " +
            "WHERE s.student_id = ?";

    private static final String FIND_COMPONENT_RESULTS_SQL =
            "SELECT " +
            "    sc.student_course_id, " +
            "    scc.student_course_component_id, " +
            "    c.course_name, " +
            "    cc.course_component_name, " +
            "    cc.weight_percent, " +
            "    COALESCE(scc.mark, 0) AS mark, " +
            "    sc.attempt_no, " +
            "    sp.student_plan_id AS existing_plan_id, " +
            "    sp.status AS existing_plan_status, " +
            "    CASE " +
            "        WHEN (COALESCE(scc.grade_point, 0) < 2.00 OR UPPER(COALESCE(scc.grade, '')) = 'F') THEN 'FAILED' " +
            "        ELSE 'PASSED' " +
            "    END AS component_status " +
            "FROM student_courses sc " +
            "JOIN student_course_components scc ON scc.student_course_id = sc.student_course_id " +
            "JOIN course_components cc ON scc.course_component_id = cc.course_component_id " +
            "JOIN program_courses pc ON sc.program_course_id = pc.program_course_id " +
            "JOIN courses c ON pc.course_id = c.course_id " +
            "JOIN ( " +
            "   SELECT sc2.student_id, sc2.program_course_id, scc2.course_component_id, MAX(sc2.attempt_no) AS max_attempt " +
            "   FROM student_courses sc2 " +
            "   JOIN student_course_components scc2 ON scc2.student_course_id = sc2.student_course_id " +
            "   WHERE sc2.student_id = ? " +
            "   GROUP BY sc2.student_id, sc2.program_course_id, scc2.course_component_id " +
            ") latest " +
            "  ON sc.student_id = latest.student_id " +
            " AND sc.program_course_id = latest.program_course_id " +
            " AND sc.attempt_no = latest.max_attempt " +
            " AND scc.course_component_id = latest.course_component_id " +
            "LEFT JOIN student_plans sp ON sp.student_course_component_id = scc.student_course_component_id " +
            "WHERE sc.student_id = ? " +
            "ORDER BY c.course_name, cc.course_component_name";

    private static final String FIND_PLAN_ID_BY_COMPONENT_SQL =
            "SELECT student_plan_id FROM student_plans WHERE student_course_component_id = ?";

    private static final String FIND_PLAN_STATUS_BY_COMPONENT_SQL =
            "SELECT status FROM student_plans WHERE student_course_component_id = ?";

    private static final String FIND_MILESTONES_BY_COMPONENT_SQL =
            "SELECT spm.student_plan_milestone_id, spm.student_plan_id, spm.milestone_no, " +
            "       spm.milestone_description, spm.duration_days, spm.status " +
            "FROM student_plan_milestones spm " +
            "JOIN student_plans sp ON spm.student_plan_id = sp.student_plan_id " +
            "WHERE sp.student_course_component_id = ? " +
            "ORDER BY spm.milestone_no";

    private static final String INSERT_STUDENT_PLAN_SQL =
            "INSERT INTO student_plans " +
            "(student_course_id, student_course_component_id, start_date, status, created_by) " +
            "VALUES (?, ?, CURRENT_DATE, ?, ?)";

    private static final String UPDATE_STUDENT_PLAN_SQL =
            "UPDATE student_plans " +
            "SET status = ?, updated_by = ?, updated_at = CURRENT_TIMESTAMP, " +
            "    end_date = CASE WHEN ? = 'COMPLETED' THEN CURRENT_DATE ELSE NULL END " +
            "WHERE student_plan_id = ?";

    private static final String DELETE_MILESTONES_SQL =
            "DELETE FROM student_plan_milestones WHERE student_plan_id = ?";

    private static final String INSERT_MILESTONE_SQL =
            "INSERT INTO student_plan_milestones " +
            "(student_plan_id, milestone_no, milestone_description, duration_days, status, completed_at) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String FIND_PLAN_DETAIL_SQL =
            "SELECT " +
            "    sp.student_plan_id, " +
            "    s.student_id, " +
            "    s.student_name, " +
            "    p.program_name, " +
            "    y.year_name, " +
            "    sem.semester_name, " +
            "    c.course_name, " +
            "    cc.course_component_name, " +
            "    sp.status, " +
            "    sp.grade, " +
            "    sp.grade_point " +
            "FROM student_plans sp " +
            "JOIN student_courses sc ON sp.student_course_id = sc.student_course_id " +
            "JOIN students s ON sc.student_id = s.student_id " +
            "JOIN programs p ON s.program_id = p.program_id " +
            "JOIN years y ON s.year_id = y.year_id " +
            "JOIN semesters sem ON s.semester_id = sem.semester_id " +
            "JOIN program_courses pc ON sc.program_course_id = pc.program_course_id " +
            "JOIN courses c ON pc.course_id = c.course_id " +
            "JOIN student_course_components scc ON sp.student_course_component_id = scc.student_course_component_id " +
            "JOIN course_components cc ON scc.course_component_id = cc.course_component_id " +
            "WHERE sp.student_plan_id = ?";

    private static final String FIND_MILESTONES_BY_PLAN_ID_SQL =
            "SELECT student_plan_milestone_id, student_plan_id, milestone_no, milestone_description, " +
            "       duration_days, status, completed_at " +
            "FROM student_plan_milestones " +
            "WHERE student_plan_id = ? " +
            "ORDER BY milestone_no";

    private static final String FIND_MILESTONE_BY_ID_SQL =
            "SELECT student_plan_milestone_id, student_plan_id, milestone_no, milestone_description, " +
            "       duration_days, status, completed_at " +
            "FROM student_plan_milestones " +
            "WHERE student_plan_milestone_id = ?";

    private static final String UPDATE_MILESTONE_SQL =
            "UPDATE student_plan_milestones " +
            "SET milestone_description = ?, duration_days = ? " +
            "WHERE student_plan_milestone_id = ?";

    private static final String COMPLETE_MILESTONE_SQL =
            "UPDATE student_plan_milestones " +
            "SET status = 'COMPLETED', completed_at = CURRENT_TIMESTAMP " +
            "WHERE student_plan_milestone_id = ?";

    private static final String HAS_PENDING_MILESTONES_SQL =
            "SELECT COUNT(*) AS total " +
            "FROM student_plan_milestones " +
            "WHERE student_plan_id = ? AND status <> 'COMPLETED'";

    private static final String MARK_PLAN_COMPLETED_SQL =
            "UPDATE student_plans " +
            "SET status = 'COMPLETED', updated_by = ?, updated_at = CURRENT_TIMESTAMP, end_date = CURRENT_DATE " +
            "WHERE student_plan_id = ?";
    
    private static final String DELETE_STUDENT_PLAN_SQL =
            "DELETE FROM student_plans WHERE student_plan_id = ?";
    
    private static final String HAS_OUTSTANDING_RECOVERY_SQL =
            "SELECT COUNT(*) AS total " +
            "FROM students s " +
            "JOIN student_courses sc ON sc.student_id = s.student_id " +
            "JOIN (" +
            "   SELECT student_id, program_course_id, MAX(attempt_no) AS max_attempt " +
            "   FROM student_courses " +
            "   WHERE student_id = ? " +
            "   GROUP BY student_id, program_course_id" +
            ") latest ON sc.student_id = latest.student_id " +
            "         AND sc.program_course_id = latest.program_course_id " +
            "         AND sc.attempt_no = latest.max_attempt " +
            "JOIN program_courses pc ON sc.program_course_id = pc.program_course_id " +
            "JOIN student_course_components scc ON scc.student_course_id = sc.student_course_id " +
            "LEFT JOIN student_plans sp ON sp.student_course_component_id = scc.student_course_component_id " +
            "WHERE s.student_id = ? " +
            "  AND pc.year_id = s.year_id " +
            "  AND pc.semester_id = s.semester_id " +
            "  AND (COALESCE(scc.grade_point, 0) < 2.00 OR UPPER(COALESCE(scc.grade, '')) = 'F') " +
            "  AND (sp.student_plan_id IS NULL OR UPPER(COALESCE(sp.status, '')) <> 'COMPLETED')";

    private static final String UPDATE_CURRENT_RECOVERY_TO_RECHECK_SQL =
            "UPDATE progression_enrolments pe " +
            "JOIN students s ON pe.student_id = s.student_id " +
            "SET pe.status = 'AWAITING_RECHECK', " +
            "    pe.reason = 'Recovery completed. Awaiting eligibility re-check.' " +
            "WHERE pe.student_id = ? " +
            "  AND pe.current_year_id = s.year_id " +
            "  AND pe.current_semester_id = s.semester_id " +
            "  AND pe.status = 'SENT_TO_RECOVERY'";
    
    private static final String UPDATE_STUDENT_COMPONENT_RESULT_SQL =
            "UPDATE student_course_components " +
            "SET mark = ?, grade = ?, grade_point = ? " +
            "WHERE student_course_component_id = ?";

    private static final String COMPLETE_PLAN_WITH_RESULT_SQL =
            "UPDATE student_plans " +
            "SET grade = ?, grade_point = ?, status = 'COMPLETED', " +
            "    updated_by = ?, updated_at = CURRENT_TIMESTAMP, end_date = CURRENT_DATE " +
            "WHERE student_plan_id = ?";
    
    private static final String FIND_COMPONENT_ID_BY_PLAN_ID_SQL =
            "SELECT student_course_component_id FROM student_plans WHERE student_plan_id = ?";
    
    private static final String FIND_ATTEMPT_INFO_BY_COMPONENT_SQL =
            "SELECT sc.student_id, sc.student_course_id, sc.program_course_id, sc.attempt_no, scc.course_component_id " +
            "FROM student_course_components scc " +
            "JOIN student_courses sc ON scc.student_course_id = sc.student_course_id " +
            "WHERE scc.student_course_component_id = ?";

    private static final String FIND_STUDENT_COURSE_ID_BY_ATTEMPT_SQL =
            "SELECT student_course_id " +
            "FROM student_courses " +
            "WHERE student_id = ? AND program_course_id = ? AND attempt_no = ?";

    private static final String INSERT_STUDENT_COURSE_ATTEMPT_SQL =
            "INSERT INTO student_courses (student_id, program_course_id, attempt_no, grade, grade_point) " +
            "VALUES (?, ?, ?, NULL, NULL)";

    private static final String FIND_COMPONENT_ID_BY_ATTEMPT_COMPONENT_SQL =
            "SELECT scc.student_course_component_id " +
            "FROM student_courses sc " +
            "JOIN student_course_components scc ON sc.student_course_id = scc.student_course_id " +
            "WHERE sc.student_id = ? " +
            "  AND sc.program_course_id = ? " +
            "  AND sc.attempt_no = ? " +
            "  AND scc.course_component_id = ?";

    private static final String INSERT_STUDENT_COURSE_COMPONENT_ATTEMPT_SQL =
            "INSERT INTO student_course_components (student_course_id, course_component_id, mark, grade, grade_point) " +
            "VALUES (?, ?, NULL, NULL, NULL)";
    
    private static final String CLONE_COMPONENTS_FROM_PREVIOUS_ATTEMPT_SQL =
            "INSERT INTO student_course_components " +
            "(student_course_id, course_component_id, mark, grade, grade_point) " +
            "SELECT ?, old_scc.course_component_id, old_scc.mark, old_scc.grade, old_scc.grade_point " +
            "FROM student_course_components old_scc " +
            "WHERE old_scc.student_course_id = ? " +
            "AND NOT EXISTS ( " +
            "    SELECT 1 " +
            "    FROM student_course_components new_scc " +
            "    WHERE new_scc.student_course_id = ? " +
            "      AND new_scc.course_component_id = old_scc.course_component_id" +
            ")";

    private static final String FIND_STUDENT_COURSE_ID_BY_PLAN_ID_SQL =
            "SELECT student_course_id FROM student_plans WHERE student_plan_id = ?";

    private static final String UPDATE_STUDENT_COURSE_RESULT_SQL =
            "UPDATE student_courses " +
            "SET grade = ?, grade_point = ? " +
            "WHERE student_course_id = ?";

    private static final String HAS_FAILED_COMPONENT_IN_STUDENT_COURSE_SQL =
            "SELECT COUNT(*) AS total " +
            "FROM student_course_components " +
            "WHERE student_course_id = ? " +
            "  AND (COALESCE(grade_point, 0) < 2.00 OR UPPER(COALESCE(grade, '')) = 'F')";

    private static final String CALCULATE_WEIGHTED_MARK_FOR_STUDENT_COURSE_SQL =
            "SELECT COALESCE(SUM(COALESCE(scc.mark,0) * COALESCE(cc.weight_percent,0) / 100), 0) AS weighted_mark " +
            "FROM student_course_components scc " +
            "JOIN course_components cc ON scc.course_component_id = cc.course_component_id " +
            "WHERE scc.student_course_id = ?";
    
    @Override
    public List<RecoveryPendingRecord> findStudentsRequiringRecoveryAction() {
        List<RecoveryPendingRecord> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_PENDING_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RecoveryPendingRecord record = new RecoveryPendingRecord();
                record.setStudentId(rs.getString("student_id"));
                record.setStudentName(rs.getString("student_name"));
                record.setProgramName(rs.getString("program_name"));
                record.setCurrentYearName(rs.getString("year_name"));
                record.setCurrentSemesterName(rs.getString("semester_name"));
                record.setFailedComponentsCount(rs.getInt("failed_components_count"));
                list.add(record);
            }

        } catch (Exception e) {
            System.out.println("ERROR in findStudentsRequiringRecoveryAction()");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<RecoveryActiveRecord> findStudentsWithExistingRecoveryAction() {
        List<RecoveryActiveRecord> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ACTIVE_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RecoveryActiveRecord record = new RecoveryActiveRecord();
                record.setStudentPlanId(rs.getInt("student_plan_id"));
                record.setStudentId(rs.getString("student_id"));
                record.setStudentName(rs.getString("student_name"));
                record.setCourseName(rs.getString("course_name"));
                record.setComponentName(rs.getString("course_component_name"));
                record.setStatus(rs.getString("status"));
                list.add(record);
            }

        } catch (Exception e) {
            System.out.println("ERROR in findStudentsWithExistingRecoveryAction()");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public RecoveryWorkspaceStudent findWorkspaceStudent(String studentId) {
        RecoveryWorkspaceStudent student = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_WORKSPACE_STUDENT_SQL)) {

            ps.setString(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = new RecoveryWorkspaceStudent();
                    student.setStudentId(rs.getString("student_id"));
                    student.setStudentName(rs.getString("student_name"));
                    student.setProgramName(rs.getString("program_name"));
                    student.setYearName(rs.getString("year_name"));
                    student.setSemesterName(rs.getString("semester_name"));
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findWorkspaceStudent()");
            e.printStackTrace();
        }

        return student;
    }

    @Override
    public List<RecoveryComponentRecord> findComponentResults(String studentId) {
        List<RecoveryComponentRecord> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_COMPONENT_RESULTS_SQL)) {

            ps.setString(1, studentId);
            ps.setString(2, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RecoveryComponentRecord record = new RecoveryComponentRecord();
                    record.setStudentCourseId(rs.getInt("student_course_id"));
                    record.setStudentCourseComponentId(rs.getInt("student_course_component_id"));
                    record.setCourseName(rs.getString("course_name"));
                    record.setComponentName(rs.getString("course_component_name"));
                    record.setWeightPercent(rs.getDouble("weight_percent"));
                    record.setMark(rs.getDouble("mark"));
                    record.setAttemptNo(rs.getInt("attempt_no"));
                    record.setComponentStatus(rs.getString("component_status"));

                    int existingPlanId = rs.getInt("existing_plan_id");
                    record.setExistingPlanId(rs.wasNull() ? null : existingPlanId);
                    record.setExistingPlanStatus(rs.getString("existing_plan_status"));

                    list.add(record);
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findComponentResults()");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public Integer findPlanIdByStudentCourseComponentId(int studentCourseComponentId) {
        Integer planId = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_PLAN_ID_BY_COMPONENT_SQL)) {

            ps.setInt(1, studentCourseComponentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    planId = rs.getInt("student_plan_id");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findPlanIdByStudentCourseComponentId()");
            e.printStackTrace();
        }

        return planId;
    }

    @Override
    public String findPlanStatusByStudentCourseComponentId(int studentCourseComponentId) {
        String status = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_PLAN_STATUS_BY_COMPONENT_SQL)) {

            ps.setInt(1, studentCourseComponentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    status = rs.getString("status");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findPlanStatusByStudentCourseComponentId()");
            e.printStackTrace();
        }

        return status;
    }

    @Override
    public List<StudentPlanMilestone> findMilestonesByStudentCourseComponentId(int studentCourseComponentId) {
        List<StudentPlanMilestone> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_MILESTONES_BY_COMPONENT_SQL)) {

            ps.setInt(1, studentCourseComponentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentPlanMilestone milestone = new StudentPlanMilestone();
                    milestone.setStudentPlanMilestoneId(rs.getInt("student_plan_milestone_id"));
                    milestone.setStudentPlanId(rs.getInt("student_plan_id"));
                    milestone.setMilestoneNo(rs.getInt("milestone_no"));
                    milestone.setMilestoneDescription(rs.getString("milestone_description"));
                    milestone.setDurationDays(rs.getInt("duration_days"));
                    milestone.setStatus(rs.getString("status"));
                    list.add(milestone);
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findMilestonesByStudentCourseComponentId()");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public int createStudentPlan(int studentCourseId, int studentCourseComponentId, int createdBy, String status) {
        int generatedId = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_STUDENT_PLAN_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, studentCourseId);
            ps.setInt(2, studentCourseComponentId);
            ps.setString(3, status);
            ps.setInt(4, createdBy);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in createStudentPlan()");
            e.printStackTrace();
        }

        return generatedId;
    }

    @Override
    public void updateStudentPlan(int studentPlanId, int updatedBy, String status) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STUDENT_PLAN_SQL)) {

            ps.setString(1, status);
            ps.setInt(2, updatedBy);
            ps.setString(3, status);
            ps.setInt(4, studentPlanId);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in updateStudentPlan()");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMilestonesByPlanId(int studentPlanId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_MILESTONES_SQL)) {

            ps.setInt(1, studentPlanId);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in deleteMilestonesByPlanId()");
            e.printStackTrace();
        }
    }

    @Override
    public void insertMilestone(int studentPlanId,
                                int milestoneNo,
                                String description,
                                int durationDays,
                                String status,
                                boolean completed) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_MILESTONE_SQL)) {

            ps.setInt(1, studentPlanId);
            ps.setInt(2, milestoneNo);
            ps.setString(3, description);
            ps.setInt(4, durationDays);
            ps.setString(5, status);

            if (completed) {
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            } else {
                ps.setNull(6, java.sql.Types.TIMESTAMP);
            }

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in insertMilestone()");
            e.printStackTrace();
        }
    }
    
    @Override
    public RecoveryPlanDetail findRecoveryPlanDetailById(int studentPlanId) {
        RecoveryPlanDetail detail = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_PLAN_DETAIL_SQL)) {

            ps.setInt(1, studentPlanId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detail = new RecoveryPlanDetail();
                    detail.setStudentPlanId(rs.getInt("student_plan_id"));
                    detail.setStudentId(rs.getString("student_id"));
                    detail.setStudentName(rs.getString("student_name"));
                    detail.setProgramName(rs.getString("program_name"));
                    detail.setYearName(rs.getString("year_name"));
                    detail.setSemesterName(rs.getString("semester_name"));
                    detail.setCourseName(rs.getString("course_name"));
                    detail.setComponentName(rs.getString("course_component_name"));
                    detail.setStatus(rs.getString("status"));
                    detail.setGrade(rs.getString("grade"));

                    double gp = rs.getDouble("grade_point");
                    detail.setGradePoint(rs.wasNull() ? null : gp);
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findRecoveryPlanDetailById()");
            e.printStackTrace();
        }

        return detail;
    }

    @Override
    public List<StudentPlanMilestone> findMilestonesByPlanId(int studentPlanId) {
        List<StudentPlanMilestone> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_MILESTONES_BY_PLAN_ID_SQL)) {

            ps.setInt(1, studentPlanId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentPlanMilestone milestone = new StudentPlanMilestone();
                    milestone.setStudentPlanMilestoneId(rs.getInt("student_plan_milestone_id"));
                    milestone.setStudentPlanId(rs.getInt("student_plan_id"));
                    milestone.setMilestoneNo(rs.getInt("milestone_no"));
                    milestone.setMilestoneDescription(rs.getString("milestone_description"));
                    milestone.setDurationDays(rs.getInt("duration_days"));
                    milestone.setStatus(rs.getString("status"));
                    milestone.setCompletedAt(rs.getTimestamp("completed_at"));
                    list.add(milestone);
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findMilestonesByPlanId()");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public StudentPlanMilestone findMilestoneById(int milestoneId) {
        StudentPlanMilestone milestone = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_MILESTONE_BY_ID_SQL)) {

            ps.setInt(1, milestoneId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    milestone = new StudentPlanMilestone();
                    milestone.setStudentPlanMilestoneId(rs.getInt("student_plan_milestone_id"));
                    milestone.setStudentPlanId(rs.getInt("student_plan_id"));
                    milestone.setMilestoneNo(rs.getInt("milestone_no"));
                    milestone.setMilestoneDescription(rs.getString("milestone_description"));
                    milestone.setDurationDays(rs.getInt("duration_days"));
                    milestone.setStatus(rs.getString("status"));
                    milestone.setCompletedAt(rs.getTimestamp("completed_at"));
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findMilestoneById()");
            e.printStackTrace();
        }

        return milestone;
    }

    @Override
    public void updateMilestone(int milestoneId, String description, int durationDays) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_MILESTONE_SQL)) {

            ps.setString(1, description);
            ps.setInt(2, durationDays);
            ps.setInt(3, milestoneId);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in updateMilestone()");
            e.printStackTrace();
        }
    }

    @Override
    public void completeMilestone(int milestoneId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(COMPLETE_MILESTONE_SQL)) {

            ps.setInt(1, milestoneId);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in completeMilestone()");
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasPendingMilestones(int studentPlanId) {
        boolean hasPending = false;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(HAS_PENDING_MILESTONES_SQL)) {

            ps.setInt(1, studentPlanId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hasPending = rs.getInt("total") > 0;
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in hasPendingMilestones()");
            e.printStackTrace();
        }

        return hasPending;
    }

    @Override
    public void markPlanCompleted(int studentPlanId, int updatedBy) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(MARK_PLAN_COMPLETED_SQL)) {

            ps.setInt(1, updatedBy);
            ps.setInt(2, studentPlanId);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in markPlanCompleted()");
            e.printStackTrace();
        }
    }
    
    @Override
    public void deleteStudentPlanById(int studentPlanId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM student_plans WHERE student_plan_id = ?")) {

            ps.setInt(1, studentPlanId);

            int rows = ps.executeUpdate();
            System.out.println("Deleted student plan rows: " + rows);

        } catch (Exception e) {
            System.out.println("ERROR in deleteStudentPlanById()");
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean hasOutstandingRecoveryForCurrentTerm(String studentId) {
        boolean hasOutstanding = false;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(HAS_OUTSTANDING_RECOVERY_SQL)) {

            ps.setString(1, studentId);
            ps.setString(2, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hasOutstanding = rs.getInt("total") > 0;
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in hasOutstandingRecoveryForCurrentTerm()");
            e.printStackTrace();
        }

        return hasOutstanding;
    }

    @Override
    public void updateCurrentRecoveryProgressionToAwaitingRecheck(String studentId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_CURRENT_RECOVERY_TO_RECHECK_SQL)) {

            ps.setString(1, studentId);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in updateCurrentRecoveryProgressionToAwaitingRecheck()");
            e.printStackTrace();
        }
    }
    
    @Override
    public void completeRecoveryPlanWithResult(int studentPlanId,
                                               int studentCourseComponentId,
                                               double mark,
                                               String grade,
                                               double gradePoint,
                                               int updatedBy) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(UPDATE_STUDENT_COMPONENT_RESULT_SQL);
                 PreparedStatement ps2 = conn.prepareStatement(COMPLETE_PLAN_WITH_RESULT_SQL)) {

                // update student_course_components
                ps1.setDouble(1, mark);
                ps1.setString(2, grade);
                ps1.setDouble(3, gradePoint);
                ps1.setInt(4, studentCourseComponentId);
                ps1.executeUpdate();

                // update student_plans
                ps2.setString(1, grade);
                ps2.setDouble(2, gradePoint);
                ps2.setInt(3, updatedBy);
                ps2.setInt(4, studentPlanId);
                ps2.executeUpdate();

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            System.out.println("ERROR in completeRecoveryPlanWithResult()");
            e.printStackTrace();
        }
    }
    
    @Override
    public Integer findStudentCourseComponentIdByPlanId(int studentPlanId) {
        Integer componentId = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_COMPONENT_ID_BY_PLAN_ID_SQL)) {

            ps.setInt(1, studentPlanId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    componentId = rs.getInt("student_course_component_id");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findStudentCourseComponentIdByPlanId()");
            e.printStackTrace();
        }

        return componentId;
    }
    
    @Override
    public RecoveryAttemptInfo findAttemptInfoByStudentCourseComponentId(int studentCourseComponentId) {
        RecoveryAttemptInfo info = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ATTEMPT_INFO_BY_COMPONENT_SQL)) {

            ps.setInt(1, studentCourseComponentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    info = new RecoveryAttemptInfo();
                    info.setStudentId(rs.getString("student_id"));
                    info.setStudentCourseId(rs.getInt("student_course_id"));
                    info.setProgramCourseId(rs.getInt("program_course_id"));
                    info.setAttemptNo(rs.getInt("attempt_no"));
                    info.setCourseComponentId(rs.getInt("course_component_id"));
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findAttemptInfoByStudentCourseComponentId()");
            e.printStackTrace();
        }

        return info;
    }

    @Override
    public Integer findStudentCourseIdByAttempt(String studentId, int programCourseId, int attemptNo) {
        Integer studentCourseId = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_STUDENT_COURSE_ID_BY_ATTEMPT_SQL)) {

            ps.setString(1, studentId);
            ps.setInt(2, programCourseId);
            ps.setInt(3, attemptNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    studentCourseId = rs.getInt("student_course_id");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findStudentCourseIdByAttempt()");
            e.printStackTrace();
        }

        return studentCourseId;
    }

    @Override
    public int insertStudentCourseAttempt(String studentId, int programCourseId, int attemptNo) {
        int generatedId = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_STUDENT_COURSE_ATTEMPT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, studentId);
            ps.setInt(2, programCourseId);
            ps.setInt(3, attemptNo);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in insertStudentCourseAttempt()");
            e.printStackTrace();
        }

        return generatedId;
    }

    @Override
    public Integer findStudentCourseComponentIdByAttemptComponent(String studentId, int programCourseId, int attemptNo, int courseComponentId) {
        Integer studentCourseComponentId = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_COMPONENT_ID_BY_ATTEMPT_COMPONENT_SQL)) {

            ps.setString(1, studentId);
            ps.setInt(2, programCourseId);
            ps.setInt(3, attemptNo);
            ps.setInt(4, courseComponentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    studentCourseComponentId = rs.getInt("student_course_component_id");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findStudentCourseComponentIdByAttemptComponent()");
            e.printStackTrace();
        }

        return studentCourseComponentId;
    }

    @Override
    public int insertStudentCourseComponentAttempt(int studentCourseId, int courseComponentId) {
        int generatedId = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_STUDENT_COURSE_COMPONENT_ATTEMPT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, studentCourseId);
            ps.setInt(2, courseComponentId);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in insertStudentCourseComponentAttempt()");
            e.printStackTrace();
        }

        return generatedId;
    }

    @Override
    public Integer prepareNewRecoveryAttemptComponent(int studentCourseComponentId) {
        RecoveryAttemptInfo info = findAttemptInfoByStudentCourseComponentId(studentCourseComponentId);

        if (info == null) {
            return null;
        }

        int nextAttemptNo = info.getAttemptNo() + 1;

        if (nextAttemptNo > 3) {
            return null;
        }

        Integer newStudentCourseId = findStudentCourseIdByAttempt(
                info.getStudentId(),
                info.getProgramCourseId(),
                nextAttemptNo
        );

        if (newStudentCourseId == null) {
            newStudentCourseId = insertStudentCourseAttempt(
                    info.getStudentId(),
                    info.getProgramCourseId(),
                    nextAttemptNo
            );
        }

        Integer newStudentCourseComponentId = findStudentCourseComponentIdByAttemptComponent(
                info.getStudentId(),
                info.getProgramCourseId(),
                nextAttemptNo,
                info.getCourseComponentId()
        );

        if (newStudentCourseComponentId == null) {
            newStudentCourseComponentId = insertStudentCourseComponentAttempt(
                    newStudentCourseId,
                    info.getCourseComponentId()
            );
        }

        return newStudentCourseComponentId;
    }
    
    @Override
    public void cloneComponentsFromPreviousAttempt(int oldStudentCourseId, int newStudentCourseId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CLONE_COMPONENTS_FROM_PREVIOUS_ATTEMPT_SQL)) {

            ps.setInt(1, newStudentCourseId);
            ps.setInt(2, oldStudentCourseId);
            ps.setInt(3, newStudentCourseId);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in cloneComponentsFromPreviousAttempt()");
            e.printStackTrace();
        }
    }
    
    @Override
    public Integer findStudentCourseIdByPlanId(int studentPlanId) {
        Integer studentCourseId = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_STUDENT_COURSE_ID_BY_PLAN_ID_SQL)) {

            ps.setInt(1, studentPlanId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    studentCourseId = rs.getInt("student_course_id");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findStudentCourseIdByPlanId()");
            e.printStackTrace();
        }

        return studentCourseId;
    }

    @Override
    public void updateStudentCourseResult(int studentCourseId, String grade, double gradePoint) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STUDENT_COURSE_RESULT_SQL)) {

            ps.setString(1, grade);
            ps.setDouble(2, gradePoint);
            ps.setInt(3, studentCourseId);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in updateStudentCourseResult()");
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasFailedComponentInStudentCourse(int studentCourseId) {
        boolean hasFailed = false;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(HAS_FAILED_COMPONENT_IN_STUDENT_COURSE_SQL)) {

            ps.setInt(1, studentCourseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hasFailed = rs.getInt("total") > 0;
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in hasFailedComponentInStudentCourse()");
            e.printStackTrace();
        }

        return hasFailed;
    }

    @Override
    public double calculateWeightedMarkForStudentCourse(int studentCourseId) {
        double weightedMark = 0.0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CALCULATE_WEIGHTED_MARK_FOR_STUDENT_COURSE_SQL)) {

            ps.setInt(1, studentCourseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    weightedMark = rs.getDouble("weighted_mark");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in calculateWeightedMarkForStudentCourse()");
            e.printStackTrace();
        }

        return weightedMark;
    }
    
    private static final String FIND_ATTEMPT_NO_BY_STUDENT_COURSE_ID_SQL =
            "SELECT attempt_no FROM student_courses WHERE student_course_id = ?";
    
    @Override
    public int findAttemptNoByStudentCourseId(int studentCourseId) {
        int attemptNo = 1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ATTEMPT_NO_BY_STUDENT_COURSE_ID_SQL)) {

            ps.setInt(1, studentCourseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    attemptNo = rs.getInt("attempt_no");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findAttemptNoByStudentCourseId()");
            e.printStackTrace();
        }

        return attemptNo;
    }
}