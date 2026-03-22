<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.EligibilityRecord" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    List<EligibilityRecord> awaitingList = (List<EligibilityRecord>) request.getAttribute("awaitingList");
    List<EligibilityRecord> pendingApprovalList = (List<EligibilityRecord>) request.getAttribute("pendingApprovalList");
    List<EligibilityRecord> recoveryQueueList = (List<EligibilityRecord>) request.getAttribute("recoveryQueueList");
    List<EligibilityRecord> processedList = (List<EligibilityRecord>) request.getAttribute("processedList");

    int awaitingCount = awaitingList != null ? awaitingList.size() : 0;
    int pendingApprovalCount = pendingApprovalList != null ? pendingApprovalList.size() : 0;
    int recoveryQueueCount = recoveryQueueList != null ? recoveryQueueList.size() : 0;
    int processedCount = processedList != null ? processedList.size() : 0;

    request.setAttribute("pageTitle", "Eligibility & Enrolment");
    request.setAttribute("breadcrumb1", "Academic Management");
    request.setAttribute("breadcrumb2", "Eligibility & Enrolment");
    request.setAttribute("breadcrumb3", "");
    request.setAttribute("currentPage", "eligibility");
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
                <div class="page-stack">

                    <!-- FILTERS -->
                    <section class="card content-card">
                        <div class="card-title-group">
                            <h2 class="card-title">Eligibility Working Filters</h2>
                            <p class="card-description">
                                Filter students by programme, intake, year, semester, or student ID / name.
                            </p>
                        </div>

                        <div class="filter-grid-5" style="margin-top: 18px;">
                            <div class="field-stack">
                                <label class="field-label-upper" for="eligibilityProgramme">Programme</label>
                                <select id="eligibilityProgramme" class="select-input">
                                    <option value="">All</option>
                                    <option>Diploma in Computer Science</option>
                                    <option>Diploma in IT</option>
                                    <option>Diploma in Software Engineering</option>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="eligibilityIntake">Intake</label>
                                <select id="eligibilityIntake" class="select-input">
                                    <option value="">All</option>
                                    <option>May 2024</option>
                                    <option>May 2025</option>
                                    <option>November 2024</option>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="eligibilityYear">Year</label>
                                <select id="eligibilityYear" class="select-input">
                                    <option value="">All</option>
                                    <option>Year 1</option>
                                    <option>Year 2</option>
                                    <option>Year 3</option>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="eligibilitySemester">Semester</label>
                                <select id="eligibilitySemester" class="select-input">
                                    <option value="">All</option>
                                    <option>Semester 1</option>
                                    <option>Semester 2</option>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="eligibilitySearch">Student ID / Name</label>
                                <div class="inline-field-row">
                                    <input id="eligibilitySearch" type="text" class="text-input" placeholder="Search Student ID or Name" />
                                    <button type="button" class="btn btn-outline">Reset</button>
                                </div>
                            </div>
                        </div>
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
                                <p class="card-description">
                                    Click Check All Eligibility to evaluate all students in this section.
                                </p>
                            </div>
                            <form action="eligibility" method="post" style="display:inline;">
							    <input type="hidden" name="action" value="checkAll"/>
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
								        <td><span class="tag-badge dark">Awaiting Check</span></td>
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
                                <p class="card-description">
                                    Academic Officer can approve individually or in bulk.
                                </p>
                            </div>
                            <div class="button-row">
                                <button type="button" class="btn btn-outline">Select All</button>
                                <button type="button" class="btn btn-outline">Clear All</button>
                                <button type="button" class="btn btn-primary">Approve Selected Eligible</button>
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
								        <td><input type="checkbox" class="table-checkbox" /></td>
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
                                <p class="card-description">
                                    Students here are already checked and found not eligible.
                                </p>
                            </div>
                            <div class="button-row">
                                <button type="button" class="btn btn-outline">Select All</button>
                                <button type="button" class="btn btn-outline">Clear All</button>
                                <button type="button" class="btn btn-primary">Send Selected to Recovery</button>
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
								        <td><input type="checkbox" class="table-checkbox" /></td>
								        <td>
								            <div class="strong-cell"><%= record.getStudentName() %></div>
								            <div class="template-subtle"><%= record.getStudentId() %></div>
								        </td>
								        <td><%= String.format("%.2f", record.getCgpa()) %></td>
								        <td><%= record.getFailedCourseCount() %></td>
								        <td class="template-subtle"><%= record.getReason() != null ? record.getReason() : "-" %></td>
								        <td>
								            <button type="button" class="btn btn-outline btn-compact">Send to Recovery</button>
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
                            <p class="card-description">
                                Approved cases and sent-to-recovery cases are moved here after final action.
                            </p>
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
</body>
</html>