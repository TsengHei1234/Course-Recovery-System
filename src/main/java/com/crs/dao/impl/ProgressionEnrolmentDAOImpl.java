package com.crs.dao.impl;

import com.crs.dao.ProgressionEnrolmentDAO;
import com.crs.model.EligibilityRecord;
import com.crs.model.ProgressionEnrolment;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
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

    private static final String UPDATE_AFTER_CHECK_SQL =
            "UPDATE progression_enrolments " +
            "SET cgpa = ?, failed_course_count = ?, status = ?, reason = ?, checked_by = ?, checked_at = NOW() " +
            "WHERE progression_id = ?";

    private static final String APPROVE_ENROLMENT_SQL =
            "UPDATE progression_enrolments " +
            "SET status = 'APPROVED', approved_by = ?, approved_at = NOW() " +
            "WHERE progression_id = ?";

    private static final String COMPLETE_STUDY_SQL =
            "UPDATE progression_enrolments " +
            "SET status = 'COMPLETED_STUDY', approved_by = ?, approved_at = NOW() " +
            "WHERE progression_id = ?";

    private static final String SEND_TO_RECOVERY_SQL =
            "UPDATE progression_enrolments " +
            "SET status = 'SENT_TO_RECOVERY', sent_to_recovery_by = ?, sent_to_recovery_at = NOW() " +
            "WHERE progression_id = ?";

    private static final String FIND_BY_ID_SQL =
            "SELECT progression_id, student_id, current_year_id, current_semester_id, " +
            "       next_year_id, next_semester_id, cgpa, failed_course_count, status, reason " +
            "FROM progression_enrolments WHERE progression_id = ?";

    private static final String FIND_YEAR_ID_BY_ORDER_SQL =
            "SELECT year_id FROM years WHERE year_order = ?";

    private static final String FIND_SEMESTER_ID_BY_ORDER_SQL =
            "SELECT semester_id FROM semesters WHERE semester_order = ?";

    private static final String EXISTS_PROGRESSION_FOR_TERM_SQL =
            "SELECT COUNT(*) AS total " +
            "FROM progression_enrolments " +
            "WHERE student_id = ? AND current_year_id = ? AND current_semester_id = ?";

    private static final String INSERT_NEXT_PROGRESSION_SQL =
            "INSERT INTO progression_enrolments " +
            "(student_id, current_year_id, current_semester_id, next_year_id, next_semester_id, status) " +
            "VALUES (?, ?, ?, ?, ?, 'STUDYING')";

    private static final String FIND_PROGRAMME_OPTIONS_SQL =
            "SELECT program_name FROM programs ORDER BY program_name";

    private static final String FIND_INTAKE_OPTIONS_SQL =
            "SELECT intake_name FROM intakes ORDER BY intake_name";

    private static final String FIND_YEAR_OPTIONS_SQL =
            "SELECT year_name FROM years ORDER BY year_order";

    private static final String FIND_SEMESTER_OPTIONS_SQL =
            "SELECT semester_name FROM semesters ORDER BY semester_order";

    private static final String CALCULATE_CGPA_SQL =
            "SELECT COALESCE(AVG(course_gp), 0) AS cgpa " +
            "FROM ( " +
            "    SELECT " +
            "        x.program_course_id, " +
            "        CASE " +
            "            WHEN x.weighted_mark >= 80 THEN 4.00 " +
            "            WHEN x.weighted_mark >= 75 THEN 3.67 " +
            "            WHEN x.weighted_mark >= 70 THEN 3.33 " +
            "            WHEN x.weighted_mark >= 65 THEN 3.00 " +
            "            WHEN x.weighted_mark >= 60 THEN 2.67 " +
            "            WHEN x.weighted_mark >= 55 THEN 2.33 " +
            "            WHEN x.weighted_mark >= 50 THEN 2.00 " +
            "            WHEN x.weighted_mark >= 45 THEN 1.67 " +
            "            WHEN x.weighted_mark >= 40 THEN 1.00 " +
            "            ELSE 0.00 " +
            "        END AS course_gp " +
            "    FROM ( " +
            "        SELECT " +
            "            sc.program_course_id, " +
            "            SUM(COALESCE(scc.mark, 0) * COALESCE(cc.weight_percent, 0) / 100) AS weighted_mark " +
            "        FROM student_courses sc " +
            "        JOIN student_course_components scc ON scc.student_course_id = sc.student_course_id " +
            "        JOIN course_components cc ON scc.course_component_id = cc.course_component_id " +
            "        JOIN ( " +
            "            SELECT " +
            "                sc2.student_id, " +
            "                sc2.program_course_id, " +
            "                scc2.course_component_id, " +
            "                MAX(sc2.attempt_no) AS max_attempt " +
            "            FROM student_courses sc2 " +
            "            JOIN student_course_components scc2 ON scc2.student_course_id = sc2.student_course_id " +
            "            WHERE sc2.student_id = ? " +
            "            GROUP BY sc2.student_id, sc2.program_course_id, scc2.course_component_id " +
            "        ) latest " +
            "          ON sc.student_id = latest.student_id " +
            "         AND sc.program_course_id = latest.program_course_id " +
            "         AND sc.attempt_no = latest.max_attempt " +
            "         AND scc.course_component_id = latest.course_component_id " +
            "        WHERE sc.student_id = ? " +
            "        GROUP BY sc.program_course_id " +
            "    ) x " +
            ") y";

    private static final String CALCULATE_FAILED_COURSE_COUNT_SQL =
            "SELECT COUNT(*) AS total " +
            "FROM ( " +
            "    SELECT " +
            "        sc.program_course_id, " +
            "        SUM(COALESCE(scc.mark, 0) * COALESCE(cc.weight_percent, 0) / 100) AS weighted_mark " +
            "    FROM student_courses sc " +
            "    JOIN student_course_components scc ON scc.student_course_id = sc.student_course_id " +
            "    JOIN course_components cc ON scc.course_component_id = cc.course_component_id " +
            "    JOIN ( " +
            "        SELECT " +
            "            sc2.student_id, " +
            "            sc2.program_course_id, " +
            "            scc2.course_component_id, " +
            "            MAX(sc2.attempt_no) AS max_attempt " +
            "        FROM student_courses sc2 " +
            "        JOIN student_course_components scc2 ON scc2.student_course_id = sc2.student_course_id " +
            "        WHERE sc2.student_id = ? " +
            "        GROUP BY sc2.student_id, sc2.program_course_id, scc2.course_component_id " +
            "    ) latest " +
            "      ON sc.student_id = latest.student_id " +
            "     AND sc.program_course_id = latest.program_course_id " +
            "     AND sc.attempt_no = latest.max_attempt " +
            "     AND scc.course_component_id = latest.course_component_id " +
            "    WHERE sc.student_id = ? " +
            "    GROUP BY sc.program_course_id " +
            "    HAVING weighted_mark < 50 " +
            ") failed_courses";
    
    @Override
    public List<EligibilityRecord> findAwaitingCheckRecords(String programme, String intake, String yearName, String semesterName, String search) {
        return findByStatus("pe.status IN ('AWAITING_CHECK', 'AWAITING_RECHECK')", programme, intake, yearName, semesterName, search);
    }

    @Override
    public List<EligibilityRecord> findPendingApprovalRecords(String programme, String intake, String yearName, String semesterName, String search) {
        return findByStatus("pe.status = 'PENDING_APPROVAL'", programme, intake, yearName, semesterName, search);
    }

    @Override
    public List<EligibilityRecord> findRecoveryQueueRecords(String programme, String intake, String yearName, String semesterName, String search) {
        return findByStatus("pe.status = 'PENDING_RECOVERY'", programme, intake, yearName, semesterName, search);
    }

    @Override
    public List<EligibilityRecord> findProcessedRecords(String programme, String intake, String yearName, String semesterName, String search) {
        return findByStatus("pe.status IN ('APPROVED', 'SENT_TO_RECOVERY', 'COMPLETED_STUDY')", programme, intake, yearName, semesterName, search);
    }

    @Override
    public List<String> findProgrammeOptions() {
        return findSimpleOptions(FIND_PROGRAMME_OPTIONS_SQL, "program_name");
    }

    @Override
    public List<String> findIntakeOptions() {
        return findSimpleOptions(FIND_INTAKE_OPTIONS_SQL, "intake_name");
    }

    @Override
    public List<String> findYearOptions() {
        return findSimpleOptions(FIND_YEAR_OPTIONS_SQL, "year_name");
    }

    @Override
    public List<String> findSemesterOptions() {
        return findSimpleOptions(FIND_SEMESTER_OPTIONS_SQL, "semester_name");
    }

    @Override
    public double calculateCgpa(String studentId) {
        double cgpa = 0.0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CALCULATE_CGPA_SQL)) {

            ps.setString(1, studentId);
            ps.setString(2, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cgpa = rs.getDouble("cgpa");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in calculateCgpa()");
            e.printStackTrace();
        }

        return cgpa;
    }

    @Override
    public int calculateFailedCourseCount(String studentId) {
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CALCULATE_FAILED_COURSE_COUNT_SQL)) {

            ps.setString(1, studentId);
            ps.setString(2, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in calculateFailedCourseCount()");
            e.printStackTrace();
        }

        return total;
    }

    @Override
    public void updateAfterCheck(int progressionId, double cgpa, int failedCourseCount, String status, String reason, int checkedBy) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_AFTER_CHECK_SQL)) {

            ps.setDouble(1, cgpa);
            ps.setInt(2, failedCourseCount);
            ps.setString(3, status);
            ps.setString(4, reason);
            ps.setInt(5, checkedBy);
            ps.setInt(6, progressionId);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in updateAfterCheck()");
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

    @Override
    public void sendToRecovery(int progressionId, int sentBy) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEND_TO_RECOVERY_SQL)) {

            ps.setInt(1, sentBy);
            ps.setInt(2, progressionId);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in sendToRecovery()");
            e.printStackTrace();
        }
    }

    @Override
    public ProgressionEnrolment findById(int progressionId) {
        ProgressionEnrolment pe = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setInt(1, progressionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pe = new ProgressionEnrolment();

                    pe.setProgressionId(rs.getInt("progression_id"));
                    pe.setStudentId(rs.getString("student_id"));
                    pe.setCurrentYearId(rs.getInt("current_year_id"));
                    pe.setCurrentSemesterId(rs.getInt("current_semester_id"));

                    int nextYear = rs.getInt("next_year_id");
                    pe.setNextYearId(rs.wasNull() ? null : nextYear);

                    int nextSemester = rs.getInt("next_semester_id");
                    pe.setNextSemesterId(rs.wasNull() ? null : nextSemester);

                    double cgpa = rs.getDouble("cgpa");
                    pe.setCgpa(rs.wasNull() ? null : cgpa);

                    int failedCourseCount = rs.getInt("failed_course_count");
                    pe.setFailedCourseCount(rs.wasNull() ? null : failedCourseCount);

                    pe.setStatus(rs.getString("status"));
                    pe.setReason(rs.getString("reason"));
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findById()");
            e.printStackTrace();
        }

        return pe;
    }

    @Override
    public Integer[] findNextTerm(int currentYearId, int currentSemesterId) {
        Integer[] result = new Integer[2]; // [nextYearId, nextSemesterId]

        try (Connection conn = DBConnection.getConnection()) {

            int currentYearOrder = 0;
            int currentSemesterOrder = 0;
            int maxSemesterOrder = 0;

            try (PreparedStatement ps = conn.prepareStatement("SELECT year_order FROM years WHERE year_id = ?")) {
                ps.setInt(1, currentYearId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentYearOrder = rs.getInt("year_order");
                    } else {
                        return result;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("SELECT semester_order FROM semesters WHERE semester_id = ?")) {
                ps.setInt(1, currentSemesterId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentSemesterOrder = rs.getInt("semester_order");
                    } else {
                        return result;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("SELECT MAX(semester_order) AS max_semester_order FROM semesters");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    maxSemesterOrder = rs.getInt("max_semester_order");
                }
            }

            int nextYearOrder;
            int nextSemesterOrder;

            if (currentSemesterOrder < maxSemesterOrder) {
                nextYearOrder = currentYearOrder;
                nextSemesterOrder = currentSemesterOrder + 1;
            } else {
                nextYearOrder = currentYearOrder + 1;
                nextSemesterOrder = 1;
            }

            Integer nextYearId = null;
            try (PreparedStatement ps = conn.prepareStatement(FIND_YEAR_ID_BY_ORDER_SQL)) {
                ps.setInt(1, nextYearOrder);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nextYearId = rs.getInt("year_id");
                    }
                }
            }

            Integer nextSemesterId = null;
            if (nextYearId != null) {
                try (PreparedStatement ps = conn.prepareStatement(FIND_SEMESTER_ID_BY_ORDER_SQL)) {
                    ps.setInt(1, nextSemesterOrder);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            nextSemesterId = rs.getInt("semester_id");
                        }
                    }
                }
            }

            result[0] = nextYearId;
            result[1] = nextSemesterId;

        } catch (Exception e) {
            System.out.println("ERROR in findNextTerm()");
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean existsProgressionForTerm(String studentId, int currentYearId, int currentSemesterId) {
        boolean exists = false;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_PROGRESSION_FOR_TERM_SQL)) {

            ps.setString(1, studentId);
            ps.setInt(2, currentYearId);
            ps.setInt(3, currentSemesterId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt("total") > 0;
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in existsProgressionForTerm()");
            e.printStackTrace();
        }

        return exists;
    }

    @Override
    public void insertNextProgression(String studentId,
                                      Integer currentYearId,
                                      Integer currentSemesterId,
                                      Integer nextYearId,
                                      Integer nextSemesterId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_NEXT_PROGRESSION_SQL)) {

            ps.setString(1, studentId);
            ps.setInt(2, currentYearId);
            ps.setInt(3, currentSemesterId);

            if (nextYearId != null) {
                ps.setInt(4, nextYearId);
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (nextSemesterId != null) {
                ps.setInt(5, nextSemesterId);
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in insertNextProgression()");
            e.printStackTrace();
        }
    }
    
    @Override
    public void completeStudy(int progressionId, int approvedBy) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(COMPLETE_STUDY_SQL)) {

            ps.setInt(1, approvedBy);
            ps.setInt(2, progressionId);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in completeStudy()");
            e.printStackTrace();
        }
    }

    private List<EligibilityRecord> findByStatus(String statusCondition,
                                                 String programme,
                                                 String intake,
                                                 String yearName,
                                                 String semesterName,
                                                 String search) {
        StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
        List<Object> params = new ArrayList<>();

        sql.append("WHERE ").append(statusCondition);
        appendFilters(sql, params, programme, intake, yearName, semesterName, search);
        sql.append(ORDER_BY_SQL);

        return findRecords(sql.toString(), params);
    }

    private void appendFilters(StringBuilder sql,
                               List<Object> params,
                               String programme,
                               String intake,
                               String yearName,
                               String semesterName,
                               String search) {

        if (programme != null && !programme.isBlank()) {
            sql.append(" AND p.program_name = ?");
            params.add(programme);
        }

        if (intake != null && !intake.isBlank()) {
            sql.append(" AND i.intake_name = ?");
            params.add(intake);
        }

        if (yearName != null && !yearName.isBlank()) {
            sql.append(" AND cy.year_name = ?");
            params.add(yearName);
        }

        if (semesterName != null && !semesterName.isBlank()) {
            sql.append(" AND cs.semester_name = ?");
            params.add(semesterName);
        }

        if (search != null && !search.isBlank()) {
            sql.append(" AND (s.student_id LIKE ? OR s.student_name LIKE ?)");
            String keyword = "%" + search.trim() + "%";
            params.add(keyword);
            params.add(keyword);
        }
    }

    private List<EligibilityRecord> findRecords(String sql, List<Object> params) {
        List<EligibilityRecord> recordList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
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
            }

        } catch (Exception e) {
            System.out.println("ERROR in findRecords()");
            e.printStackTrace();
        }

        return recordList;
    }

    private List<String> findSimpleOptions(String sql, String columnName) {
        List<String> options = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                options.add(rs.getString(columnName));
            }

        } catch (Exception e) {
            System.out.println("ERROR in findSimpleOptions()");
            e.printStackTrace();
        }

        return options;
    }
    
    private static final String FIND_YEAR_NAME_SQL =
            "SELECT year_name FROM years WHERE year_id = ?";

    private static final String FIND_SEMESTER_NAME_SQL =
            "SELECT semester_name FROM semesters WHERE semester_id = ?";
    
    @Override
    public String findYearNameById(Integer yearId) {
        if (yearId == null) {
            return null;
        }

        String yearName = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_YEAR_NAME_SQL)) {

            ps.setInt(1, yearId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    yearName = rs.getString("year_name");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findYearNameById()");
            e.printStackTrace();
        }

        return yearName;
    }

    @Override
    public String findSemesterNameById(Integer semesterId) {
        if (semesterId == null) {
            return null;
        }

        String semesterName = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_SEMESTER_NAME_SQL)) {

            ps.setInt(1, semesterId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    semesterName = rs.getString("semester_name");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findSemesterNameById()");
            e.printStackTrace();
        }

        return semesterName;
    }
}