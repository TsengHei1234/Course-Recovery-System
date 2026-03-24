<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.RecoveryWorkspaceStudent" %>
<%@ page import="com.crs.model.RecoveryComponentRecord" %>
<%@ page import="com.crs.model.StudentPlanMilestone" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    RecoveryWorkspaceStudent workspaceStudent = (RecoveryWorkspaceStudent) request.getAttribute("workspaceStudent");
    List<RecoveryComponentRecord> componentList = (List<RecoveryComponentRecord>) request.getAttribute("componentList");
    RecoveryComponentRecord selectedComponent = (RecoveryComponentRecord) request.getAttribute("selectedComponent");
    String selectedPlanStatus = (String) request.getAttribute("selectedPlanStatus");
    List<StudentPlanMilestone> savedMilestones = (List<StudentPlanMilestone>) request.getAttribute("savedMilestones");
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

                    <div class="button-row">
                        <a href="recovery-plans" class="btn btn-outline">Back to Recovery Plans</a>
                    </div>

                    <% if (workspaceStudent != null) { %>
                    <div class="summary-grid-4">
                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Student ID</div>
                                <div class="metric-value" style="font-size:20px;"><%= workspaceStudent.getStudentId() %></div>
                            </div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Student Name</div>
                                <div class="metric-value" style="font-size:20px;"><%= workspaceStudent.getStudentName() %></div>
                            </div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Program</div>
                                <div class="metric-value" style="font-size:18px;"><%= workspaceStudent.getProgramName() %></div>
                            </div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Current Term</div>
                                <div class="metric-value" style="font-size:18px;"><%= workspaceStudent.getYearName() %> / <%= workspaceStudent.getSemesterName() %></div>
                            </div>
                        </section>
                    </div>
                    <% } %>

                    <section class="card content-card">
                        <div class="card-title-group">
                            <h2 class="card-title">Course Component Results</h2>
                            <p class="card-description">
                                Select a FAILED component only. The manual milestone builder below will follow the selected component.
                            </p>
                        </div>

                        <div class="table-wrap" style="margin-top:18px;">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Course</th>
                                        <th>Component</th>
                                        <th>Weight</th>
                                        <th>Mark</th>
                                        <th>Attempt</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (componentList != null && !componentList.isEmpty()) {
                                        for (RecoveryComponentRecord record : componentList) {
                                %>
                                    <tr>
                                        <td><%= record.getCourseName() %></td>
                                        <td><%= record.getComponentName() %></td>
                                        <td><%= String.format("%.2f", record.getWeightPercent()) %>%</td>
                                        <td><%= String.format("%.2f", record.getMark()) %></td>
                                        <td>Attempt <%= record.getAttemptNo() %></td>
                                        <td>
                                            <% if ("FAILED".equalsIgnoreCase(record.getComponentStatus())) { %>
                                                <span class="tag-badge warning">Failed</span>
                                            <% } else { %>
                                                <span class="tag-badge success">Passed</span>
                                            <% } %>
                                        </td>
                                        <td>
											<% if (!"FAILED".equalsIgnoreCase(record.getComponentStatus())) { %>
											    <button type="button" class="btn btn-outline btn-compact" disabled>Passed</button>
											
											<% } else if (record.getExistingPlanId() == null && record.getAttemptNo() == 1) { %>
											    <!-- 第一次 recovery：只有 attempt 1 才能開 attempt 2 -->
											    <form action="recovery-workspace" method="post" style="display:inline;">
											        <input type="hidden" name="action" value="startRecovery" />
											        <input type="hidden" name="studentId" value="<%= workspaceStudent.getStudentId() %>" />
											        <input type="hidden" name="studentCourseComponentId" value="<%= record.getStudentCourseComponentId() %>" />
											        <button type="submit" class="btn btn-primary btn-compact">Select</button>
											    </form>
											
											<% } else if (record.getExistingPlanId() == null && record.getAttemptNo() > 1) { %>
											    <!-- attempt 2 / 3 已經存在，只是這顆 component 還沒 plan，直接進 builder，不要再開新 attempt -->
											    <a href="recovery-workspace?studentId=<%= workspaceStudent.getStudentId() %>&selectedComponentId=<%= record.getStudentCourseComponentId() %>"
											       class="btn btn-primary btn-compact">
											        Select
											    </a>
											
											<% } else if ("ONGOING".equalsIgnoreCase(record.getExistingPlanStatus()) || "DRAFT".equalsIgnoreCase(record.getExistingPlanStatus())) { %>
											    <button type="button" class="btn btn-outline btn-compact" disabled>Plan Exists</button>
											
											<% } else if ("COMPLETED".equalsIgnoreCase(record.getExistingPlanStatus()) && record.getAttemptNo() < 3) { %>
											    <!-- 已有 completed plan，而且還沒到 attempt 3，才允許 Add New Plan -->
											    <form action="recovery-workspace" method="post" style="display:inline;">
											        <input type="hidden" name="action" value="addNewPlan" />
											        <input type="hidden" name="studentId" value="<%= workspaceStudent.getStudentId() %>" />
											        <input type="hidden" name="studentCourseComponentId" value="<%= record.getStudentCourseComponentId() %>" />
											        <button type="submit" class="btn btn-primary btn-compact">Add New Plan</button>
											    </form>
											
											<% } else { %>
											    <button type="button" class="btn btn-outline btn-compact" disabled>Max Attempts Reached</button>
											<% } %>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="7" class="empty-row">No course component results found.</td>
                                    </tr>
                                <%
                                    }
                                %>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <div class="dashboard-grid content-grid">
                        <section class="card content-card">
                            <div class="card-title-row">
                                <div class="card-title-group">
                                    <h2 class="card-title">Manual Milestone Builder</h2>
                                    <p class="card-description">
                                        Selected component:
                                        <%= selectedComponent != null ? selectedComponent.getComponentName() + " (" + selectedComponent.getCourseName() + ")" : "None selected" %>
                                    </p>
                                </div>
                            </div>

                            <% if (selectedComponent != null) { %>
                            <form action="recovery-workspace" method="post" id="milestoneForm" class="page-stack">
                                <input type="hidden" name="studentId" value="<%= workspaceStudent.getStudentId() %>" />
                                <input type="hidden" name="studentCourseId" value="<%= selectedComponent.getStudentCourseId() %>" />
                                <input type="hidden" name="studentCourseComponentId" value="<%= selectedComponent.getStudentCourseComponentId() %>" />

                                <div id="milestoneRows" class="page-stack">
                                    <div class="card" style="padding:16px;">
                                        <div class="button-row" style="justify-content:space-between;">
                                            <div class="strong-cell">Task 1</div>
                                            <button type="button" class="btn btn-outline btn-compact" onclick="removeRow(this)">Remove</button>
                                        </div>
                                        <div class="workspace-grid-2" style="margin-top:12px;">
                                            <div class="field-stack">
                                                <label class="field-label-upper">Milestone Description</label>
                                                <input type="text" name="milestoneDescription" class="text-input" placeholder="Enter milestone description" />
                                            </div>
                                            <div class="field-stack">
                                                <label class="field-label-upper">Duration (Days)</label>
                                                <input type="number" name="milestoneDuration" class="text-input" placeholder="Duration" />
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="button-row">
                                    <button type="button" class="btn btn-outline" onclick="addMilestoneRow()">Add Milestone</button>
                                    <button type="submit" name="action" value="saveMilestones" class="btn btn-primary">Save Milestone</button>
                                </div>
                            </form>
                            <% } else { %>
                                <div class="empty-row">Select a failed component first.</div>
                            <% } %>
                        </section>

                        <section class="card content-card">
                            <div class="card-title-group">
                                <h2 class="card-title">Saved Milestones For This Component</h2>
                                <p class="card-description">
                                    What has already been written for the selected failed component.
                                </p>
                            </div>

                            <div style="margin-top:18px;">
                                <% if (selectedComponent == null) { %>
                                    <div class="empty-row">Select a failed component first.</div>
                                <% } else if (savedMilestones != null && !savedMilestones.isEmpty()) { %>

                                    <div class="button-row" style="justify-content:space-between; margin-bottom:12px;">
                                        <div class="strong-cell"><%= selectedComponent.getComponentName() %></div>
                                        <% if ("COMPLETED".equalsIgnoreCase(selectedPlanStatus)) { %>
                                            <span class="tag-badge success">Completed</span>
                                        <% } else { %>
                                            <span class="tag-badge warning">Ongoing</span>
                                        <% } %>
                                    </div>

                                    <div class="page-stack">
                                    <%
                                        for (StudentPlanMilestone milestone : savedMilestones) {
                                    %>
                                        <div class="card" style="padding:14px;">
                                            <div class="button-row" style="justify-content:space-between;">
                                                <div class="strong-cell">Task <%= milestone.getMilestoneNo() %></div>
                                                <% if ("COMPLETED".equalsIgnoreCase(milestone.getStatus())) { %>
                                                    <span class="tag-badge success">Completed</span>
                                                <% } else { %>
                                                    <span class="tag-badge warning">Pending</span>
                                                <% } %>
                                            </div>
                                            <div class="template-subtle" style="margin-top:8px;">
                                                <%= milestone.getMilestoneDescription() %>
                                            </div>
                                            <div class="template-subtle" style="margin-top:6px;">
                                                Duration: <%= milestone.getDurationDays() %> day(s)
                                            </div>
                                        </div>
                                    <%
                                        }
                                    %>
                                    </div>

                                <% } else { %>
                                    <div class="empty-row">No milestones have been saved for this component yet.</div>
                                <% } %>
                            </div>
                        </section>
                    </div>

                </div>
            </div>
        </div>
    </div>

    <script>
        function addMilestoneRow() {
            var container = document.getElementById("milestoneRows");
            var count = container.children.length + 1;

            var wrapper = document.createElement("div");
            wrapper.className = "card";
            wrapper.style.padding = "16px";

            wrapper.innerHTML =
                '<div class="button-row" style="justify-content:space-between;">' +
                    '<div class="strong-cell">Task ' + count + '</div>' +
                    '<button type="button" class="btn btn-outline btn-compact" onclick="removeRow(this)">Remove</button>' +
                '</div>' +
                '<div class="workspace-grid-2" style="margin-top:12px;">' +
                    '<div class="field-stack">' +
                        '<label class="field-label-upper">Milestone Description</label>' +
                        '<input type="text" name="milestoneDescription" class="text-input" placeholder="Enter milestone description" />' +
                    '</div>' +
                    '<div class="field-stack">' +
                        '<label class="field-label-upper">Duration (Days)</label>' +
                        '<input type="number" name="milestoneDuration" class="text-input" placeholder="Duration" />' +
                    '</div>' +
                '</div>';

            container.appendChild(wrapper);
        }

        function removeRow(button) {
            var container = document.getElementById("milestoneRows");
            if (container.children.length === 1) {
                return;
            }
            button.closest(".card").remove();
        }
    </script>
</body>
</html>