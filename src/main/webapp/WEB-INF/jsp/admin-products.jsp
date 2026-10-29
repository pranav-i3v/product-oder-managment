<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Products</title>
    <%@ include file="store-style.jspf" %>
</head>
<body>
<%@ include file="store-nav.jspf" %>
<main>
    <h2>Manage products</h2>
    <c:if test="${not empty message}">
        <p class="message"> <c:out value="${message}"/> </p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error"> <c:out value="${error}"/> </p>
    </c:if>
    <section class="box">
        <h3>Add product</h3>
        <form action="/admin/products" method="post">
            <label class="form-label">Name <input maxlength="255" name="name" required></label>
            <label class="form-label">Description <input maxlength="255" name="description"></label>
            <label class="form-label">Price <input min="0" name="price" required step="0.01" type="number"></label>
            <label class="form-label">Quantity <input min="0" name="quantity" required type="number"></label>
            <button type="submit">Add product</button>
        </form>
    </section>
    <h3>Existing products</h3>
    <c:forEach items="${products}" var="product">
        <section class="box">
            <div class="row">
                <strong>
                    <c:out value="${product.name}"/>
                </strong>
                <span><c:out value="${product.enabled ? 'Enabled' : 'Disabled'}"/></span>
            </div>
            <div class="form-label"><span>Price</span><span><c:out value="${product.price}"/></span></div>
            <div class="form-label"><span>Quantity</span><span><c:out value="${product.quantity}"/></span></div>
            <div class="row product-actions">
                <form action="/admin/products/${product.id}/enabled" method="post">
                    <input name="enabled" type="hidden" value="${!product.enabled}">
                    <button type="submit">
                        <c:out value="${product.enabled ? 'Disable' : 'Enable'}"/>
                    </button>
                </form>
                <details>
                    <summary>Edit</summary>
                    <form action="/admin/products/${product.id}/update" method="post">
                        <label class="form-label">Price <input min="0" name="price" required step="0.01" type="number"
                                            value="${product.price}"></label>
                        <label class="form-label">Quantity <input min="0" name="quantity" required type="number"
                                               value="${product.quantity}"></label>
                        <button type="submit">Update product</button>
                    </form>
                </details>
            </div>
        </section>
    </c:forEach>
</main>
</body>
</html>
