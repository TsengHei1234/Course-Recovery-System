<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.ReferenceOption" %>
<%@ page import="com.crs.model.ProgrammeRecord" %>
<%@ page import="com.crs.model.CourseRecord" %>
<%@ page import="com.crs.model.ProgrammeStructureGroup" %>
<%@ page import="com.crs.model.StudentCurriculumFocus" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setAttribute("pageTitle", "Programs & Courses");
    request.setAttribute("breadcrumb1", "Reference");
    request.setAttribute("breadcrumb2", "Programs & Courses");
    request.setAttribute("currentPage", "courses");
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
</head>
<body class="app-body">
    <%@ include file="/WEB-INF/jspf/auth-check.jspf" %>

    <%
        List<ProgrammeRecord> programmeRowsData = (List<ProgrammeRecord>) request.getAttribute("programmeList");
        List<CourseRecord> courseRowsData = (List<CourseRecord>) request.getAttribute("courseList");
        List<ProgrammeStructureGroup> structureRowsData = (List<ProgrammeStructureGroup>) request.getAttribute("structureList");
        List<ProgrammeRecord> programmeOptionRowsData = (List<ProgrammeRecord>) request.getAttribute("programmeOptions");
        List<ReferenceOption> intakeOptionRowsData = (List<ReferenceOption>) request.getAttribute("intakeOptions");
        List<ReferenceOption> yearOptionRowsData = (List<ReferenceOption>) request.getAttribute("yearOptions");
        List<ReferenceOption> semesterOptionRowsData = (List<ReferenceOption>) request.getAttribute("semesterOptions");
        StudentCurriculumFocus focusStudentData = (StudentCurriculumFocus) request.getAttribute("focusStudent");

        String programmeSearchValue = String.valueOf(request.getAttribute("programmeSearch"));
        Integer programmeIntakeIdValue = (Integer) request.getAttribute("programmeIntakeId");
        String courseSearchValue = String.valueOf(request.getAttribute("courseSearch"));
        String structureProgramCodeValue = String.valueOf(request.getAttribute("structureProgramCode"));
        Integer structureIntakeIdValue = (Integer) request.getAttribute("structureIntakeId");
        Integer structureYearIdValue = (Integer) request.getAttribute("structureYearId");
        Integer structureSemesterIdValue = (Integer) request.getAttribute("structureSemesterId");
        String structureCourseSearchValue = String.valueOf(request.getAttribute("structureCourseSearch"));
        String focusStudentIdValue = String.valueOf(request.getAttribute("focusStudentId"));
    %>

    <div class="app-shell">
        <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>

        <div class="app-main">
            <%@ include file="/WEB-INF/jspf/topbar.jspf" %>

            <div class="app-content">
                <div class="page-stack">
                    <% if (focusStudentData != null) { %>
                        <section class="note-card">
                            <div class="card-title-row curriculum-banner-row">
                                <div class="card-title-group">
                                    <div class="note-title">Curriculum focus loaded from Students page</div>
                                    <p class="card-description">
                                        Showing the selected student’s current programme structure for
                                        <strong><%= focusStudentData.getStudentName() %></strong>
                                        (<%= focusStudentData.getStudentId() %>).
                                    </p>
                                </div>

                                <form action="programs-courses" method="get">
                                    <input type="hidden" name="programmeIntakeId" value="<%= programmeIntakeIdValue == null ? "" : programmeIntakeIdValue %>" />
                                    <input type="hidden" name="programmeSearch" value="<%= programmeSearchValue %>" />
                                    <input type="hidden" name="courseSearch" value="<%= courseSearchValue %>" />
                                    <input type="hidden" name="structureProgramCode" value="<%= structureProgramCodeValue %>" />
                                    <input type="hidden" name="structureIntakeId" value="<%= structureIntakeIdValue == null ? "" : structureIntakeIdValue %>" />
                                    <input type="hidden" name="structureYearId" value="<%= structureYearIdValue == null ? "" : structureYearIdValue %>" />
                                    <input type="hidden" name="structureSemesterId" value="<%= structureSemesterIdValue == null ? "" : structureSemesterIdValue %>" />
                                    <input type="hidden" name="structureCourseSearch" value="<%= structureCourseSearchValue %>" />
                                    <button type="submit" class="btn btn-outline">Clear Focus</button>
                                </form>
                            </div>
                        </section>
                    <% } %>

                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h3 class="card-title">All Programmes</h3>
                                <p class="card-description">Fixed read-only programme master data. No add, edit, or delete action is needed here.</p>
                            </div>
                        </div>

                        <form action="programs-courses" method="get" class="filter-grid-2">
                            <input type="hidden" name="courseSearch" value="<%= courseSearchValue %>" />
                            <input type="hidden" name="structureProgramCode" value="<%= structureProgramCodeValue %>" />
                            <input type="hidden" name="structureIntakeId" value="<%= structureIntakeIdValue == null ? "" : structureIntakeIdValue %>" />
                            <input type="hidden" name="structureYearId" value="<%= structureYearIdValue == null ? "" : structureYearIdValue %>" />
                            <input type="hidden" name="structureSemesterId" value="<%= structureSemesterIdValue == null ? "" : structureSemesterIdValue %>" />
                            <input type="hidden" name="structureCourseSearch" value="<%= structureCourseSearchValue %>" />
                            <input type="hidden" name="studentId" value="<%= focusStudentIdValue %>" />

                            <div class="field-stack">
                                <label class="field-label-upper" for="programmeIntakeId">Intake</label>
                                <select class="select-input" id="programmeIntakeId" name="programmeIntakeId">
                                    <option value="">All Intakes</option>
                                    <% for (ReferenceOption intakeOptionData : intakeOptionRowsData) { %>
                                        <option value="<%= intakeOptionData.getId() %>" <%= programmeIntakeIdValue != null && programmeIntakeIdValue == intakeOptionData.getId() ? "selected" : "" %>>
                                            <%= intakeOptionData.getLabel() %>
                                        </option>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="programmeSearch">Programme Code / Name</label>
                                <div class="inline-field-row">
                                    <input class="text-input" id="programmeSearch" name="programmeSearch" type="text" placeholder="Search Programme Code or Name" value="<%= programmeSearchValue %>" />
                                    <div class="action-button-row section-inline-actions">
                                        <button type="submit" class="btn btn-outline btn-compact">Apply</button>
                                        <button type="button" class="btn btn-outline btn-compact"
                                                onclick="this.form.programmeIntakeId.value=''; this.form.programmeSearch.value=''; this.form.submit();">
                                            Reset
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>

                        <div class="table-wrap top-gap-16">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Programme Code</th>
                                        <th>Programme Name</th>
                                        <th>Major</th>
                                        <th>Level</th>
                                        <th>Intake</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (programmeRowsData != null && !programmeRowsData.isEmpty()) { %>
                                        <% for (ProgrammeRecord programmeData : programmeRowsData) { %>
                                            <tr>
                                                <td class="strong-cell"><%= programmeData.getProgramCode() %></td>
                                                <td><%= programmeData.getProgramName() %></td>
                                                <td><%= programmeData.getMajorName() %></td>
                                                <td><%= programmeData.getLevelName() %></td>
                                                <td><%= programmeData.getIntakeName() %></td>
                                            </tr>
                                        <% } %>
                                    <% } else { %>
                                        <tr>
                                            <td colspan="5" class="empty-row">No programmes match the current filters.</td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h3 class="card-title">All Courses</h3>
                                <p class="card-description">Fixed read-only course master data shared across programmes.</p>
                            </div>
                        </div>

                        <form action="programs-courses" method="get" class="field-stack">
                            <input type="hidden" name="programmeIntakeId" value="<%= programmeIntakeIdValue == null ? "" : programmeIntakeIdValue %>" />
                            <input type="hidden" name="programmeSearch" value="<%= programmeSearchValue %>" />
                            <input type="hidden" name="structureProgramCode" value="<%= structureProgramCodeValue %>" />
                            <input type="hidden" name="structureIntakeId" value="<%= structureIntakeIdValue == null ? "" : structureIntakeIdValue %>" />
                            <input type="hidden" name="structureYearId" value="<%= structureYearIdValue == null ? "" : structureYearIdValue %>" />
                            <input type="hidden" name="structureSemesterId" value="<%= structureSemesterIdValue == null ? "" : structureSemesterIdValue %>" />
                            <input type="hidden" name="structureCourseSearch" value="<%= structureCourseSearchValue %>" />
                            <input type="hidden" name="studentId" value="<%= focusStudentIdValue %>" />

                            <label class="field-label-upper" for="courseSearch">Course Code / Name</label>
                            <div class="inline-field-row">
                                <input class="text-input" id="courseSearch" name="courseSearch" type="text" placeholder="Search Course Code or Name" value="<%= courseSearchValue %>" />
                                <div class="action-button-row section-inline-actions">
                                    <button type="submit" class="btn btn-outline btn-compact">Apply</button>
                                    <button type="button" class="btn btn-outline btn-compact"
                                            onclick="this.form.courseSearch.value=''; this.form.submit();">
                                        Reset
                                    </button>
                                </div>
                            </div>
                        </form>

                        <div class="table-wrap top-gap-16">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Course Code</th>
                                        <th>Course Name</th>
                                        <th>Credit Hour</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (courseRowsData != null && !courseRowsData.isEmpty()) { %>
                                        <% for (CourseRecord courseData : courseRowsData) { %>
                                            <tr>
                                                <td class="strong-cell"><%= courseData.getCourseCode() %></td>
                                                <td><%= courseData.getCourseName() %></td>
                                                <td><%= courseData.getCreditHour() %></td>
                                            </tr>
                                        <% } %>
                                    <% } else { %>
                                        <tr>
                                            <td colspan="3" class="empty-row">No courses match the current filters.</td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <section id="programme-structure" class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h3 class="card-title">Programme Year / Semester Courses</h3>
                                <p class="card-description">This section shows which courses belong to a selected programme, intake, year, and semester.</p>
                            </div>
                        </div>

                        <form action="programs-courses#programme-structure" method="get" class="filter-grid-5">
                            <input type="hidden" name="programmeIntakeId" value="<%= programmeIntakeIdValue == null ? "" : programmeIntakeIdValue %>" />
                            <input type="hidden" name="programmeSearch" value="<%= programmeSearchValue %>" />
                            <input type="hidden" name="courseSearch" value="<%= courseSearchValue %>" />
                            <input type="hidden" name="studentId" value="<%= focusStudentIdValue %>" />

                            <div class="field-stack">
                                <label class="field-label-upper" for="structureProgramCode">Programme</label>
                                <select class="select-input" id="structureProgramCode" name="structureProgramCode">
                                    <option value="">All Programmes</option>
                                    <% for (ProgrammeRecord programmeOptionData : programmeOptionRowsData) { %>
                                        <option value="<%= programmeOptionData.getProgramCode() %>" <%= structureProgramCodeValue.equals(programmeOptionData.getProgramCode()) ? "selected" : "" %>>
                                            <%= programmeOptionData.getProgramCode() %> - <%= programmeOptionData.getProgramName() %>
                                        </option>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="structureIntakeId">Intake</label>
                                <select class="select-input" id="structureIntakeId" name="structureIntakeId">
                                    <option value="">All Intakes</option>
                                    <% for (ReferenceOption intakeOptionData : intakeOptionRowsData) { %>
                                        <option value="<%= intakeOptionData.getId() %>" <%= structureIntakeIdValue != null && structureIntakeIdValue == intakeOptionData.getId() ? "selected" : "" %>>
                                            <%= intakeOptionData.getLabel() %>
                                        </option>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="structureYearId">Year</label>
                                <select class="select-input" id="structureYearId" name="structureYearId">
                                    <option value="">All Years</option>
                                    <% for (ReferenceOption yearOptionData : yearOptionRowsData) { %>
                                        <option value="<%= yearOptionData.getId() %>" <%= structureYearIdValue != null && structureYearIdValue == yearOptionData.getId() ? "selected" : "" %>>
                                            <%= yearOptionData.getLabel() %>
                                        </option>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="structureSemesterId">Semester</label>
                                <select class="select-input" id="structureSemesterId" name="structureSemesterId">
                                    <option value="">All Semesters</option>
                                    <% for (ReferenceOption semesterOptionData : semesterOptionRowsData) { %>
                                        <option value="<%= semesterOptionData.getId() %>" <%= structureSemesterIdValue != null && structureSemesterIdValue == semesterOptionData.getId() ? "selected" : "" %>>
                                            <%= semesterOptionData.getLabel() %>
                                        </option>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="structureCourseSearch">Course Search</label>
                                <div class="inline-field-row structure-search-row">
                                    <input class="text-input" id="structureCourseSearch" name="structureCourseSearch" type="text" placeholder="Search Course Code or Name" value="<%= structureCourseSearchValue %>" />
                                    <div class="action-button-row section-inline-actions">
                                        <button type="submit" class="btn btn-outline btn-compact">Apply</button>
                                        <button type="button" class="btn btn-outline btn-compact"
                                                onclick="this.form.structureProgramCode.value=''; this.form.structureIntakeId.value=''; this.form.structureYearId.value=''; this.form.structureSemesterId.value=''; this.form.structureCourseSearch.value=''; this.form.submit();">
                                            Reset
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>

                        <div class="top-gap-16 structure-stack">
                            <% if (structureRowsData != null && !structureRowsData.isEmpty()) { %>
                                <% for (ProgrammeStructureGroup structureGroupData : structureRowsData) { %>
                                    <div class="structure-group">
                                        <div class="structure-group-head">
                                            <div>
                                                <div class="structure-title">
                                                    <%= structureGroupData.getProgramCode() %> - <%= structureGroupData.getProgramName() %>
                                                </div>
                                                <div class="structure-meta">
                                                    <%= structureGroupData.getIntakeName() %> &bull; <%= structureGroupData.getYearName() %> &bull; <%= structureGroupData.getSemesterName() %>
                                                </div>
                                            </div>
                                            <span class="tag-badge neutral"><%= structureGroupData.getCourses().size() %> course(s)</span>
                                        </div>

                                        <div class="table-wrap">
                                            <table class="data-table structure-table">
                                                <colgroup>
                                                    <col class="col-course-code" />
                                                    <col class="col-course-name" />
                                                    <col class="col-credit-hour" />
                                                    <col class="col-assessment-components" />
                                                </colgroup>
                                                <thead>
                                                    <tr>
                                                        <th>Course Code</th>
                                                        <th>Course Name</th>
                                                        <th>Credit Hour</th>
                                                        <th>Assessment Components</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <% for (CourseRecord structureCourseData : structureGroupData.getCourses()) { %>
                                                        <tr>
                                                            <td class="strong-cell"><%= structureCourseData.getCourseCode() %></td>
                                                            <td><%= structureCourseData.getCourseName() %></td>
                                                            <td><%= structureCourseData.getCreditHour() %></td>
                                                            <td><%= structureCourseData.getAssessmentComponents() == null ? "-" : structureCourseData.getAssessmentComponents() %></td>
                                                        </tr>
                                                    <% } %>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                <% } %>
                            <% } else { %>
                                <div class="empty-box structure-empty-box">No programme structure matches the current filters.</div>
                            <% } %>
                        </div>
                    </section>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
