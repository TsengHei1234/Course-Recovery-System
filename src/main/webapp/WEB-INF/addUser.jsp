<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.Role" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setAttribute("pageTitle", "Add User");
    request.setAttribute("breadcrumb1", "Administration");
    request.setAttribute("breadcrumb2", "User Management");
    request.setAttribute("breadcrumb3", "Add User");
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
                <div class="page-stack">
                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h3 class="card-title">Add User</h3>
                                <p class="card-description">
                                    Create a new user account for the Course Recovery System.
                                </p>
                            </div>
                        </div>

                        <%
                            String errorMessage = (String) request.getAttribute("errorMessage");
                            List<Role> roleList = (List<Role>) request.getAttribute("roleList");
                        %>

                        <% if (errorMessage != null) { %>
                            <div class="helper-box" style="color:#b91c1c; border-color:#fecaca; background:#fef2f2;">
                                <%= errorMessage %>
                            </div>
                        <% } %>

                        <form action="add-user" method="post" class="page-stack">
                            <div class="filter-grid-2">
                                <div class="field-stack">
                                    <label class="field-label-upper">Role</label>
                                    <select name="roleId" class="select-input" required>
                                        <option value="">Select Role</option>
                                        <%
                                            if (roleList != null) {
                                                for (Role role : roleList) {
                                        %>
                                            <option value="<%= role.getRoleId() %>"><%= role.getRoleName() %></option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                </div>

                                <div class="field-stack">
                                    <label class="field-label-upper">Status</label>
                                    <select name="status" class="select-input" required>
                                        <option value="ACTIVE">ACTIVE</option>
                                        <option value="INACTIVE">INACTIVE</option>
                                    </select>
                                </div>
                            </div>

                            <div class="filter-grid-2">
                                <div class="field-stack">
                                    <label class="field-label-upper">Name</label>
                                    <input type="text" name="name" class="text-input" required />
                                </div>

                                <div class="field-stack">
                                    <label class="field-label-upper">Email</label>
                                    <input type="email" name="email" class="text-input" required />
                                </div>
                            </div>

                            <div class="filter-grid-2">
                                <div class="field-stack">
                                    <label class="field-label-upper">Password</label>
                                    <input type="password" name="password" class="text-input" required />
                                </div>
                            </div>

                            <div class="button-row">
                                <button type="submit" class="btn btn-primary">Create User</button>
                                <a href="user-management" class="btn btn-outline">Cancel</a>
                            </div>
                        </form>
                    </section>
                </div>
            </div>
        </div>
    </div>
</body>
</html>