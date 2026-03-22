package com.crs.dao.impl;

import com.crs.dao.ProgressionEnrolmentDAO;
import com.crs.model.EligibilityRecord;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProgressionEnrolmentDAOImpl implements ProgressionEnrolmentDAO {

    private static final String BASE_SELECT_SQL =
            "SELECT " +
            "    pe.progression_id, " +
            "    s.student_id, " +
            "    s.student_name, " +
            "    s.email AS student_email, " +
            "    p.program_name, " +
            "    i.intake_name, " +
            "    cy.year_name AS current_year_name, " +
            "    cs.semester_name AS current_semester_name, " +
            "    ny.year_name AS next_year_name, " +
            "    ns.semester_name AS next_semester_name, " +
            "    pe.cgpa, " +
            "    pe.failed_course_count, " +
            "    pe.status, " +
            "    pe.reason " +
            "FROM progression_enrolments pe " +
            "JOIN students s ON pe.student_id = s.student_id " +
            "JOIN programs p ON s.program_id = p.program_id " +
            "JOIN intakes i ON p.intake_id = i.intake_id " +
            "JOIN years cy ON pe.current_year_id = cy.year_id " +
            "JOIN semesters cs ON pe.current_semester_id = cs.semester_id " +
            "LEFT JOIN years ny ON pe.next_year_id = ny.year_id " +
            "LEFT JOIN semesters ns ON pe.next_semester_id = ns.semester_id ";

    private static final String ORDER_BY_SQL =
            " ORDER BY p.program_name, i.intake_name, cy.year_order, cs.semester_order, s.student_id";

    private static final String FIND_AWAITING_SQL =
            BASE_SELECT_SQL +
            "WHERE pe.status = 'AWAITING_CHECK'" +
            ORDER_BY_SQL;

    private static final String FIND_PENDING_APPROVAL_SQL =
            BASE_SELECT_SQL +
            "WHERE pe.status = 'PENDING_APPROVAL'" +
            ORDER_BY_SQL;

    private static final String FIND_RECOVERY_QUEUE_SQL =
            BASE_SELECT_SQL +
            "WHERE pe.status = 'PENDING_RECOVERY'" +
            ORDER_BY_SQL;

    private static final String FIND_PROCESSED_SQL =
            BASE_SELECT_SQL +
            "WHERE pe.status IN ('APPROVED', 'SENT_TO_RECOVERY')" +
            ORDER_BY_SQL;
    
    private static final String UPDATE_AFTER_CHECK_SQL =
    	    "UPDATE progression_enrolments " +
    	    "SET status = ?, reason = ?, checked_by = ?, checked_at = NOW() " +
    	    "WHERE progression_id = ?";
    
    private static final String APPROVE_ENROLMENT_SQL =
            "UPDATE progression_enrolments " +
            "SET status = 'APPROVED', approved_by = ?, approved_at = NOW() " +
            "WHERE progression_id = ?";

    @Override
    public List<EligibilityRecord> findAwaitingCheckRecords() {
        return findBySql(FIND_AWAITING_SQL);
    }

    @Override
    public List<EligibilityRecord> findPendingApprovalRecords() {
        return findBySql(FIND_PENDING_APPROVAL_SQL);
    }

    @Override
    public List<EligibilityRecord> findRecoveryQueueRecords() {
        return findBySql(FIND_RECOVERY_QUEUE_SQL);
    }

    @Override
    public List<EligibilityRecord> findProcessedRecords() {
        return findBySql(FIND_PROCESSED_SQL);
    }

    private List<EligibilityRecord> findBySql(String sql) {
        List<EligibilityRecord> recordList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EligibilityRecord record = new EligibilityRecord();
                record.setProgressionId(rs.getInt("progression_id"));
                record.setStudentId(rs.getString("student_id"));
                record.setStudentName(rs.getString("student_name"));
                record.setStudentEmail(rs.getString("student_email"));
                record.setProgrammeName(rs.getString("program_name"));
                record.setIntakeName(rs.getString("intake_name"));
                record.setCurrentYearName(rs.getString("current_year_name"));
                record.setCurrentSemesterName(rs.getString("current_semester_name"));
                record.setNextYearName(rs.getString("next_year_name"));
                record.setNextSemesterName(rs.getString("next_semester_name"));
                record.setCgpa(rs.getDouble("cgpa"));
                record.setFailedCourseCount(rs.getInt("failed_course_count"));
                record.setStatus(rs.getString("status"));
                record.setReason(rs.getString("reason"));

                recordList.add(record);
            }

        } catch (Exception e) {
            System.out.println("ERROR in ProgressionEnrolmentDAOImpl.findBySql()");
            e.printStackTrace();
        }

        return recordList;
    }
    
    @Override
    public void updateAfterCheck(int progressionId, String status, String reason, int checkedBy) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_AFTER_CHECK_SQL)) {

            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setInt(3, checkedBy);
            ps.setInt(4, progressionId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void approveEnrolment(int progressionId, int approvedBy) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(APPROVE_ENROLMENT_SQL)) {

            ps.setInt(1, approvedBy);
            ps.setInt(2, progressionId);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in approveEnrolment()");
            e.printStackTrace();
        }
    }
}