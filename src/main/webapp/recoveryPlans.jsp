<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.RecoveryPendingRecord" %>
<%@ page import="com.crs.model.RecoveryActiveRecord" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    List<RecoveryPendingRecord> pendingList = (List<RecoveryPendingRecord>) request.getAttribute("pendingList");
    List<RecoveryActiveRecord> activeList = (List<RecoveryActiveRecord>) request.getAttribute("activeList");
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

                    <section class="card content-card">
                        <div class="card-title-group">
                            <h2 class="card-title">Students Requiring Recovery Action</h2>
                            <p class="card-description">
                                Students sent into recovery workflow. Open Workspace to manually create component-level milestones.
                            </p>
                        </div>

                        <div class="table-wrap" style="margin-top:18px;">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Student</th>
                                        <th>Program</th>
                                        <th>Current Term</th>
                                        <th>Failed Components</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (pendingList != null && !pendingList.isEmpty()) {
                                        for (RecoveryPendingRecord record : pendingList) {
                                %>
                                    <tr>
                                        <td>
                                            <div class="strong-cell"><%= record.getStudentName() %></div>
                                            <div class="template-subtle"><%= record.getStudentId() %></div>
                                        </td>
                                        <td><%= record.getProgramName() %></td>
                                        <td><%= record.getCurrentYearName() %> / <%= record.getCurrentSemesterName() %></td>
                                        <td><%= record.getFailedComponentsCount() %></td>
                                        <td>
                                            <a href="recovery-workspace?studentId=<%= record.getStudentId() %>" class="btn btn-primary btn-compact">
                                                Open Workspace
                                            </a>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="5" class="empty-row">No students are currently waiting for recovery action.</td>
                                    </tr>
                                <%
                                    }
                                %>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <section class="card content-card">
                        <div class="card-title-group">
                            <h2 class="card-title">Students With Existing Recovery Action</h2>
                            <p class="card-description">
                                Recovery actions that have already been created and are being tracked.
                            </p>
                        </div>

                        <div class="table-wrap" style="margin-top:18px;">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Student</th>
                                        <th>Course</th>
                                        <th>Component</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (activeList != null && !activeList.isEmpty()) {
                                        for (RecoveryActiveRecord record : activeList) {
                                %>
                                    <tr>
                                        <td>
                                            <div class="strong-cell"><%= record.getStudentName() %></div>
                                            <div class="template-subtle"><%= record.getStudentId() %></div>
                                        </td>
                                        <td><%= record.getCourseName() %></td>
                                        <td><%= record.getComponentName() %></td>
                                        <td>
                                            <% if ("COMPLETED".equalsIgnoreCase(record.getStatus())) { %>
                                                <span class="tag-badge success">Completed</span>
                                            <% } else { %>
                                                <span class="tag-badge warning">Ongoing</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <a href="recovery-plan-view?studentPlanId=<%= record.getStudentPlanId() %>"
                                               class="btn btn-primary btn-compact">
                                                View
                                            </a>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="5" class="empty-row">No recovery actions have been created yet.</td>
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