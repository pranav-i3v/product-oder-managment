<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><title>Products</title><%@ include file="store-style.jspf" %></head>
<body>
<%@ include file="store-nav.jspf" %>
<main>
    <h2>Products</h2>
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
    <div class="grid">
        <c:forEach items="${products}" var="product">
            <section class="card">
                <h3>
                    <c:out value="${product.name}"/>
                </h3>
                <p class="muted">
                    <c:out value="${product.description}"/>
                </p>
                <p>$
                    <c:out value="${product.price}"/>
                    ·
                    <c:out value="${product.quantity}"/>
                    in stock
                </p>
                <c:if test="${product.quantity gt 0}">
                    <form action="/cart/add" method="post">
                        <input name="productId" type="hidden" value="${product.id}">
                        <label>Quantity
                            <input max="${product.quantity}" min="1" name="quantity" required type="number" value="1">
                        </label>
                        <button type="submit">Add to cart</button>
                    </form>
                </c:if>
                <c:if test="${product.quantity eq 0}"><p class="muted">Out of stock</p></c:if>
            </section>
        </c:forEach>
    </div>
    <c:if test="${empty products}"><p class="muted">No products are currently available.</p></c:if>
</main>
</body>
</html>
