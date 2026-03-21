package com.crs.dao.impl;

import com.crs.dao.ProgramCourseDAO;
import com.crs.model.CourseRecord;
import com.crs.model.ProgrammeRecord;
import com.crs.model.ProgrammeStructureGroup;
import com.crs.model.ReferenceOption;
import com.crs.model.StudentCurriculumFocus;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProgramCourseDAOImpl implements ProgramCourseDAO {

    private static final String FIND_PROGRAMMES_SQL =
            "SELECT p.program_id, p.program_code, p.program_name, m.major_name, l.level_name, i.intake_id, i.intake_name " +
            "FROM programs p " +
            "JOIN majors m ON p.major_id = m.major_id " +
            "JOIN levels l ON p.level_id = l.level_id " +
            "JOIN intakes i ON p.intake_id = i.intake_id " +
            "WHERE (? IS NULL OR p.intake_id = ?) " +
            "AND (? IS NULL OR ? = '' OR LOWER(CONCAT(p.program_code, ' ', p.program_name)) LIKE ?) " +
            "ORDER BY p.program_code, i.intake_name";

    private static final String FIND_PROGRAMME_OPTIONS_SQL =
            "SELECT MIN(p.program_id) AS program_id, p.program_code, p.program_name " +
            "FROM programs p " +
            "GROUP BY p.program_code, p.program_name " +
            "ORDER BY p.program_code";

    private static final String FIND_COURSES_SQL =
            "SELECT c.course_id, c.course_code, c.course_name, c.credit_hour " +
            "FROM courses c " +
            "WHERE (? IS NULL OR ? = '' OR LOWER(CONCAT(c.course_code, ' ', c.course_name)) LIKE ?) " +
            "ORDER BY c.course_code";

    private static final String FIND_PROGRAMME_STRUCTURES_SQL =
            "SELECT p.program_code, p.program_name, i.intake_id, i.intake_name, " +
            "       y.year_id, y.year_name, y.year_order, s.semester_id, s.semester_name, s.semester_order, " +
            "       c.course_id, c.course_code, c.course_name, c.credit_hour, " +
            "       COALESCE(GROUP_CONCAT(cc.course_component_name ORDER BY cc.course_component_name SEPARATOR ', '), '-') AS assessment_components " +
            "FROM program_courses pc " +
            "JOIN programs p ON pc.program_id = p.program_id " +
            "JOIN intakes i ON p.intake_id = i.intake_id " +
            "JOIN years y ON pc.year_id = y.year_id " +
            "JOIN semesters s ON pc.semester_id = s.semester_id " +
            "JOIN courses c ON pc.course_id = c.course_id " +
            "LEFT JOIN course_components cc ON c.course_id = cc.course_id " +
            "WHERE (? IS NULL OR ? = '' OR p.program_code = ?) " +
            "AND (? IS NULL OR p.intake_id = ?) " +
            "AND (? IS NULL OR pc.year_id = ?) " +
            "AND (? IS NULL OR pc.semester_id = ?) " +
            "AND (? IS NULL OR ? = '' OR LOWER(CONCAT(c.course_code, ' ', c.course_name)) LIKE ?) " +
            "GROUP BY p.program_code, p.program_name, i.intake_id, i.intake_name, y.year_id, y.year_name, y.year_order, " +
            "         s.semester_id, s.semester_name, s.semester_order, c.course_id, c.course_code, c.course_name, c.credit_hour " +
            "ORDER BY p.program_code, i.intake_name, y.year_order, s.semester_order, c.course_code";

    private static final String FIND_ALL_INTAKES_SQL =
            "SELECT intake_id, intake_name FROM intakes ORDER BY intake_id DESC";

    private static final String FIND_ALL_YEARS_SQL =
            "SELECT year_id, year_name FROM years ORDER BY year_order";

    private static final String FIND_ALL_SEMESTERS_SQL =
            "SELECT semester_id, semester_name FROM semesters ORDER BY semester_order";

    private static final String FIND_STUDENT_CURRICULUM_FOCUS_SQL =
            "SELECT st.student_id, st.student_name, p.program_code, p.program_name, " +
            "       i.intake_id, i.intake_name, y.year_id, y.year_name, s.semester_id, s.semester_name " +
            "FROM students st " +
            "JOIN programs p ON st.program_id = p.program_id " +
            "JOIN intakes i ON p.intake_id = i.intake_id " +
            "JOIN years y ON st.year_id = y.year_id " +
            "JOIN semesters s ON st.semester_id = s.semester_id " +
            "WHERE st.student_id = ?";

    @Override
    public List<ProgrammeRecord> findProgrammes(Integer intakeId, String searchTerm) {
        List<ProgrammeRecord> programmeList = new ArrayList<>();
        String safeSearch = normalizeSearch(searchTerm);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_PROGRAMMES_SQL)) {

            if (intakeId == null) {
                preparedStatement.setNull(1, java.sql.Types.INTEGER);
                preparedStatement.setNull(2, java.sql.Types.INTEGER);
            } else {
                preparedStatement.setInt(1, intakeId);
                preparedStatement.setInt(2, intakeId);
            }

            if (safeSearch == null) {
                preparedStatement.setNull(3, java.sql.Types.VARCHAR);
                preparedStatement.setNull(4, java.sql.Types.VARCHAR);
                preparedStatement.setNull(5, java.sql.Types.VARCHAR);
            } else {
                preparedStatement.setString(3, safeSearch);
                preparedStatement.setString(4, safeSearch);
                preparedStatement.setString(5, "%" + safeSearch.toLowerCase() + "%");
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ProgrammeRecord programme = new ProgrammeRecord();
                    programme.setProgramId(resultSet.getInt("program_id"));
                    programme.setProgramCode(resultSet.getString("program_code"));
                    programme.setProgramName(resultSet.getString("program_name"));
                    programme.setMajorName(resultSet.getString("major_name"));
                    programme.setLevelName(resultSet.getString("level_name"));
                    programme.setIntakeId(resultSet.getInt("intake_id"));
                    programme.setIntakeName(resultSet.getString("intake_name"));
                    programmeList.add(programme);
                }
            }

        } catch (Exception exception) {
            System.out.println("ERROR in findProgrammes()");
            exception.printStackTrace();
        }

        return programmeList;
    }

    @Override
    public List<ProgrammeRecord> findProgrammeOptions() {
        List<ProgrammeRecord> programmeOptionList = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_PROGRAMME_OPTIONS_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                ProgrammeRecord programme = new ProgrammeRecord();
                programme.setProgramId(resultSet.getInt("program_id"));
                programme.setProgramCode(resultSet.getString("program_code"));
                programme.setProgramName(resultSet.getString("program_name"));
                programmeOptionList.add(programme);
            }

        } catch (Exception exception) {
            System.out.println("ERROR in findProgrammeOptions()");
            exception.printStackTrace();
        }

        return programmeOptionList;
    }

    @Override
    public List<CourseRecord> findCourses(String searchTerm) {
        List<CourseRecord> courseList = new ArrayList<>();
        String safeSearch = normalizeSearch(searchTerm);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_COURSES_SQL)) {

            if (safeSearch == null) {
                preparedStatement.setNull(1, java.sql.Types.VARCHAR);
                preparedStatement.setNull(2, java.sql.Types.VARCHAR);
                preparedStatement.setNull(3, java.sql.Types.VARCHAR);
            } else {
                preparedStatement.setString(1, safeSearch);
                preparedStatement.setString(2, safeSearch);
                preparedStatement.setString(3, "%" + safeSearch.toLowerCase() + "%");
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    CourseRecord course = new CourseRecord();
                    course.setCourseId(resultSet.getInt("course_id"));
                    course.setCourseCode(resultSet.getString("course_code"));
                    course.setCourseName(resultSet.getString("course_name"));
                    course.setCreditHour(resultSet.getInt("credit_hour"));
                    courseList.add(course);
                }
            }

        } catch (Exception exception) {
            System.out.println("ERROR in findCourses()");
            exception.printStackTrace();
        }

        return courseList;
    }

    @Override
    public List<ProgrammeStructureGroup> findProgrammeStructures(String programCode,
                                                                 Integer intakeId,
                                                                 Integer yearId,
                                                                 Integer semesterId,
                                                                 String courseSearch) {
        List<ProgrammeStructureGroup> structureGroupList = new ArrayList<>();
        String safeProgramCode = normalizeSearch(programCode);
        String safeCourseSearch = normalizeSearch(courseSearch);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_PROGRAMME_STRUCTURES_SQL)) {

            if (safeProgramCode == null) {
                preparedStatement.setNull(1, java.sql.Types.VARCHAR);
                preparedStatement.setNull(2, java.sql.Types.VARCHAR);
                preparedStatement.setNull(3, java.sql.Types.VARCHAR);
            } else {
                preparedStatement.setString(1, safeProgramCode);
                preparedStatement.setString(2, safeProgramCode);
                preparedStatement.setString(3, safeProgramCode);
            }

            if (intakeId == null) {
                preparedStatement.setNull(4, java.sql.Types.INTEGER);
                preparedStatement.setNull(5, java.sql.Types.INTEGER);
            } else {
                preparedStatement.setInt(4, intakeId);
                preparedStatement.setInt(5, intakeId);
            }

            if (yearId == null) {
                preparedStatement.setNull(6, java.sql.Types.INTEGER);
                preparedStatement.setNull(7, java.sql.Types.INTEGER);
            } else {
                preparedStatement.setInt(6, yearId);
                preparedStatement.setInt(7, yearId);
            }

            if (semesterId == null) {
                preparedStatement.setNull(8, java.sql.Types.INTEGER);
                preparedStatement.setNull(9, java.sql.Types.INTEGER);
            } else {
                preparedStatement.setInt(8, semesterId);
                preparedStatement.setInt(9, semesterId);
            }

            if (safeCourseSearch == null) {
                preparedStatement.setNull(10, java.sql.Types.VARCHAR);
                preparedStatement.setNull(11, java.sql.Types.VARCHAR);
                preparedStatement.setNull(12, java.sql.Types.VARCHAR);
            } else {
                preparedStatement.setString(10, safeCourseSearch);
                preparedStatement.setString(11, safeCourseSearch);
                preparedStatement.setString(12, "%" + safeCourseSearch.toLowerCase() + "%");
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ProgrammeStructureGroup currentGroup = null;
                String currentGroupKey = null;

                while (resultSet.next()) {
                    String nextGroupKey = resultSet.getString("program_code") + "|"
                            + resultSet.getInt("intake_id") + "|"
                            + resultSet.getInt("year_id") + "|"
                            + resultSet.getInt("semester_id");

                    if (!nextGroupKey.equals(currentGroupKey)) {
                        currentGroup = new ProgrammeStructureGroup();
                        currentGroup.setProgramCode(resultSet.getString("program_code"));
                        currentGroup.setProgramName(resultSet.getString("program_name"));
                        currentGroup.setIntakeId(resultSet.getInt("intake_id"));
                        currentGroup.setIntakeName(resultSet.getString("intake_name"));
                        currentGroup.setYearId(resultSet.getInt("year_id"));
                        currentGroup.setYearName(resultSet.getString("year_name"));
                        currentGroup.setSemesterId(resultSet.getInt("semester_id"));
                        currentGroup.setSemesterName(resultSet.getString("semester_name"));
                        structureGroupList.add(currentGroup);
                        currentGroupKey = nextGroupKey;
                    }

                    CourseRecord course = new CourseRecord();
                    course.setCourseId(resultSet.getInt("course_id"));
                    course.setCourseCode(resultSet.getString("course_code"));
                    course.setCourseName(resultSet.getString("course_name"));
                    course.setCreditHour(resultSet.getInt("credit_hour"));
                    course.setAssessmentComponents(resultSet.getString("assessment_components"));
                    currentGroup.getCourses().add(course);
                }
            }

        } catch (Exception exception) {
            System.out.println("ERROR in findProgrammeStructures()");
            exception.printStackTrace();
        }

        return structureGroupList;
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
    public StudentCurriculumFocus findStudentCurriculumFocus(String studentId) {
        StudentCurriculumFocus focus = null;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_STUDENT_CURRICULUM_FOCUS_SQL)) {

            preparedStatement.setString(1, studentId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    focus = new StudentCurriculumFocus();
                    focus.setStudentId(resultSet.getString("student_id"));
                    focus.setStudentName(resultSet.getString("student_name"));
                    focus.setProgramCode(resultSet.getString("program_code"));
                    focus.setProgramName(resultSet.getString("program_name"));
                    focus.setIntakeId(resultSet.getInt("intake_id"));
                    focus.setIntakeName(resultSet.getString("intake_name"));
                    focus.setYearId(resultSet.getInt("year_id"));
                    focus.setYearName(resultSet.getString("year_name"));
                    focus.setSemesterId(resultSet.getInt("semester_id"));
                    focus.setSemesterName(resultSet.getString("semester_name"));
                }
            }

        } catch (Exception exception) {
            System.out.println("ERROR in findStudentCurriculumFocus()");
            exception.printStackTrace();
        }

        return focus;
    }

    private List<ReferenceOption> findReferenceOptions(String sql, String idColumn, String labelColumn) {
        List<ReferenceOption> optionList = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                optionList.add(new ReferenceOption(resultSet.getInt(idColumn), resultSet.getString(labelColumn)));
            }

        } catch (Exception exception) {
            System.out.println("ERROR in findReferenceOptions()");
            exception.printStackTrace();
        }

        return optionList;
    }

    private String normalizeSearch(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
