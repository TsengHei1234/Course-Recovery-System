# Course Recovery System

Course Recovery System is a Jakarta EE web application for academic officers to review progression eligibility, manage recovery plans and milestones, view academic information, and send template-based notifications.

## Project background

This was developed as a university group project. The preserved Git history is the source of truth for authorship. Suy Tseng Hei's verified work includes the Students, Programmes/Courses, Academic Reports, and Email Templates areas, together with related DAO, EJB, model, JSP, styling, and Gmail-integration work.

## Technology

- Java 17
- Jakarta EE Web Profile 10
- Apache TomEE 10 / Tomcat 10.1
- JSP and JSTL
- MySQL 8
- Maven WAR packaging

The code follows a Servlet → EJB → DAO/JDBC → MySQL structure.

## Local setup

1. Install JDK 17, Maven, MySQL 8, and Apache TomEE 10 WebProfile.
2. Run `database/schema.sql` in an isolated local MySQL database.
3. Set `CRS_DB_URL`, `CRS_DB_USER`, and `CRS_DB_PASSWORD` using `.env.example` as a name reference. The application reads operating-system environment variables or Java system properties; it does not load `.env` automatically.
4. Run `mvn clean package`.
5. Deploy `target/course-recovery-system.war` to TomEE.

Gmail delivery is optional. It also requires `CRS_GMAIL_CLIENT_ID`, `CRS_GMAIL_CLIENT_SECRET`, and `CRS_GMAIL_REFRESH_TOKEN`. Never commit real values.

## Main features

- Student and academic-record lookup
- Progression eligibility assessment
- Course recovery workspace and plan milestones
- Programme and course reference views
- Academic reporting
- User and email-template administration
- Optional Gmail notifications

## Security notes

- Secrets are external configuration, not source code.
- New and reset passwords use PBKDF2-HMAC-SHA256. Existing plaintext development passwords are upgraded after a successful login.
- OTP generation uses `SecureRandom`.
- The former OAuth token-display and Gmail test endpoints were removed.
- This remains an educational application. It has not received a production security review, and CSRF protection should be added before any real deployment.

## Testing status

The original project did not include automated tests. Portfolio preparation adds JUnit checks for password hashing and OTP generation; run them with `mvn test`. Database and browser workflows still require an isolated local test database.

## Ownership

This repository presents a group university project and does not claim sole authorship. No open-source licence is granted because shared ownership has not been formally resolved.
