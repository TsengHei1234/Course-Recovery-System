<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.Role" %>
<%@ page import="com.crs.model.User" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setAttribute("pageTitle", "Edit User");
    request.setAttribute("breadcrumb1", "Administration");
    request.setAttribute("breadcrumb2", "User Management");
    request.setAttribute("breadcrumb3", "Edit User");
    request.setAttribute("currentPage", "users");
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
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h3 class="card-title">Edit User</h3>
                                <p class="card-description">
                                    Update user account details and access status.
                                </p>
                            </div>
                        </div>

                        <%
						    String formErrorMessage = (String) request.getAttribute("errorMessage");
						    User user = (User) request.getAttribute("user");
						    List<Role> roleList = (List<Role>) request.getAttribute("roleList");
						%>
						
						<% if (formErrorMessage != null) { %>
						    <div class="helper-box" style="color:#b91c1c; border-color:#fecaca; background:#fef2f2;">
						        <%= formErrorMessage %>
						    </div>
						<% } %>

                        <% if (user != null) { %>
                        <form action="edit-user" method="post" class="page-stack">
                            <input type="hidden" name="userId" value="<%= user.getUserId() %>" />

                            <div class="form-shell">

                                <div class="field-stack form-narrow form-gap">
                                    <label class="field-label-upper">Role</label>
                                    <select name="roleId" class="select-input" required>
                                        <option value="">Select Role</option>
                                        <%
                                            if (roleList != null) {
                                                for (Role role : roleList) {
                                        %>
                                            <option value="<%= role.getRoleId() %>"
                                                <%= role.getRoleId() == user.getRoleId() ? "selected" : "" %>>
                                                <%= role.getRoleName() %>
                                            </option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                </div>

                                <div class="workspace-grid-2 form-gap">
                                    <div class="field-stack">
                                        <label class="field-label-upper">Name</label>
                                        <input type="text" name="name" class="text-input"
                                               value="<%= user.getName() %>" required />
                                    </div>

                                    <div class="field-stack">
                                        <label class="field-label-upper">Email</label>
                                        <input type="email" name="email" class="text-input"
                                               value="<%= user.getEmail() %>" required />
                                    </div>
                                </div>

                                <div class="field-stack form-narrow form-gap-lg">
                                    <label class="field-label-upper">Status</label>
                                    <select name="status" class="select-input" required>
                                        <option value="ACTIVE" <%= "ACTIVE".equalsIgnoreCase(user.getStatus()) ? "selected" : "" %>>ACTIVE</option>
                                        <option value="INACTIVE" <%= "INACTIVE".equalsIgnoreCase(user.getStatus()) ? "selected" : "" %>>INACTIVE</option>
                                    </select>
                                </div>

                                <div class="button-row">
                                    <button type="submit" class="btn btn-primary">Update User</button>
                                    <a href="user-management" class="btn btn-outline">Cancel</a>
                                </div>
                            </div>
                        </form>
                        <% } %>
                    </section>
                </div>
            </div>
        </div>
    </div>
</body>
</html>