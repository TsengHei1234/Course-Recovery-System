package com.crs.dao.impl;

import com.crs.dao.AcademicReportDAO;
import com.crs.model.AcademicReportCourseRow;
import com.crs.model.AcademicReportStudent;
import com.crs.model.SelectionOption;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AcademicReportDAOImpl implements AcademicReportDAO {

    private static final String PROGRAMME_OPTIONS_SQL =
            "SELECT DISTINCT p.program_code, p.program_name " +
            "FROM programs p " +
            "ORDER BY p.program_code, p.program_name";

    private static final String INTAKE_OPTIONS_SQL =
            "SELECT intake_id, intake_name FROM intakes ORDER BY intake_id";

    private static final String YEAR_OPTIONS_SQL =
            "SELECT year_id, year_name FROM years ORDER BY year_order, year_id";

    private static final String SEMESTER_OPTIONS_SQL =
            "SELECT semester_id, semester_name FROM semesters ORDER BY semester_order, semester_id";

    private static final String STUDENT_LIST_SQL =
            "SELECT s.student_id, s.student_name, s.email, " +
            "       p.program_id, p.program_code, p.program_name, " +
            "       i.intake_id, i.intake_name, " +
            "       y.year_id, y.year_name, " +
            "       sem.semester_id, sem.semester_name " +
            "FROM students s " +
            "JOIN programs p ON p.program_id = s.program_id " +
            "JOIN intakes i ON i.intake_id = p.intake_id " +
            "JOIN years y ON y.year_id = s.year_id " +
            "JOIN semesters sem ON sem.semester_id = s.semester_id " +
            "WHERE (? IS NULL OR p.program_code = ?) " +
            "  AND (? IS NULL OR i.intake_id = ?) " +
            "  AND (? IS NULL OR y.year_id = ?) " +
            "  AND (? IS NULL OR sem.semester_id = ?) " +
            "  AND (? IS NULL OR s.student_id LIKE CONCAT('%', ?, '%') OR s.student_name LIKE CONCAT('%', ?, '%')) " +
            "ORDER BY s.student_id";

    private static final String STUDENT_BY_ID_SQL =
            "SELECT s.student_id, s.student_name, s.email, " +
            "       p.program_id, p.program_code, p.program_name, " +
            "       i.intake_id, i.intake_name, " +
            "       y.year_id, y.year_name, " +
            "       sem.semester_id, sem.semester_name " +
            "FROM students s " +
            "JOIN programs p ON p.program_id = s.program_id " +
            "JOIN intakes i ON i.intake_id = p.intake_id " +
            "JOIN years y ON y.year_id = s.year_id " +
            "JOIN semesters sem ON sem.semester_id = s.semester_id " +
            "WHERE s.student_id = ?";

    private static final String TERM_COURSE_ROWS_SQL =
            "SELECT c.course_code, c.course_name, c.credit_hour, sc.grade, sc.grade_point " +
            "FROM student_courses sc " +
            "JOIN ( " +
            "    SELECT program_course_id, MAX(attempt_no) AS latest_attempt " +
            "    FROM student_courses " +
            "    WHERE student_id = ? " +
            "    GROUP BY program_course_id " +
            ") latest ON latest.program_course_id = sc.program_course_id " +
            "         AND latest.latest_attempt = sc.attempt_no " +
            "JOIN program_courses pc ON pc.program_course_id = sc.program_course_id " +
            "JOIN courses c ON c.course_id = pc.course_id " +
            "WHERE sc.student_id = ? " +
            "  AND pc.year_id = ? " +
            "  AND pc.semester_id = ? " +
            "ORDER BY c.course_code";

    private static final String SEMESTER_GPA_SQL =
            "SELECT COALESCE(ROUND(SUM(c.credit_hour * sc.grade_point) / NULLIF(SUM(c.credit_hour), 0), 2), 0) AS semester_gpa " +
            "FROM student_courses sc " +
            "JOIN ( " +
            "    SELECT program_course_id, MAX(attempt_no) AS latest_attempt " +
            "    FROM student_courses " +
            "    WHERE student_id = ? " +
            "    GROUP BY program_course_id " +
            ") latest ON latest.program_course_id = sc.program_course_id " +
            "         AND latest.latest_attempt = sc.attempt_no " +
            "JOIN program_courses pc ON pc.program_course_id = sc.program_course_id " +
            "JOIN courses c ON c.course_id = pc.course_id " +
            "WHERE sc.student_id = ? " +
            "  AND pc.year_id = ? " +
            "  AND pc.semester_id = ?";

    private static final String CGPA_SQL =
            "SELECT COALESCE(ROUND(SUM(c.credit_hour * sc.grade_point) / NULLIF(SUM(c.credit_hour), 0), 2), 0) AS cgpa " +
            "FROM student_courses sc " +
            "JOIN ( " +
            "    SELECT program_course_id, MAX(attempt_no) AS latest_attempt " +
            "    FROM student_courses " +
            "    WHERE student_id = ? " +
            "    GROUP BY program_course_id " +
            ") latest ON latest.program_course_id = sc.program_course_id " +
            "         AND latest.latest_attempt = sc.attempt_no " +
            "JOIN program_courses pc ON pc.program_course_id = sc.program_course_id " +
            "JOIN courses c ON c.course_id = pc.course_id " +
            "WHERE sc.student_id = ?";

    private static final String YEAR_NAME_SQL = "SELECT year_name FROM years WHERE year_id = ?";
    private static final String SEMESTER_NAME_SQL = "SELECT semester_name FROM semesters WHERE semester_id = ?";

    @Override
    public List<SelectionOption> findProgrammeOptions() {
        List<SelectionOption> options = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(PROGRAMME_OPTIONS_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                options.add(new SelectionOption(
                        resultSet.getString("program_code"),
                        resultSet.getString("program_code") + " - " + resultSet.getString("program_name")
                ));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return options;
    }

    @Override
    public List<SelectionOption> findIntakeOptions() {
        return findOptionList(INTAKE_OPTIONS_SQL, "intake_id", "intake_name");
    }

    @Override
    public List<SelectionOption> findYearOptions() {
        return findOptionList(YEAR_OPTIONS_SQL, "year_id", "year_name");
    }

    @Override
    public List<SelectionOption> findSemesterOptions() {
        return findOptionList(SEMESTER_OPTIONS_SQL, "semester_id", "semester_name");
    }

    @Override
    public List<AcademicReportStudent> findStudents(String programmeCode, Integer intakeId, Integer yearId, Integer semesterId, String studentSearch) {
        List<AcademicReportStudent> students = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(STUDENT_LIST_SQL)) {

            int parameterIndex = 1;
            preparedStatement.setString(parameterIndex++, programmeCode);
            preparedStatement.setString(parameterIndex++, programmeCode);
            setNullableInteger(preparedStatement, parameterIndex++, intakeId);
            setNullableInteger(preparedStatement, parameterIndex++, intakeId);
            setNullableInteger(preparedStatement, parameterIndex++, yearId);
            setNullableInteger(preparedStatement, parameterIndex++, yearId);
            setNullableInteger(preparedStatement, parameterIndex++, semesterId);
            setNullableInteger(preparedStatement, parameterIndex++, semesterId);
            preparedStatement.setString(parameterIndex++, studentSearch);
            preparedStatement.setString(parameterIndex++, studentSearch);
            preparedStatement.setString(parameterIndex, studentSearch);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return students;
    }

    @Override
    public AcademicReportStudent findStudentById(String studentId) {
        AcademicReportStudent student = null;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(STUDENT_BY_ID_SQL)) {

            preparedStatement.setString(1, studentId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    student = mapStudent(resultSet);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return student;
    }

    @Override
    public List<AcademicReportCourseRow> findLatestTermCourseRows(String studentId, int yearId, int semesterId) {
        List<AcademicReportCourseRow> courseRows = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(TERM_COURSE_ROWS_SQL)) {

            preparedStatement.setString(1, studentId);
            preparedStatement.setString(2, studentId);
            preparedStatement.setInt(3, yearId);
            preparedStatement.setInt(4, semesterId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    AcademicReportCourseRow courseRow = new AcademicReportCourseRow();
                    courseRow.setCourseCode(resultSet.getString("course_code"));
                    courseRow.setCourseName(resultSet.getString("course_name"));
                    courseRow.setCreditHour(resultSet.getInt("credit_hour"));
                    courseRow.setGrade(resultSet.getString("grade"));
                    courseRow.setGradePoint(resultSet.getDouble("grade_point"));
                    courseRows.add(courseRow);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return courseRows;
    }

    @Override
    public double findSemesterGpa(String studentId, int yearId, int semesterId) {
        return findSingleDouble(SEMESTER_GPA_SQL, studentId, yearId, semesterId);
    }

    @Override
    public double findCgpa(String studentId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CGPA_SQL)) {

            preparedStatement.setString(1, studentId);
            preparedStatement.setString(2, studentId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("cgpa");
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return 0;
    }

    @Override
    public String findYearNameById(int yearId) {
        return findSingleLabel(YEAR_NAME_SQL, yearId, "year_name");
    }

    @Override
    public String findSemesterNameById(int semesterId) {
        return findSingleLabel(SEMESTER_NAME_SQL, semesterId, "semester_name");
    }

    private List<SelectionOption> findOptionList(String sql, String valueColumn, String labelColumn) {
        List<SelectionOption> options = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                options.add(new SelectionOption(
                        String.valueOf(resultSet.getInt(valueColumn)),
                        resultSet.getString(labelColumn)
                ));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return options;
    }

    private AcademicReportStudent mapStudent(ResultSet resultSet) throws Exception {
        AcademicReportStudent student = new AcademicReportStudent();
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
        return student;
    }

    private void setNullableInteger(PreparedStatement preparedStatement, int parameterIndex, Integer value) throws Exception {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, java.sql.Types.INTEGER);
        } else {
            preparedStatement.setInt(parameterIndex, value);
        }
    }

    private double findSingleDouble(String sql, String studentId, int yearId, int semesterId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, studentId);
            preparedStatement.setString(2, studentId);
            preparedStatement.setInt(3, yearId);
            preparedStatement.setInt(4, semesterId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return 0;
    }

    private String findSingleLabel(String sql, int id, String columnName) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(columnName);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return "";
    }
}
