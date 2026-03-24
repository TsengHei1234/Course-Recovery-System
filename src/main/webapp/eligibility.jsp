<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.EligibilityRecord" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    List<EligibilityRecord> awaitingList = (List<EligibilityRecord>) request.getAttribute("awaitingList");
    List<EligibilityRecord> pendingApprovalList = (List<EligibilityRecord>) request.getAttribute("pendingApprovalList");
    List<EligibilityRecord> recoveryQueueList = (List<EligibilityRecord>) request.getAttribute("recoveryQueueList");
    List<EligibilityRecord> processedList = (List<EligibilityRecord>) request.getAttribute("processedList");

    List<String> programmeOptions = (List<String>) request.getAttribute("programmeOptions");
    List<String> intakeOptions = (List<String>) request.getAttribute("intakeOptions");
    List<String> yearOptions = (List<String>) request.getAttribute("yearOptions");
    List<String> semesterOptions = (List<String>) request.getAttribute("semesterOptions");

    String filterProgramme = (String) request.getAttribute("filterProgramme");
    String filterIntake = (String) request.getAttribute("filterIntake");
    String filterYearName = (String) request.getAttribute("filterYearName");
    String filterSemesterName = (String) request.getAttribute("filterSemesterName");
    String filterSearch = (String) request.getAttribute("filterSearch");

    int awaitingCount = awaitingList != null ? awaitingList.size() : 0;
    int pendingApprovalCount = pendingApprovalList != null ? pendingApprovalList.size() : 0;
    int recoveryQueueCount = recoveryQueueList != null ? recoveryQueueList.size() : 0;
    int processedCount = processedList != null ? processedList.size() : 0;
%>

