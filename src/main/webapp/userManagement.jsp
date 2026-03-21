<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.User" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setAttribute("pageTitle", "User Management");
    request.setAttribute("breadcrumb1", "Administration");
    request.setAttribute("breadcrumb2", "User Management");
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
                                <h3 class="card-title">User List</h3>
                                <p class="card-description">
                                    View all registered system users and manage account status.
                                </p>
                            </div>

                            <a href="add-user" class="btn btn-primary">Add User</a>
                        </div>

                        <div class="table-wrap">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>User ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Role</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <%
                                    List<User> userList = (List<User>) request.getAttribute("userList");

                                    if (userList != null && !userList.isEmpty()) {
                                        for (User user : userList) {
                                %>
                                    <tr>
                                        <td class="strong-cell"><%= user.getUserId() %></td>
                                        <td><%= user.getName() %></td>
                                        <td><%= user.getEmail() %></td>
                                        <td><%= user.getRoleName() %></td>
                                        <td>
                                            <% if ("ACTIVE".equalsIgnoreCase(user.getStatus())) { %>
                                                <span class="tag-badge success">Active</span>
                                            <% } else { %>
                                                <span class="tag-badge warning">Inactive</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <div class="button-row">
                                            	<a href="edit-user?userId=<%= user.getUserId() %>" class="btn btn-outline btn-compact">Edit</a>

                                                <form action="user-status" method="post" style="display:inline;">
                                                    <input type="hidden" name="userId" value="<%= user.getUserId() %>" />
                                                    <% if ("ACTIVE".equalsIgnoreCase(user.getStatus())) { %>
                                                        <input type="hidden" name="status" value="INACTIVE" />
                                                        <button type="submit" class="btn btn-outline btn-compact">Deactivate</button>
                                                    <% } else { %>
                                                        <input type="hidden" name="status" value="ACTIVE" />
                                                        <button type="submit" class="btn btn-primary btn-compact">Activate</button>
                                                    <% } %>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="6" class="empty-row">No users found.</td>
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