<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Forgot Password</title>
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

        .card {
            width: 100%;
            max-width: 500px;
            background: white;
            border-radius: 24px;
            padding: 40px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
        }

        h2 {
            margin-top: 0;
            color: #0f172a;
        }

        p {
            color: #64748b;
            margin-bottom: 24px;
            line-height: 1.6;
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
    </style>
</head>
<body>
<div class="container">
    <div class="card">
        <h2>Forgot Password</h2>
        <p>Enter your registered email address. An OTP will be sent to your email for password reset.</p>

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

        <form action="forgot-password" method="post">
            <div class="form-group">
                <label for="email">Registered Email Address</label>
                <input type="email" id="email" name="email" required />
            </div>

            <button type="submit" class="btn">Send OTP</button>
        </form>

        <div class="link-row">
            <a href="login.jsp">Back to Login</a>
        </div>
    </div>
</div>
</body>
</html>