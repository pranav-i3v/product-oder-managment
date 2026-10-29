<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <style>
        body  {
            font-family: sans-serif;
            background: #fff;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .box  {
            width: 270px;
            background: #f7f7f7;
            border: 1px solid #403f3f;
            padding: 30px;
            text-align: center;
        }

        h2  {
            margin-top: 10px;
        }

        input  {
            width: 100%;
            padding: 8px;
            margin: 8px 0;
            box-sizing: border-box;
        }

        button  {
            width: 100%;
            padding: 8px;
            background: #ddd;
            border: 1px solid #999;
            border-radius: 3px;
            cursor: pointer;
        }

        p, .error  {
            font-size: 12px;
        }

        .error  {
            color: red;
        }
    </style>
</head>
<body>
<div class="box">
    <h2>Login</h2>
    <c:if test="${not empty error}"><div class="error">${error}</div></c:if>
    <form action="/login" method="post">
        <input type="text" name="username" placeholder="Username" required>
        <input type="password" name="password" placeholder="Password" required>
        <button type="submit">Log In</button>
    </form>
    <p>Don't have an account? <a href="/register">Sign Up</a></p>
</div>
</body>
</html>
