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

The code follows a Servlet -> EJB -> DAO/JDBC -> MySQL structure.

## Before you run the project

1. Install JDK 17, Maven, MySQL 8, and Apache TomEE 10 WebProfile.
2. Run `database/schema.sql` in an isolated local MySQL database.
3. Set `CRS_DB_URL`, `CRS_DB_USER`, and `CRS_DB_PASSWORD` using `.env.example` as a name reference. The application reads operating-system environment variables or Java system properties; it does not load `.env` automatically.

Gmail delivery is optional. It also requires `CRS_GMAIL_CLIENT_ID`, `CRS_GMAIL_CLIENT_SECRET`, and `CRS_GMAIL_REFRESH_TOKEN`. Never commit real values.

## Build and run

### Eclipse IDE (recommended)

1. Open Eclipse IDE for Enterprise Java and Web Developers with JDK 17 configured.
2. Select **File > Import > Maven > Existing Maven Projects**.
3. Select this repository and finish the Maven import.
4. Add Apache TomEE 10 as a Tomcat 10.1 server in the **Servers** view.
5. Add the project to the server and select **Run on Server**.

TomEE supports deploying web applications through Eclipse using the Tomcat server adapter.

### Visual Studio Code

1. Install the **Extension Pack for Java**, which includes Maven support.
2. Open this repository as the workspace folder and allow the Java extensions to import `pom.xml`.
3. Open the **Maven** view and run **Lifecycle > test**, followed by **Lifecycle > package**.
4. Copy `target/course-recovery-system.war` into the `webapps` directory of your TomEE installation.
5. Start TomEE and open `http://localhost:8080/course-recovery-system/`.

VS Code can build the Maven project, while TomEE runs the packaged web application.

### IntelliJ IDEA Ultimate

1. Open this repository and allow IntelliJ IDEA to import the Maven project.
2. Select **Run > Edit Configurations**.
3. Add a local **TomEE Server** configuration and select your TomEE 10 installation.
4. Add the `course-recovery-system:war` artifact for deployment.
5. Run the TomEE configuration.

### Windows PowerShell

Open PowerShell in the repository directory and run:

```powershell
mvn clean test package
Copy-Item .\target\course-recovery-system.war "$env:CATALINA_HOME\webapps\course-recovery-system.war"
& "$env:CATALINA_HOME\bin\startup.bat"
```

`CATALINA_HOME` must point to the TomEE installation directory.

### macOS or Linux terminal

Open a terminal in the repository directory and run:

```bash
mvn clean test package
cp target/course-recovery-system.war "$CATALINA_HOME/webapps/course-recovery-system.war"
"$CATALINA_HOME/bin/startup.sh"
```

After TomEE starts, open `http://localhost:8080/course-recovery-system/`.

Official references: [Java in Visual Studio Code](https://code.visualstudio.com/docs/languages/java), [Java build tools in VS Code](https://code.visualstudio.com/docs/java/java-build), and [deploying applications in TomEE](https://tomee.apache.org/latest/docs/application-deployment-solutions.html).

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
