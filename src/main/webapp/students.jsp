<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.ProgrammeRecord" %>
<%@ page import="com.crs.model.ReferenceOption" %>
<%@ page import="com.crs.model.StudentDirectoryRecord" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
</head>
<body class="app-body">
    <%@ include file="/WEB-INF/jspf/auth-check.jspf" %>

    <%
        List<StudentDirectoryRecord> studentRowsData = (List<StudentDirectoryRecord>) request.getAttribute("studentList");
        List<ProgrammeRecord> programmeOptionRowsData = (List<ProgrammeRecord>) request.getAttribute("programmeOptions");
        List<ReferenceOption> intakeOptionRowsData = (List<ReferenceOption>) request.getAttribute("intakeOptions");
        List<ReferenceOption> yearOptionRowsData = (List<ReferenceOption>) request.getAttribute("yearOptions");
        List<ReferenceOption> semesterOptionRowsData = (List<ReferenceOption>) request.getAttribute("semesterOptions");

        String programmeCodeValue = request.getAttribute("programmeCode") == null ? "" : String.valueOf(request.getAttribute("programmeCode"));
        Integer intakeIdValue = (Integer) request.getAttribute("intakeId");
        Integer yearIdValue = (Integer) request.getAttribute("yearId");
        Integer semesterIdValue = (Integer) request.getAttribute("semesterId");
        String studentSearchValue = request.getAttribute("studentSearch") == null ? "" : String.valueOf(request.getAttribute("studentSearch"));
    %>

    <div class="app-shell">
        <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>

        <main class="app-main">
            <%@ include file="/WEB-INF/jspf/topbar.jspf" %>

            <div class="app-content">
                <div class="page-stack">
                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h3 class="card-title">Student Directory Filters</h3>
                                <p class="card-description">Read-only student listing with a curriculum shortcut. All students are shown by default and the filters narrow the list.</p>
                            </div>
                        </div>

                        <form action="students" method="get" class="filter-grid-5">
                            <div class="field-stack">
                                <label class="field-label-upper" for="programmeCode">Programme</label>
                                <select class="select-input" id="programmeCode" name="programmeCode">
                                    <option value="">All Programmes</option>
                                    <% if (programmeOptionRowsData != null) { %>
                                        <% for (ProgrammeRecord programmeOptionData : programmeOptionRowsData) { %>
                                            <option value="<%= programmeOptionData.getProgramCode() %>" <%= programmeCodeValue.equals(programmeOptionData.getProgramCode()) ? "selected" : "" %>>
                                                <%= programmeOptionData.getProgramCode() %> - <%= programmeOptionData.getProgramName() %>
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="intakeId">Intake</label>
                                <select class="select-input" id="intakeId" name="intakeId">
                                    <option value="">All Intakes</option>
                                    <% if (intakeOptionRowsData != null) { %>
                                        <% for (ReferenceOption intakeOptionData : intakeOptionRowsData) { %>
                                            <option value="<%= intakeOptionData.getId() %>" <%= intakeIdValue != null && intakeIdValue == intakeOptionData.getId() ? "selected" : "" %>>
                                                <%= intakeOptionData.getLabel() %>
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="yearId">Year</label>
                                <select class="select-input" id="yearId" name="yearId">
                                    <option value="">All Years</option>
                                    <% if (yearOptionRowsData != null) { %>
                                        <% for (ReferenceOption yearOptionData : yearOptionRowsData) { %>
                                            <option value="<%= yearOptionData.getId() %>" <%= yearIdValue != null && yearIdValue == yearOptionData.getId() ? "selected" : "" %>>
                                                <%= yearOptionData.getLabel() %>
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="semesterId">Semester</label>
                                <select class="select-input" id="semesterId" name="semesterId">
                                    <option value="">All Semesters</option>
                                    <% if (semesterOptionRowsData != null) { %>
                                        <% for (ReferenceOption semesterOptionData : semesterOptionRowsData) { %>
                                            <option value="<%= semesterOptionData.getId() %>" <%= semesterIdValue != null && semesterIdValue == semesterOptionData.getId() ? "selected" : "" %>>
                                                <%= semesterOptionData.getLabel() %>
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="studentSearch">Student ID / Name</label>
                                <div class="inline-field-row">
                                    <input class="text-input" id="studentSearch" name="studentSearch" type="text" placeholder="Search Student ID or Name" value="<%= studentSearchValue %>" />
                                    <div class="action-button-row student-search-actions">
                                        <button type="submit" class="btn btn-outline btn-compact">Apply</button>
                                        <button type="button" class="btn btn-outline btn-compact"
                                                onclick="this.form.programmeCode.value=''; this.form.intakeId.value=''; this.form.yearId.value=''; this.form.semesterId.value=''; this.form.studentSearch.value=''; this.form.submit();">
                                            Reset
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </section>

                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h3 class="card-title">All Students</h3>
                                <p class="card-description">The student row will automatically reflect the new Year and Semester after enrolment approval progression.</p>
                            </div>
                        </div>

                        <div class="table-wrap">
                            <table class="data-table student-table">
                                <thead>
                                    <tr>
                                        <th>Student ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Programme</th>
                                        <th>Intake</th>
                                        <th>Year</th>
                                        <th>Semester</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (studentRowsData != null && !studentRowsData.isEmpty()) { %>
                                        <% for (StudentDirectoryRecord studentData : studentRowsData) { %>
                                            <tr>
                                                <td class="strong-cell"><%= studentData.getStudentId() %></td>
                                                <td><%= studentData.getStudentName() %></td>
                                                <td><%= studentData.getEmail() %></td>
                                                <td><%= studentData.getProgramCode() %> - <%= studentData.getProgramName() %></td>
                                                <td><%= studentData.getIntakeName() %></td>
                                                <td><%= studentData.getYearName() %></td>
                                                <td><%= studentData.getSemesterName() %></td>
                                                <td class="student-action-cell">
                                                    <a class="btn btn-outline btn-compact" href="<%= request.getContextPath() %>/programs-courses?studentId=<%= studentData.getStudentId() %>#programme-structure">View Curriculum</a>
                                                </td>
                                            </tr>
                                        <% } %>
                                    <% } else { %>
                                        <tr>
                                            <td colspan="8" class="empty-row">No students match the current filters.</td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
