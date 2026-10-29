<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
    <style>
        body {
            font-family: sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .box {
            width: 270px;
            background: #f7f7f7;
            border: 1px solid #403f3f;
            padding: 30px;
            text-align: center;
        }

        h2 {
            margin-top: 10px;
        }

        input {
            width: 100%;
            padding: 8px;
            margin: 8px 0;
            box-sizing: border-box;
            border: 1px solid #999;
            border-radius: 3px;
        }

        button {
            width: 100%;
            padding: 8px;
            background: #ddd;
            border: 1px solid #999;
            border-radius: 3px;
            cursor: pointer;
        }

        p, .error {
            font-size: 12px;
        }

        .error {
            color: red;
        }
    </style>
</head>
<body>
<div class="box">
    <h2>Register</h2>
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>
    <form action="/register" method="post">
        <input name="username" placeholder="Username" required type="text">
        <input name="email" placeholder="Email" type="email">
        <input minlength="8" name="password" placeholder="Password (8 characters minimum)" required type="password">
        <input name="confirmPassword" placeholder="Confirm Password" required type="password">
        <button type="submit">Sign Up</button>
    </form>
    <p>Already have an account? <a href="/login">Log In</a></p>
</div>
</body>
</html>
