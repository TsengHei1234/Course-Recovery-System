CREATE DATABASE IF NOT EXISTS epda_assignment;
USE epda_assignment;

-- =========================
-- 1. ROLES
-- =========================
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- 2. USERS
-- =========================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES roles(role_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =========================
-- 3. OTPS
-- =========================
CREATE TABLE otps (
    otp_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    otp VARCHAR(10) NOT NULL,
    expired_time DATETIME NOT NULL,
    CONSTRAINT fk_otps_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- =========================
-- 4. MAJORS
-- =========================
CREATE TABLE majors (
    major_id INT AUTO_INCREMENT PRIMARY KEY,
    major_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- 5. LEVELS
-- =========================
CREATE TABLE levels (
    level_id INT AUTO_INCREMENT PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- 6. INTAKES
-- =========================
CREATE TABLE intakes (
    intake_id INT AUTO_INCREMENT PRIMARY KEY,
    intake_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- 7. YEARS
-- =========================
CREATE TABLE years (
    year_id INT AUTO_INCREMENT PRIMARY KEY,
    year_name VARCHAR(20) NOT NULL UNIQUE,
    year_order INT NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- 8. SEMESTERS
-- =========================
CREATE TABLE semesters (
    semester_id INT AUTO_INCREMENT PRIMARY KEY,
    semester_name VARCHAR(20) NOT NULL UNIQUE,
    semester_order INT NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- 9. PROGRAMS
-- =========================
CREATE TABLE programs (
    program_id INT AUTO_INCREMENT PRIMARY KEY,
    major_id INT NOT NULL,
    level_id INT NOT NULL,
    intake_id INT NOT NULL,
    program_code VARCHAR(20) NOT NULL,
    program_name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_programs_major
        FOREIGN KEY (major_id) REFERENCES majors(major_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_programs_level
        FOREIGN KEY (level_id) REFERENCES levels(level_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_programs_intake
        FOREIGN KEY (intake_id) REFERENCES intakes(intake_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT uq_program_code_intake
        UNIQUE (program_code, intake_id)
) ENGINE=InnoDB;

-- =========================
-- 10. STUDENTS
-- =========================
CREATE TABLE students (
    student_id VARCHAR(20) PRIMARY KEY,
    program_id INT NOT NULL,
    year_id INT NOT NULL,
    semester_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    CONSTRAINT fk_students_program
        FOREIGN KEY (program_id) REFERENCES programs(program_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_students_year
        FOREIGN KEY (year_id) REFERENCES years(year_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_students_semester
        FOREIGN KEY (semester_id) REFERENCES semesters(semester_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =========================
-- 11. COURSES
-- =========================
CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    credit_hour INT NOT NULL
) ENGINE=InnoDB;

-- =========================
-- 12. COMPONENTS
-- =========================
CREATE TABLE components (
    component_id INT AUTO_INCREMENT PRIMARY KEY,
    component_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- 13. COURSE_COMPONENTS
-- =========================
CREATE TABLE course_components (
    course_component_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    component_id INT NOT NULL,
    course_component_name VARCHAR(100) NOT NULL,
    weight_percent DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_course_components_course
        FOREIGN KEY (course_id) REFERENCES courses(course_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_course_components_component
        FOREIGN KEY (component_id) REFERENCES components(component_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT uq_course_component
        UNIQUE (course_id, component_id, course_component_name)
) ENGINE=InnoDB;

-- =========================
-- 14. PROGRAM_COURSES
-- =========================
CREATE TABLE program_courses (
    program_course_id INT AUTO_INCREMENT PRIMARY KEY,
    program_id INT NOT NULL,
    year_id INT NOT NULL,
    semester_id INT NOT NULL,
    course_id INT NOT NULL,
    academic_officer_user_id INT NOT NULL,
    CONSTRAINT fk_program_courses_program
        FOREIGN KEY (program_id) REFERENCES programs(program_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_program_courses_year
        FOREIGN KEY (year_id) REFERENCES years(year_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_program_courses_semester
        FOREIGN KEY (semester_id) REFERENCES semesters(semester_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_program_courses_course
        FOREIGN KEY (course_id) REFERENCES courses(course_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_program_courses_user
        FOREIGN KEY (academic_officer_user_id) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT uq_program_course
        UNIQUE (program_id, year_id, semester_id, course_id)
) ENGINE=InnoDB;

-- =========================
-- 15. STUDENT_COURSES
-- =========================
CREATE TABLE student_courses (
    student_course_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    program_course_id INT NOT NULL,
    attempt_no INT NOT NULL DEFAULT 1,
    grade VARCHAR(5),
    grade_point DECIMAL(3,2),
    CONSTRAINT fk_student_courses_student
        FOREIGN KEY (student_id) REFERENCES students(student_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_student_courses_program_course
        FOREIGN KEY (program_course_id) REFERENCES program_courses(program_course_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT uq_student_course_attempt
        UNIQUE (student_id, program_course_id, attempt_no)
) ENGINE=InnoDB;

-- =========================
-- 16. STUDENT_COURSE_COMPONENTS
-- =========================
CREATE TABLE student_course_components (
    student_course_component_id INT AUTO_INCREMENT PRIMARY KEY,
    student_course_id INT NOT NULL,
    course_component_id INT NOT NULL,
    mark DECIMAL(5,2),
    grade VARCHAR(5),
    grade_point DECIMAL(3,2),
    CONSTRAINT fk_scc_student_course
        FOREIGN KEY (student_course_id) REFERENCES student_courses(student_course_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_scc_course_component
        FOREIGN KEY (course_component_id) REFERENCES course_components(course_component_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT uq_student_course_component
        UNIQUE (student_course_id, course_component_id)
) ENGINE=InnoDB;

-- =========================
-- 17. PROGRESSION_ENROLMENTS
-- =========================
CREATE TABLE progression_enrolments (
    progression_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    current_year_id INT NOT NULL,
    current_semester_id INT NOT NULL,
    next_year_id INT NULL,
    next_semester_id INT NULL,
    cgpa DECIMAL(4,2) NULL,
    failed_course_count INT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AWAITING_CHECK',
    reason VARCHAR(255) NULL,
    checked_by INT NULL,
    checked_at DATETIME NULL,
    approved_by INT NULL,
    approved_at DATETIME NULL,
    sent_to_recovery_by INT NULL,
    sent_to_recovery_at DATETIME NULL,
    remarks VARCHAR(255) NULL,
    CONSTRAINT fk_progression_student
        FOREIGN KEY (student_id) REFERENCES students(student_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_progression_current_year
        FOREIGN KEY (current_year_id) REFERENCES years(year_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_progression_current_semester
        FOREIGN KEY (current_semester_id) REFERENCES semesters(semester_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_progression_next_year
        FOREIGN KEY (next_year_id) REFERENCES years(year_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_progression_next_semester
        FOREIGN KEY (next_semester_id) REFERENCES semesters(semester_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_progression_checked_by
        FOREIGN KEY (checked_by) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_progression_approved_by
        FOREIGN KEY (approved_by) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_progression_sent_by
        FOREIGN KEY (sent_to_recovery_by) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT uq_progression_term
        UNIQUE (student_id, current_year_id, current_semester_id)
) ENGINE=InnoDB;

-- =========================
-- 18. STUDENT_PLANS
-- =========================
CREATE TABLE student_plans (
    student_plan_id INT AUTO_INCREMENT PRIMARY KEY,
    student_course_id INT NOT NULL,
    student_course_component_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    created_by INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by INT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    grade VARCHAR(5),
    grade_point DECIMAL(3,2),
    remarks VARCHAR(255) NULL,
    CONSTRAINT fk_student_plans_student_course
        FOREIGN KEY (student_course_id) REFERENCES student_courses(student_course_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_student_plans_student_course_component
        FOREIGN KEY (student_course_component_id) REFERENCES student_course_components(student_course_component_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_student_plans_created_by
        FOREIGN KEY (created_by) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_student_plans_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    CONSTRAINT uq_student_plan_component
        UNIQUE (student_course_component_id)
) ENGINE=InnoDB;

-- =========================
-- 19. STUDENT_PLAN_MILESTONES
-- =========================
CREATE TABLE student_plan_milestones (
    student_plan_milestone_id INT AUTO_INCREMENT PRIMARY KEY,
    student_plan_id INT NOT NULL,
    milestone_no INT NOT NULL,
    milestone_description TEXT NOT NULL,
    duration_days INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    remarks VARCHAR(255) NULL,
    completed_at DATETIME NULL,
    CONSTRAINT fk_student_plan_milestones_plan
        FOREIGN KEY (student_plan_id) REFERENCES student_plans(student_plan_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT uq_student_plan_milestone_no
        UNIQUE (student_plan_id, milestone_no)
) ENGINE=InnoDB;

-- =========================
-- 20. EMAIL_TEMPLATES
-- =========================
CREATE TABLE email_templates (
    email_template_id INT AUTO_INCREMENT PRIMARY KEY,
    template_code VARCHAR(100) NOT NULL UNIQUE,
    template_name VARCHAR(150) NOT NULL,
    module VARCHAR(50) NOT NULL,
    trigger_event VARCHAR(100) NOT NULL,
    subject_template VARCHAR(255) NOT NULL,
    body_template TEXT NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    updated_by INT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_email_templates_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB;


