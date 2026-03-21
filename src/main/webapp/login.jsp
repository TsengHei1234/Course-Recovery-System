<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CRS Login</title>
    <style>
        * {
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        body {
            margin: 0;
            background: #f1f5f9;
        }

        .container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 30px;
        }

        .wrapper {
            width: 100%;
            max-width: 1100px;
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 24px;
        }

        .left-panel {
            background: #0f172a;
            color: white;
            border-radius: 24px;
            padding: 50px 40px;
        }

        .left-panel h1 {
            font-size: 36px;
            margin-bottom: 20px;
        }

        .left-panel p {
            color: #cbd5e1;
            line-height: 1.7;
        }

        .feature-list {
            margin-top: 30px;
            padding-left: 20px;
        }

        .feature-list li {
            margin-bottom: 12px;
        }

        .right-panel {
            background: white;
            border-radius: 24px;
            padding: 50px 40px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
        }

        .right-panel h2 {
            margin-top: 0;
            font-size: 30px;
            color: #0f172a;
        }

        .subtitle {
            color: #64748b;
            margin-bottom: 25px;
        }

        .form-group {
            margin-bottom: 18px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #334155;
        }

        .form-group input {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid #cbd5e1;
            border-radius: 12px;
            font-size: 14px;
        }

        .btn {
            width: 100%;
            padding: 13px;
            border: none;
            border-radius: 12px;
            background: #0f172a;
            color: white;
            font-size: 15px;
            cursor: pointer;
        }

        .btn:hover {
            background: #1e293b;
        }

        .link-row {
            margin-top: 16px;
            text-align: center;
        }

        .link-row a {
            color: #2563eb;
            text-decoration: none;
        }

        .message {
            padding: 12px 14px;
            border-radius: 10px;
            margin-bottom: 18px;
            font-size: 14px;
        }

        .error {
            background: #fee2e2;
            color: #b91c1c;
        }

        .success {
            background: #dcfce7;
            color: #166534;
        }

        @media (max-width: 900px) {
            .wrapper {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="wrapper">

        <div class="left-panel">
            <h1>Course Recovery System</h1>
            <p>
                A web-based enterprise system for managing academic recovery workflows,
                eligibility checking, academic performance reporting, and notifications.
            </p>

            <ul class="feature-list">
                <li>Authentication and role-based access</li>
                <li>Eligibility and enrolment workflow</li>
                <li>Recovery plan milestone management</li>
                <li>Academic report generation</li>
                <li>Email notification support</li>
            </ul>
        </div>

        <div class="right-panel">
            <h2>Login</h2>
            <p class="subtitle">Sign in using your registered email and password.</p>

            <%
                String errorMessage = (String) request.getAttribute("errorMessage");
                String successMessage = (String) request.getAttribute("successMessage");
            %>

            <% if (errorMessage != null) { %>
                <div class="message error"><%= errorMessage %></div>
            <% } %>

            <% if (successMessage != null) { %>
                <div class="message success"><%= successMessage %></div>
            <% } %>

            <form action="login" method="post">
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" required />
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required />
                </div>

                <button type="submit" class="btn">Login</button>
            </form>

            <div class="link-row">
                <a href="forgotPassword.jsp">Forgot Password?</a>
            </div>
        </div>

    </div>
</div>
</body>
</html>