<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
</head>
<body class="app-body">
    <%@ include file="/WEB-INF/jspf/auth-check.jspf" %>

    <div class="app-shell">
        <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>

        <div class="app-main">
            <%@ include file="/WEB-INF/jspf/topbar.jspf" %>

            <div class="app-content">
            	<%@ include file="/WEB-INF/jspf/flash-message.jspf" %>
                <div class="page-stack">

                    <!-- FILTERS -->
                    <section class="card content-card">
                        <div class="card-title-group">
                            <h2 class="card-title">Eligibility Working Filters</h2>
                            <p class="card-description">
                                Programme, Intake, Year, Semester, and Student ID / Name. Actions apply to the currently visible filtered rows.
                            </p>
                        </div>

                        <form action="eligibility" method="get">
                            <div class="filter-grid-5" style="margin-top: 18px;">
                                <div class="field-stack">
                                    <label class="field-label-upper" for="programme">Programme</label>
                                    <select id="programme" name="programme" class="select-input">
                                        <option value="">All</option>
                                        <%
                                            if (programmeOptions != null) {
                                                for (String option : programmeOptions) {
                                        %>
                                            <option value="<%= option %>" <%= option.equals(filterProgramme) ? "selected" : "" %>><%= option %></option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                </div>

                                <div class="field-stack">
                                    <label class="field-label-upper" for="intake">Intake</label>
                                    <select id="intake" name="intake" class="select-input">
                                        <option value="">All</option>
                                        <%
                                            if (intakeOptions != null) {
                                                for (String option : intakeOptions) {
                                        %>
                                            <option value="<%= option %>" <%= option.equals(filterIntake) ? "selected" : "" %>><%= option %></option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                </div>

                                <div class="field-stack">
                                    <label class="field-label-upper" for="yearName">Year</label>
                                    <select id="yearName" name="yearName" class="select-input">
                                        <option value="">All</option>
                                        <%
                                            if (yearOptions != null) {
                                                for (String option : yearOptions) {
                                        %>
                                            <option value="<%= option %>" <%= option.equals(filterYearName) ? "selected" : "" %>><%= option %></option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                </div>

                                <div class="field-stack">
                                    <label class="field-label-upper" for="semesterName">Semester</label>
                                    <select id="semesterName" name="semesterName" class="select-input">
                                        <option value="">All</option>
                                        <%
                                            if (semesterOptions != null) {
                                                for (String option : semesterOptions) {
                                        %>
                                            <option value="<%= option %>" <%= option.equals(filterSemesterName) ? "selected" : "" %>><%= option %></option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                </div>

                                <div class="field-stack">
                                    <label class="field-label-upper" for="search">Student ID / Name</label>
                                    <div class="inline-field-row">
                                        <input id="search" name="search" type="text" class="text-input"
                                               value="<%= filterSearch != null ? filterSearch : "" %>"
                                               placeholder="Search Student ID or Name" />
                                        <a href="eligibility" class="btn btn-outline">Reset</a>
                                    </div>
                                </div>
                            </div>

                            <div class="button-row" style="margin-top: 16px;">
                                <button type="submit" class="btn btn-primary">Apply Filters</button>
                            </div>
                        </form>
                    </section>

                    <!-- SUMMARY -->
                    <div class="summary-grid-4">
                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Awaiting Check</div>
                                <div class="metric-value"><%= awaitingCount %></div>
                            </div>
                            <div class="metric-icon">&#10003;</div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Eligible Pending Approval</div>
                                <div class="metric-value"><%= pendingApprovalCount %></div>
                            </div>
                            <div class="metric-icon">&#9673;</div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Recovery Queue</div>
                                <div class="metric-value"><%= recoveryQueueCount %></div>
                            </div>
                            <div class="metric-icon">&#9678;</div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Processed Cases</div>
                                <div class="metric-value"><%= processedCount %></div>
                            </div>
                            <div class="metric-icon">&#9635;</div>
                        </section>
                    </div>

                    <!-- AWAITING CHECK -->
                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h2 class="card-title">Awaiting Eligibility Check (<%= awaitingCount %>)</h2>
                                <p class="card-description">Check All Eligibility evaluates only the currently visible filtered students.</p>
                            </div>

                            <form action="eligibility" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="checkAll" />
                                <input type="hidden" name="programme" value="<%= filterProgramme != null ? filterProgramme : "" %>" />
                                <input type="hidden" name="intake" value="<%= filterIntake != null ? filterIntake : "" %>" />
                                <input type="hidden" name="yearName" value="<%= filterYearName != null ? filterYearName : "" %>" />
                                <input type="hidden" name="semesterName" value="<%= filterSemesterName != null ? filterSemesterName : "" %>" />
                                <input type="hidden" name="search" value="<%= filterSearch != null ? filterSearch : "" %>" />
                                <button type="submit" class="btn btn-primary">Check All Eligibility</button>
                            </form>
                        </div>

                        <div class="table-wrap">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Student</th>
                                        <th>Programme</th>
                                        <th>CGPA</th>
                                        <th>Failed Courses</th>
                                        <th>Current Term</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (awaitingList != null && !awaitingList.isEmpty()) {
                                        for (EligibilityRecord record : awaitingList) {
                                %>
                                    <tr>
                                        <td>
                                            <div class="strong-cell"><%= record.getStudentName() %></div>
                                            <div class="template-subtle"><%= record.getStudentId() %></div>
                                        </td>
                                        <td><%= record.getProgrammeName() %></td>
                                        <td><%= String.format("%.2f", record.getCgpa()) %></td>
                                        <td><%= record.getFailedCourseCount() %></td>
                                        <td><%= record.getCurrentYearName() %> / <%= record.getCurrentSemesterName() %></td>
                                        <td>
										    <% if ("AWAITING_RECHECK".equalsIgnoreCase(record.getStatus())) { %>
										        <span class="tag-badge dark">Awaiting Re-check</span>
										    <% } else { %>
										        <span class="tag-badge dark">Awaiting Check</span>
										    <% } %>
										</td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="6" class="empty-row">No students are awaiting eligibility check.</td>
                                    </tr>
                                <%
                                    }
                                %>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <!-- PENDING APPROVAL -->
                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h2 class="card-title">Eligible Pending Approval (<%= pendingApprovalCount %>)</h2>
                                <p class="card-description">Approve individually or approve the selected visible records.</p>
                            </div>
                            <div class="button-row">
                                <button type="button" class="btn btn-outline" onclick="toggleSelection('approval-checkbox', true)">Select All</button>
                                <button type="button" class="btn btn-outline" onclick="toggleSelection('approval-checkbox', false)">Clear All</button>

                                <form id="approveSelectedForm" action="eligibility" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="approveSelected" />
                                    <input type="hidden" id="approveSelectedIds" name="progressionIds" />
                                    <input type="hidden" name="programme" value="<%= filterProgramme != null ? filterProgramme : "" %>" />
                                    <input type="hidden" name="intake" value="<%= filterIntake != null ? filterIntake : "" %>" />
                                    <input type="hidden" name="yearName" value="<%= filterYearName != null ? filterYearName : "" %>" />
                                    <input type="hidden" name="semesterName" value="<%= filterSemesterName != null ? filterSemesterName : "" %>" />
                                    <input type="hidden" name="search" value="<%= filterSearch != null ? filterSearch : "" %>" />
                                    <button type="button" class="btn btn-primary" onclick="submitBulk('approval-checkbox', 'approveSelectedIds', 'approveSelectedForm')">
                                        Approve Selected Eligible
                                    </button>
                                </form>
                            </div>
                        </div>

                        <div class="table-wrap">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Select</th>
                                        <th>Student</th>
                                        <th>CGPA</th>
                                        <th>Failed Courses</th>
                                        <th>Reason</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (pendingApprovalList != null && !pendingApprovalList.isEmpty()) {
                                        for (EligibilityRecord record : pendingApprovalList) {
                                %>
                                    <tr>
                                        <td>
                                            <input type="checkbox" class="table-checkbox approval-checkbox" value="<%= record.getProgressionId() %>" />
                                        </td>
                                        <td>
                                            <div class="strong-cell"><%= record.getStudentName() %></div>
                                            <div class="template-subtle"><%= record.getStudentId() %></div>
                                        </td>
                                        <td><%= String.format("%.2f", record.getCgpa()) %></td>
                                        <td><%= record.getFailedCourseCount() %></td>
                                        <td class="template-subtle"><%= record.getReason() != null ? record.getReason() : "-" %></td>
                                        <td>
                                            <form action="eligibility" method="post" style="display:inline;">
                                                <input type="hidden" name="action" value="approveOne" />
                                                <input type="hidden" name="progressionId" value="<%= record.getProgressionId() %>" />
                                                <input type="hidden" name="programme" value="<%= filterProgramme != null ? filterProgramme : "" %>" />
                                                <input type="hidden" name="intake" value="<%= filterIntake != null ? filterIntake : "" %>" />
                                                <input type="hidden" name="yearName" value="<%= filterYearName != null ? filterYearName : "" %>" />
                                                <input type="hidden" name="semesterName" value="<%= filterSemesterName != null ? filterSemesterName : "" %>" />
                                                <input type="hidden" name="search" value="<%= filterSearch != null ? filterSearch : "" %>" />
                                                <button type="submit" class="btn btn-primary btn-compact">Approve Enrolment</button>
                                            </form>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="6" class="empty-row">No students are pending approval.</td>
                                    </tr>
                                <%
                                    }
                                %>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <!-- RECOVERY QUEUE -->
                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h2 class="card-title">Not Eligible / Recovery Queue (<%= recoveryQueueCount %>)</h2>
                                <p class="card-description">Send individually or send the selected visible records to recovery.</p>
                            </div>
                            <div class="button-row">
                                <button type="button" class="btn btn-outline" onclick="toggleSelection('recovery-checkbox', true)">Select All</button>
                                <button type="button" class="btn btn-outline" onclick="toggleSelection('recovery-checkbox', false)">Clear All</button>

                                <form id="sendSelectedForm" action="eligibility" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="sendSelected" />
                                    <input type="hidden" id="sendSelectedIds" name="progressionIds" />
                                    <input type="hidden" name="programme" value="<%= filterProgramme != null ? filterProgramme : "" %>" />
                                    <input type="hidden" name="intake" value="<%= filterIntake != null ? filterIntake : "" %>" />
                                    <input type="hidden" name="yearName" value="<%= filterYearName != null ? filterYearName : "" %>" />
                                    <input type="hidden" name="semesterName" value="<%= filterSemesterName != null ? filterSemesterName : "" %>" />
                                    <input type="hidden" name="search" value="<%= filterSearch != null ? filterSearch : "" %>" />
                                    <button type="button" class="btn btn-primary" onclick="submitBulk('recovery-checkbox', 'sendSelectedIds', 'sendSelectedForm')">
                                        Send Selected to Recovery
                                    </button>
                                </form>
                            </div>
                        </div>

                        <div class="table-wrap">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Select</th>
                                        <th>Student</th>
                                        <th>CGPA</th>
                                        <th>Failed Courses</th>
                                        <th>Reason</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (recoveryQueueList != null && !recoveryQueueList.isEmpty()) {
                                        for (EligibilityRecord record : recoveryQueueList) {
                                %>
                                    <tr>
                                        <td>
                                            <input type="checkbox" class="table-checkbox recovery-checkbox" value="<%= record.getProgressionId() %>" />
                                        </td>
                                        <td>
                                            <div class="strong-cell"><%= record.getStudentName() %></div>
                                            <div class="template-subtle"><%= record.getStudentId() %></div>
                                        </td>
                                        <td><%= String.format("%.2f", record.getCgpa()) %></td>
                                        <td><%= record.getFailedCourseCount() %></td>
                                        <td class="template-subtle"><%= record.getReason() != null ? record.getReason() : "-" %></td>
                                        <td>
                                            <form action="eligibility" method="post" style="display:inline;">
                                                <input type="hidden" name="action" value="sendOne" />
                                                <input type="hidden" name="progressionId" value="<%= record.getProgressionId() %>" />
                                                <input type="hidden" name="programme" value="<%= filterProgramme != null ? filterProgramme : "" %>" />
                                                <input type="hidden" name="intake" value="<%= filterIntake != null ? filterIntake : "" %>" />
                                                <input type="hidden" name="yearName" value="<%= filterYearName != null ? filterYearName : "" %>" />
                                                <input type="hidden" name="semesterName" value="<%= filterSemesterName != null ? filterSemesterName : "" %>" />
                                                <input type="hidden" name="search" value="<%= filterSearch != null ? filterSearch : "" %>" />
                                                <button type="submit" class="btn btn-outline btn-compact">Send to Recovery</button>
                                            </form>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="6" class="empty-row">No students are in recovery queue.</td>
                                    </tr>
                                <%
                                    }
                                %>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <!-- PROCESSED -->
                    <section class="card content-card">
                        <div class="card-title-group">
                            <h2 class="card-title">Processed Cases (<%= processedCount %>)</h2>
                            <p class="card-description">Approved students and sent-to-recovery students are shown here.</p>
                        </div>

                        <div class="table-wrap" style="margin-top: 18px;">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Student</th>
                                        <th>Programme</th>
                                        <th>Decision</th>
                                        <th>Reason</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (processedList != null && !processedList.isEmpty()) {
                                        for (EligibilityRecord record : processedList) {
                                %>
                                    <tr>
                                        <td>
                                            <div class="strong-cell"><%= record.getStudentName() %></div>
                                            <div class="template-subtle"><%= record.getStudentId() %></div>
                                        </td>
                                        <td><%= record.getProgrammeName() %></td>
                                        <td>
                                            <% if ("APPROVED".equalsIgnoreCase(record.getStatus())) { %>
											    <span class="tag-badge success">Approved</span>
											<% } else if ("COMPLETED_STUDY".equalsIgnoreCase(record.getStatus())) { %>
											    <span class="tag-badge success">Completed Study</span>
											<% } else { %>
											    <span class="tag-badge warning">Sent to Recovery</span>
											<% } %>
                                        </td>
                                        <td class="template-subtle"><%= record.getReason() != null ? record.getReason() : "-" %></td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="4" class="empty-row">No processed cases found.</td>
                                    </tr>
                                <%
                                    }
                                %>
                                </tbody>
                            </table>
                        </div>
                    </section>

                </div>
            </div>
        </div>
    </div>

    <script>
        function toggleSelection(className, checked) {
            var boxes = document.querySelectorAll("." + className);
            boxes.forEach(function (box) {
                box.checked = checked;
            });
        }

        function submitBulk(className, hiddenInputId, formId) {
            var boxes = document.querySelectorAll("." + className + ":checked");
            var ids = [];

            boxes.forEach(function (box) {
                ids.push(box.value);
            });

            if (ids.length === 0) {
                alert("Please select at least one row first.");
                return;
            }

            document.getElementById(hiddenInputId).value = ids.join(",");
            document.getElementById(formId).submit();
        }
    </script>
</body>
</html>