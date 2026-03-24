<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.RecoveryPlanDetail" %>
<%@ page import="com.crs.model.StudentPlanMilestone" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    RecoveryPlanDetail planDetail = (RecoveryPlanDetail) request.getAttribute("planDetail");
    List<StudentPlanMilestone> milestones = (List<StudentPlanMilestone>) request.getAttribute("milestones");

    boolean planCompleted = planDetail != null && "COMPLETED".equalsIgnoreCase(planDetail.getStatus());
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
					
					    <% if (planDetail != null && !"COMPLETED".equalsIgnoreCase(planDetail.getStatus())) { %>
					        <form action="recovery-plan-view" method="post" style="display:inline;">
					            <input type="hidden" name="action" value="removePlan" />
					            <input type="hidden" name="studentPlanId" value="<%= planDetail.getStudentPlanId() %>" />
					            <button type="submit" class="btn btn-outline"
					                    onclick="return confirm('Remove this recovery plan? This will delete all milestones under this plan.');">
					                Remove Plan
					            </button>
					        </form>
					    <% } %>
					</div>

                    <% if (planDetail != null) { %>
                    <div class="summary-grid-4">
                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Student</div>
                                <div class="metric-value" style="font-size:20px;"><%= planDetail.getStudentName() %></div>
                                <div class="template-subtle"><%= planDetail.getStudentId() %></div>
                            </div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Program</div>
                                <div class="metric-value" style="font-size:18px;"><%= planDetail.getProgramName() %></div>
                            </div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Course / Component</div>
                                <div class="metric-value" style="font-size:18px;"><%= planDetail.getCourseName() %></div>
                                <div class="template-subtle"><%= planDetail.getComponentName() %></div>
                            </div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Plan Status</div>
                                <div class="metric-value" style="font-size:18px;">
                                    <% if (planCompleted) { %>
                                        Completed
                                    <% } else { %>
                                        Ongoing
                                    <% } %>
                                </div>
                                <div class="template-subtle"><%= planDetail.getYearName() %> / <%= planDetail.getSemesterName() %></div>
                            </div>
                        </section>
                    </div>

                    <section class="card content-card">
					    <div class="card-title-group">
					        <h2 class="card-title">Recovery Result</h2>
					        <p class="card-description">
					            Enter the final recovery mark as a percentage (0–100) only after all milestones have been completed.
					        </p>
					    </div>
					
					    <div style="margin-top:18px;">
					        <% if (planCompleted) { %>
					            <div class="workspace-grid-2">
					                <div class="card" style="padding:16px;">
					                    <div class="metric-label">Grade</div>
					                    <div class="metric-value" style="font-size:22px;">
					                        <%= planDetail.getGrade() != null ? planDetail.getGrade() : "Pending grading" %>
					                    </div>
					                </div>
					
					                <div class="card" style="padding:16px;">
					                    <div class="metric-label">Grade Point</div>
					                    <div class="metric-value" style="font-size:22px;">
					                        <%
					                            if (planDetail.getGradePoint() != null) {
					                                out.print(String.format("%.2f", planDetail.getGradePoint()));
					                            } else {
					                                out.print("Pending grading");
					                            }
					                        %>
					                    </div>
					                </div>
					            </div>
					
					        <% } else { %>
					            <%
					                boolean hasPendingMilestones = false;
					                if (milestones != null) {
					                    for (StudentPlanMilestone milestone : milestones) {
					                        if (!"COMPLETED".equalsIgnoreCase(milestone.getStatus())) {
					                            hasPendingMilestones = true;
					                            break;
					                        }
					                    }
					                }
					            %>
					
					            <% if (hasPendingMilestones) { %>
					                <div class="empty-row">Complete all milestones first before submitting the final recovery result.</div>
					            <% } else { %>
					                <form action="recovery-plan-view" method="post" class="page-stack">
					                    <input type="hidden" name="action" value="submitRecoveryResult" />
					                    <input type="hidden" name="studentPlanId" value="<%= planDetail.getStudentPlanId() %>" />
					
					                    <div class="field-stack" style="max-width:320px;">
					                        <label class="field-label-upper">Final Recovery Mark (0–100)</label>
					                        <input type="number" name="resultMark" class="text-input"
					                               min="0" max="100" step="0.01" required />
					                    </div>
					
					                    <div class="button-row">
					                        <button type="submit" class="btn btn-primary">Submit Recovery Result</button>
					                    </div>
					                </form>
					            <% } %>
					        <% } %>
					    </div>
					</section>

                    <section class="card content-card">
                        <div class="card-title-group">
                            <h2 class="card-title">Milestones</h2>
                            <p class="card-description">
                                Completed milestones are locked. Completed plans are fully read-only.
                            </p>
                        </div>

                        <div class="page-stack" style="margin-top:18px;">
                        <%
                            if (milestones != null && !milestones.isEmpty()) {
                                for (StudentPlanMilestone milestone : milestones) {
                                    boolean milestoneCompleted = "COMPLETED".equalsIgnoreCase(milestone.getStatus());
                        %>
                            <div class="card" style="padding:18px;">
                                <div class="button-row" style="justify-content:space-between;">
                                    <div class="strong-cell">Task <%= milestone.getMilestoneNo() %></div>
                                    <% if (milestoneCompleted) { %>
                                        <span class="tag-badge success">Completed</span>
                                    <% } else if (planCompleted) { %>
                                        <span class="tag-badge dark">Locked</span>
                                    <% } else { %>
                                        <span class="tag-badge warning">Pending</span>
                                    <% } %>
                                </div>

                                <% if (!planCompleted && !milestoneCompleted) { %>
                                    <form action="recovery-plan-view" method="post" class="page-stack" style="margin-top:14px;">
                                        <input type="hidden" name="studentPlanId" value="<%= planDetail.getStudentPlanId() %>" />
                                        <input type="hidden" name="milestoneId" value="<%= milestone.getStudentPlanMilestoneId() %>" />

                                        <div class="workspace-grid-2">
                                            <div class="field-stack">
                                                <label class="field-label-upper">Milestone Description</label>
                                                <input type="text" name="milestoneDescription" class="text-input"
                                                       value="<%= milestone.getMilestoneDescription() %>" />
                                            </div>

                                            <div class="field-stack">
                                                <label class="field-label-upper">Duration (Days)</label>
                                                <input type="number" name="durationDays" class="text-input"
                                                       value="<%= milestone.getDurationDays() %>" />
                                            </div>
                                        </div>

                                        <div class="button-row">
                                            <button type="submit" name="action" value="updateMilestone" class="btn btn-outline">
                                                Save Changes
                                            </button>
                                            <button type="submit" name="action" value="completeMilestone" class="btn btn-primary">
                                                Mark as Completed
                                            </button>
                                        </div>
                                    </form>
                                <% } else { %>
                                    <div class="template-subtle" style="margin-top:12px;">
                                        <strong>Description:</strong> <%= milestone.getMilestoneDescription() %>
                                    </div>
                                    <div class="template-subtle" style="margin-top:8px;">
                                        <strong>Duration:</strong> <%= milestone.getDurationDays() %> day(s)
                                    </div>
                                    <div class="template-subtle" style="margin-top:8px;">
                                        <strong>Completed At:</strong>
                                        <%= milestone.getCompletedAt() != null ? milestone.getCompletedAt() : "-" %>
                                    </div>
                                <% } %>
                            </div>
                        <%
                                }
                            } else {
                        %>
                            <div class="empty-row">No milestones found for this recovery plan.</div>
                        <%
                            }
                        %>
                        </div>
                    </section>
                    <% } %>

                </div>
            </div>
        </div>
    </div>
</body>
</html>