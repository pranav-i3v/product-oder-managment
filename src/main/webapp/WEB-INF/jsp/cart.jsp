<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><title>Your Cart</title><%@ include file="store-style.jspf" %></head>
<body>
<%@ include file="store-nav.jspf" %>
<main>
    <h2>Your cart</h2>
    <c:if test="${not empty message}"><p class="message"><c:out value="${message}"/></p></c:if>
    <c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>
    <c:forEach items="${cart.items}" var="item">
        <section class="box row">
            <div>
                <strong><c:out value="${item.name}"/></strong>
                <p>$<c:out value="${item.price}"/> each · line total $<c:out value="${item.lineTotal}"/></p>
            </div>
            <form action="/cart/update" method="post">
                <input type="hidden" name="productId" value="${item.productId}">
                <label>Quantity <input type="number" name="quantity" value="${item.quantity}" min="1" required></label>
                <button type="submit">Update</button>
            </form>
            <form action="/cart/remove" method="post">
                <input type="hidden" name="productId" value="${item.productId}">
                <button type="submit">Remove</button>
            </form>
        </section>
    </c:forEach>
    <c:if test="${empty cart.items}">
        <p class="muted">Your cart is empty. <a href="/products">Browse products</a></p>
    </c:if>
    <div class="row">
        <strong>Total: $<c:out value="${cart.total}"/></strong>
        <c:if test="${not empty cart.items}">
            <form action="/orders/place" method="post">
                <button type="submit">Place order</button>
            </form>
        </c:if>
    </div>
</main>
</body>
</html>
