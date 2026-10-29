<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><title>Your Orders</title><%@ include file="store-style.jspf" %></head>
<body>
<%@ include file="store-nav.jspf" %>
<main>
    <h2>Your orders</h2>
    <c:if test="${not empty message}">
        <p class="message">
            <c:out value="${message}"/>
        </p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error">
            <c:out value="${error}"/>
        </p>
    </c:if>
    <c:forEach items="${orders}" var="order">
        <section class="box">
            <div class="row">
                <strong>Order #
                    <c:out value="${order.id}"/>
                </strong>
                <span><c:out value="${order.status}"/> · $<c:out value="${order.totalPrice}"/></span>
            </div>
            <ul>
                <c:forEach items="${order.items}" var="item">
                    <li>
                        <c:out value="${item.productName}"/>
                        ×
                        <c:out value="${item.quantity}"/>
                        — $
                        <c:out value="${item.lineTotal}"/>
                    </li>
                </c:forEach>
            </ul>
        </section>
    </c:forEach>
    <c:if test="${empty orders}"><p class="muted">You have not placed any orders yet.</p></c:if>
</main>
</body>
</html>
