package com.crs.dao.impl;

import com.crs.dao.StudentDAO;
import com.crs.model.ProgrammeRecord;
import com.crs.model.ReferenceOption;
import com.crs.model.StudentDirectoryRecord;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class StudentDAOImpl implements StudentDAO {

    private static final String FIND_STUDENTS_SQL =
            "SELECT st.student_id, st.student_name, st.email, " +
            "       p.program_id, p.program_code, p.program_name, " +
            "       i.intake_id, i.intake_name, y.year_id, y.year_name, s.semester_id, s.semester_name " +
            "FROM students st " +
            "JOIN programs p ON st.program_id = p.program_id " +
            "JOIN intakes i ON p.intake_id = i.intake_id " +
            "JOIN years y ON st.year_id = y.year_id " +
            "JOIN semesters s ON st.semester_id = s.semester_id " +
            "WHERE (? IS NULL OR ? = '' OR p.program_code = ?) " +
            "AND (? IS NULL OR p.intake_id = ?) " +
            "AND (? IS NULL OR st.year_id = ?) " +
            "AND (? IS NULL OR st.semester_id = ?) " +
            "AND (? IS NULL OR ? = '' OR LOWER(CONCAT(st.student_id, ' ', st.student_name)) LIKE ?) " +
            "ORDER BY st.student_id";

    private static final String FIND_PROGRAMME_OPTIONS_SQL =
            "SELECT MIN(p.program_id) AS program_id, p.program_code, p.program_name " +
            "FROM programs p " +
            "GROUP BY p.program_code, p.program_name " +
            "ORDER BY p.program_code";

    private static final String FIND_ALL_INTAKES_SQL =
            "SELECT intake_id, intake_name FROM intakes ORDER BY intake_id DESC";

    private static final String FIND_ALL_YEARS_SQL =
            "SELECT year_id, year_name FROM years ORDER BY year_order";

    private static final String FIND_ALL_SEMESTERS_SQL =
            "SELECT semester_id, semester_name FROM semesters ORDER BY semester_order";

    private static final String UPDATE_STUDENT_TERM_SQL =
            "UPDATE students SET year_id = ?, semester_id = ? WHERE student_id = ?";

    private static final String FIND_STUDENT_EMAIL_SQL =
            "SELECT email FROM students WHERE student_id = ?";

    private static final String FIND_STUDENT_NAME_SQL =
            "SELECT student_name FROM students WHERE student_id = ?";

    private static final String FIND_PROGRAM_NAME_SQL =
            "SELECT p.program_name " +
            "FROM students s " +
            "JOIN programs p ON s.program_id = p.program_id " +
            "WHERE s.student_id = ?";

    @Override
    public List<StudentDirectoryRecord> findStudents(String programCode,
                                                     Integer intakeId,
                                                     Integer yearId,
                                                     Integer semesterId,
                                                     String searchTerm) {
        List<StudentDirectoryRecord> studentList = new ArrayList<>();
        String safeProgramCode = normalizeSearch(programCode);
        String safeSearch = normalizeSearch(searchTerm);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENTS_SQL)) {

            if (safeProgramCode == null) {
                preparedStatement.setNull(1, Types.VARCHAR);
                preparedStatement.setNull(2, Types.VARCHAR);
                preparedStatement.setNull(3, Types.VARCHAR);
            } else {
                preparedStatement.setString(1, safeProgramCode);
                preparedStatement.setString(2, safeProgramCode);
                preparedStatement.setString(3, safeProgramCode);
            }

            if (intakeId == null) {
                preparedStatement.setNull(4, Types.INTEGER);
                preparedStatement.setNull(5, Types.INTEGER);
            } else {
                preparedStatement.setInt(4, intakeId);
                preparedStatement.setInt(5, intakeId);
            }

            if (yearId == null) {
                preparedStatement.setNull(6, Types.INTEGER);
                preparedStatement.setNull(7, Types.INTEGER);
            } else {
                preparedStatement.setInt(6, yearId);
                preparedStatement.setInt(7, yearId);
            }

            if (semesterId == null) {
                preparedStatement.setNull(8, Types.INTEGER);
                preparedStatement.setNull(9, Types.INTEGER);
            } else {
                preparedStatement.setInt(8, semesterId);
                preparedStatement.setInt(9, semesterId);
            }

            if (safeSearch == null) {
                preparedStatement.setNull(10, Types.VARCHAR);
                preparedStatement.setNull(11, Types.VARCHAR);
                preparedStatement.setNull(12, Types.VARCHAR);
            } else {
                preparedStatement.setString(10, safeSearch);
                preparedStatement.setString(11, safeSearch);
                preparedStatement.setString(12, "%" + safeSearch.toLowerCase() + "%");
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    StudentDirectoryRecord student = new StudentDirectoryRecord();
                    student.setStudentId(resultSet.getString("student_id"));
                    student.setStudentName(resultSet.getString("student_name"));
                    student.setEmail(resultSet.getString("email"));
                    student.setProgramId(resultSet.getInt("program_id"));
                    student.setProgramCode(resultSet.getString("program_code"));
                    student.setProgramName(resultSet.getString("program_name"));
                    student.setIntakeId(resultSet.getInt("intake_id"));
                    student.setIntakeName(resultSet.getString("intake_name"));
                    student.setYearId(resultSet.getInt("year_id"));
                    student.setYearName(resultSet.getString("year_name"));
                    student.setSemesterId(resultSet.getInt("semester_id"));
                    student.setSemesterName(resultSet.getString("semester_name"));
                    studentList.add(student);
                }
            }

        } catch (Exception exception) {
            System.out.println("ERROR in findStudents()");
            exception.printStackTrace();
        }

        return studentList;
    }

    @Override
    public List<ProgrammeRecord> findProgrammeOptions() {
        List<ProgrammeRecord> programmeList = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_PROGRAMME_OPTIONS_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                ProgrammeRecord programme = new ProgrammeRecord();
                programme.setProgramId(resultSet.getInt("program_id"));
                programme.setProgramCode(resultSet.getString("program_code"));
                programme.setProgramName(resultSet.getString("program_name"));
                programmeList.add(programme);
            }

        } catch (Exception exception) {
            System.out.println("ERROR in findProgrammeOptions() for Students");
            exception.printStackTrace();
        }

        return programmeList;
    }

    @Override
    public List<ReferenceOption> findAllIntakes() {
        return findReferenceOptions(FIND_ALL_INTAKES_SQL, "intake_id", "intake_name");
    }

    @Override
    public List<ReferenceOption> findAllYears() {
        return findReferenceOptions(FIND_ALL_YEARS_SQL, "year_id", "year_name");
    }

    @Override
    public List<ReferenceOption> findAllSemesters() {
        return findReferenceOptions(FIND_ALL_SEMESTERS_SQL, "semester_id", "semester_name");
    }

    @Override
    public void updateStudentTerm(String studentId, int yearId, int semesterId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STUDENT_TERM_SQL)) {

            ps.setInt(1, yearId);
            ps.setInt(2, semesterId);
            ps.setString(3, studentId);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("ERROR in updateStudentTerm()");
            e.printStackTrace();
        }
    }

    @Override
    public String findStudentEmailByStudentId(String studentId) {
        String email = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_STUDENT_EMAIL_SQL)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("email");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findStudentEmailByStudentId()");
            e.printStackTrace();
        }

        return email;
    }

    @Override
    public String findStudentNameByStudentId(String studentId) {
        String name = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_STUDENT_NAME_SQL)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("student_name");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findStudentNameByStudentId()");
            e.printStackTrace();
        }

        return name;
    }

    @Override
    public String findProgramNameByStudentId(String studentId) {
        String programName = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_PROGRAM_NAME_SQL)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    programName = rs.getString("program_name");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findProgramNameByStudentId()");
            e.printStackTrace();
        }

        return programName;
    }

    private List<ReferenceOption> findReferenceOptions(String sql, String idColumn, String labelColumn) {
        List<ReferenceOption> options = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                options.add(new ReferenceOption(resultSet.getInt(idColumn), resultSet.getString(labelColumn)));
            }

        } catch (Exception exception) {
            System.out.println("ERROR in findReferenceOptions() for Students");
            exception.printStackTrace();
        }

        return options;
    }

    private String normalizeSearch(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
