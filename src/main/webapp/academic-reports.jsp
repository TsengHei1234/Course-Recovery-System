<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="com.crs.model.SelectionOption" %>
<%@ page import="com.crs.model.AcademicReportStudent" %>
<%@ page import="com.crs.model.AcademicReportData" %>
<%@ page import="com.crs.model.AcademicReportCourseRow" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
</head>
<body class="app-body">
    <%@ include file="/WEB-INF/jspf/auth-check.jspf" %>

    <%
        List<SelectionOption> programmeOptionRowsData = (List<SelectionOption>) request.getAttribute("programmeOptions");
        List<SelectionOption> intakeOptionRowsData = (List<SelectionOption>) request.getAttribute("intakeOptions");
        List<SelectionOption> yearOptionRowsData = (List<SelectionOption>) request.getAttribute("yearOptions");
        List<SelectionOption> semesterOptionRowsData = (List<SelectionOption>) request.getAttribute("semesterOptions");
        List<AcademicReportStudent> candidateStudentRowsData = (List<AcademicReportStudent>) request.getAttribute("candidateStudents");

        AcademicReportStudent selectedStudentData = (AcademicReportStudent) request.getAttribute("selectedStudent");
        AcademicReportData generatedReportData = (AcademicReportData) request.getAttribute("generatedReport");

        String programmeCodeValue = request.getAttribute("programmeCode") == null ? "" : String.valueOf(request.getAttribute("programmeCode"));
        Integer intakeIdValue = (Integer) request.getAttribute("intakeId");
        Integer yearIdValue = (Integer) request.getAttribute("yearId");
        Integer semesterIdValue = (Integer) request.getAttribute("semesterId");
        String studentSearchValue = request.getAttribute("studentSearch") == null ? "" : String.valueOf(request.getAttribute("studentSearch"));
        String selectedStudentIdValue = request.getAttribute("selectedStudentId") == null ? "" : String.valueOf(request.getAttribute("selectedStudentId"));

        String toastTypeValue = request.getAttribute("toastType") == null ? "" : String.valueOf(request.getAttribute("toastType"));
        String toastMessageValue = request.getAttribute("toastMessage") == null ? "" : String.valueOf(request.getAttribute("toastMessage"));

        DecimalFormat academicDecimalFormat = new DecimalFormat("0.00");
    %>

    <div class="app-shell">
        <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>

        <main class="app-main">
            <%@ include file="/WEB-INF/jspf/topbar.jspf" %>

            <div class="app-content">
                <% if (!toastMessageValue.isEmpty()) { %>
                    <div class="toast-banner toast-<%= toastTypeValue %>"><%= toastMessageValue %></div>
                <% } %>

                <div class="page-stack">
                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h3 class="card-title">Academic Report Filters</h3>
                                <p class="card-description">Use Programme, Intake, Year, Semester, and Student ID / Name to narrow the report candidates, then select a student and generate the report.</p>
                            </div>
                        </div>

                        <form action="academic-reports" method="get" class="filter-grid-5">
                            <div class="field-stack">
                                <label class="field-label-upper" for="programmeCode">Programme</label>
                                <select class="select-input" id="programmeCode" name="programmeCode">
                                    <option value="">All Programmes</option>
                                    <% if (programmeOptionRowsData != null) { %>
                                        <% for (SelectionOption programmeOptionData : programmeOptionRowsData) { %>
                                            <option value="<%= programmeOptionData.getValue() %>" <%= programmeCodeValue.equals(programmeOptionData.getValue()) ? "selected" : "" %>><%= programmeOptionData.getLabel() %></option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="intakeId">Intake</label>
                                <select class="select-input" id="intakeId" name="intakeId">
                                    <option value="">All Intakes</option>
                                    <% if (intakeOptionRowsData != null) { %>
                                        <% for (SelectionOption intakeOptionData : intakeOptionRowsData) { %>
                                            <option value="<%= intakeOptionData.getValue() %>" <%= intakeIdValue != null && String.valueOf(intakeIdValue).equals(intakeOptionData.getValue()) ? "selected" : "" %>><%= intakeOptionData.getLabel() %></option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="yearId">Year</label>
                                <select class="select-input" id="yearId" name="yearId">
                                    <option value="">All Years</option>
                                    <% if (yearOptionRowsData != null) { %>
                                        <% for (SelectionOption yearOptionData : yearOptionRowsData) { %>
                                            <option value="<%= yearOptionData.getValue() %>" <%= yearIdValue != null && String.valueOf(yearIdValue).equals(yearOptionData.getValue()) ? "selected" : "" %>><%= yearOptionData.getLabel() %></option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="semesterId">Semester</label>
                                <select class="select-input" id="semesterId" name="semesterId">
                                    <option value="">All Semesters</option>
                                    <% if (semesterOptionRowsData != null) { %>
                                        <% for (SelectionOption semesterOptionData : semesterOptionRowsData) { %>
                                            <option value="<%= semesterOptionData.getValue() %>" <%= semesterIdValue != null && String.valueOf(semesterIdValue).equals(semesterOptionData.getValue()) ? "selected" : "" %>><%= semesterOptionData.getLabel() %></option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="studentSearch">Student ID / Name</label>
                                <div class="inline-field-row">
                                    <input class="text-input" id="studentSearch" name="studentSearch" type="text" placeholder="Search Student ID or Name" value="<%= studentSearchValue %>" />
                                    <div class="action-button-row report-filter-actions">
                                        <button type="submit" class="btn btn-outline btn-compact">Apply</button>
                                        <button type="button" class="btn btn-outline btn-compact"
                                                onclick="this.form.programmeCode.value=''; this.form.intakeId.value=''; this.form.yearId.value=''; this.form.semesterId.value=''; this.form.studentSearch.value=''; this.form.selectedStudentId.value=''; this.form.submit();">
                                            Reset
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <input type="hidden" name="selectedStudentId" value="" />
                        </form>
                    </section>

                    <div class="split-layout reports-layout">
                        <section class="card content-card-tight">
                            <div class="card-title-row">
                                <div class="card-title-group">
                                    <h3 class="card-title">Candidate Students</h3>
                                    <p class="card-description">Search directly by ID / Name or narrow the list first using the academic filters above.</p>
                                </div>
                            </div>

                            <div class="report-candidate-list">
                                <% if (candidateStudentRowsData != null && !candidateStudentRowsData.isEmpty()) { %>
                                    <% for (AcademicReportStudent candidateStudentData : candidateStudentRowsData) { %>
                                        <form action="academic-reports" method="get" class="report-candidate-card <%= candidateStudentData.getStudentId().equals(selectedStudentIdValue) ? "selected" : "" %>">
                                            <input type="hidden" name="programmeCode" value="<%= programmeCodeValue %>" />
                                            <input type="hidden" name="intakeId" value="<%= intakeIdValue == null ? "" : intakeIdValue %>" />
                                            <input type="hidden" name="yearId" value="<%= yearIdValue == null ? "" : yearIdValue %>" />
                                            <input type="hidden" name="semesterId" value="<%= semesterIdValue == null ? "" : semesterIdValue %>" />
                                            <input type="hidden" name="studentSearch" value="<%= studentSearchValue %>" />
                                            <input type="hidden" name="selectedStudentId" value="<%= candidateStudentData.getStudentId() %>" />

                                            <div class="report-candidate-head">
                                                <div>
                                                    <div class="template-name"><%= candidateStudentData.getStudentName() %></div>
                                                    <div class="template-subtle"><%= candidateStudentData.getStudentId() %></div>
                                                    <div class="template-subtle top-gap-8"><%= candidateStudentData.getProgramCode() %> - <%= candidateStudentData.getProgramName() %></div>
                                                    <div class="template-subtle"><%= candidateStudentData.getIntakeName() %> &bull; <%= candidateStudentData.getYearName() %> &bull; <%= candidateStudentData.getSemesterName() %></div>
                                                </div>
                                                <button type="submit" class="btn btn-outline btn-compact"><%= candidateStudentData.getStudentId().equals(selectedStudentIdValue) ? "Selected" : "Select" %></button>
                                            </div>
                                        </form>
                                    <% } %>
                                <% } else { %>
                                    <div class="empty-box">No students match the current report filters.</div>
                                <% } %>
                            </div>
                        </section>

                        <div class="page-stack reports-stack">
                            <section class="card content-card-tight">
                                <div class="report-action-banner">
                                    <div>
                                        <div class="meta-text">Selected Student</div>
                                        <div class="report-selected-title">
                                            <%= selectedStudentData != null ? selectedStudentData.getStudentName() + " (" + selectedStudentData.getStudentId() + ")" : "No student selected yet" %>
                                        </div>
                                        <div class="template-subtle top-gap-8">
                                            <%= selectedStudentData != null
                                                    ? selectedStudentData.getProgramCode() + " - " + selectedStudentData.getProgramName() + " • " + selectedStudentData.getIntakeName() + " • " + (yearIdValue != null ? generatedReportData != null ? generatedReportData.getReportYear() : selectedStudentData.getYearName() : selectedStudentData.getYearName()) + " • " + (semesterIdValue != null ? generatedReportData != null ? generatedReportData.getReportSemester() : selectedStudentData.getSemesterName() : selectedStudentData.getSemesterName())
                                                    : "Pick a student from the left list first." %>
                                        </div>
                                    </div>

                                    <div class="report-action-buttons">
                                        <form action="academic-reports" method="post">
                                            <input type="hidden" name="reportAction" value="generate" />
                                            <input type="hidden" name="programmeCode" value="<%= programmeCodeValue %>" />
                                            <input type="hidden" name="intakeId" value="<%= intakeIdValue == null ? "" : intakeIdValue %>" />
                                            <input type="hidden" name="yearId" value="<%= yearIdValue == null ? "" : yearIdValue %>" />
                                            <input type="hidden" name="semesterId" value="<%= semesterIdValue == null ? "" : semesterIdValue %>" />
                                            <input type="hidden" name="studentSearch" value="<%= studentSearchValue %>" />
                                            <input type="hidden" name="selectedStudentId" value="<%= selectedStudentIdValue %>" />
                                            <button type="submit" class="btn btn-primary" <%= selectedStudentData == null ? "disabled" : "" %>>Generate Report</button>
                                        </form>

                                        <form action="academic-reports" method="post">
                                            <input type="hidden" name="reportAction" value="email" />
                                            <input type="hidden" name="reportReady" value="<%= generatedReportData != null ? "true" : "false" %>" />
                                            <input type="hidden" name="programmeCode" value="<%= programmeCodeValue %>" />
                                            <input type="hidden" name="intakeId" value="<%= intakeIdValue == null ? "" : intakeIdValue %>" />
                                            <input type="hidden" name="yearId" value="<%= yearIdValue == null ? "" : yearIdValue %>" />
                                            <input type="hidden" name="semesterId" value="<%= semesterIdValue == null ? "" : semesterIdValue %>" />
                                            <input type="hidden" name="studentSearch" value="<%= studentSearchValue %>" />
                                            <input type="hidden" name="selectedStudentId" value="<%= selectedStudentIdValue %>" />
                                            <button type="submit" class="btn btn-outline" <%= generatedReportData == null ? "disabled" : "" %>>Email Report</button>
                                        </form>
                                    </div>
                                </div>
                            </section>

                            <section class="card content-card">
                                <div class="card-title-row">
                                    <div class="card-title-group">
                                        <h3 class="card-title">Academic Performance Report</h3>
                                        <p class="card-description">Generate Report only prepares the report preview. Email Report is the separate action that sends the student email.</p>
                                    </div>
                                </div>

                                <% if (generatedReportData != null) { %>
                                    <div class="info-grid-2">
                                        <div class="info-box"><div class="info-box-label">Student ID</div><div class="info-box-value"><%= generatedReportData.getStudent().getStudentId() %></div></div>
                                        <div class="info-box"><div class="info-box-label">Student Name</div><div class="info-box-value"><%= generatedReportData.getStudent().getStudentName() %></div></div>
                                        <div class="info-box"><div class="info-box-label">Programme</div><div class="info-box-value"><%= generatedReportData.getStudent().getProgramCode() %> - <%= generatedReportData.getStudent().getProgramName() %></div></div>
                                        <div class="info-box"><div class="info-box-label">Intake</div><div class="info-box-value"><%= generatedReportData.getStudent().getIntakeName() %></div></div>
                                        <div class="info-box"><div class="info-box-label">Academic Year</div><div class="info-box-value"><%= generatedReportData.getReportYear() %></div></div>
                                        <div class="info-box"><div class="info-box-label">Semester</div><div class="info-box-value"><%= generatedReportData.getReportSemester() %></div></div>
                                    </div>

                                    <div class="table-wrap top-gap-16">
                                        <table class="data-table report-data-table">
                                            <thead>
                                                <tr>
                                                    <th>Code</th>
                                                    <th>Course</th>
                                                    <th>Credit Hour</th>
                                                    <th>Grade</th>
                                                    <th>Grade Point</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% if (generatedReportData.getCourseRows() != null && !generatedReportData.getCourseRows().isEmpty()) { %>
                                                    <% for (AcademicReportCourseRow courseRowData : generatedReportData.getCourseRows()) { %>
                                                        <tr>
                                                            <td class="strong-cell"><%= courseRowData.getCourseCode() %></td>
                                                            <td><%= courseRowData.getCourseName() %></td>
                                                            <td><%= courseRowData.getCreditHour() %></td>
                                                            <td><%= courseRowData.getGrade() %></td>
                                                            <td><%= academicDecimalFormat.format(courseRowData.getGradePoint()) %></td>
                                                        </tr>
                                                    <% } %>
                                                <% } else { %>
                                                    <tr>
                                                        <td colspan="5" class="empty-row">No course performance rows were found for the selected term.</td>
                                                    </tr>
                                                <% } %>
                                            </tbody>
                                        </table>
                                    </div>

                                    <div class="report-metric-grid top-gap-16">
                                        <div class="metric-lite-card">
                                            <div class="meta-text">Semester GPA</div>
                                            <div class="metric-lite-value"><%= academicDecimalFormat.format(generatedReportData.getSemesterGpa()) %></div>
                                        </div>
                                        <div class="metric-lite-card">
                                            <div class="meta-text">Cumulative CGPA</div>
                                            <div class="metric-lite-value"><%= academicDecimalFormat.format(generatedReportData.getCgpa()) %></div>
                                        </div>
                                    </div>
                                <% } else { %>
                                    <div class="empty-box">Generate a report after selecting a student. Email Report stays separate and will not run until the report is generated first.</div>
                                <% } %>
                            </section>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <% if (!toastMessageValue.isEmpty()) { %>
        <script>
            window.setTimeout(function () {
                var toastBanner = document.querySelector('.toast-banner');
                if (toastBanner) {
                    toastBanner.classList.add('toast-hide');
                }
            }, 3200);
        </script>
    <% } %>
</body>
</html>
