# Product Order Management

This is a small app for products and orders. A user can add products to a cart and place an order. An admin can add and update products.

## Setup

You need Java 21 and PostgreSQL.

1. Database `feedbackdb` in PostgreSQL.
2. Open `src/main/resources/application.properties` and set your database username and password.
3. In the project folder, run:

```powershell
.\mvnw.cmd spring-boot:run
```

4. Open `http://localhost:8080/` in your browser. You can register a user there.

## Database

The app uses two schemas:

- `sec.users` has user names, passwords, and roles.
- `jf.products` has the products and how many are in stock.
- `jf.cart` and `jf.cart_items` keep each user's cart. Cart items with `use_ind = 'Y'` are active.
- `jf.orders` and `jf.order_items` keep placed orders.

The database setup is in `src/main/resources/schema.sql`.

## API list

Most API requests need a username and password. A few admin requests need an admin account.

| Method | Path | What it does |
|---|---|---|
| GET | `/api/products` | Show available products |
| GET | `/api/cart` | Show your cart |
| POST | `/api/cart/items/{productId}` | Add a product to your cart |
| PUT | `/api/cart/items/{productId}` | Change the quantity |
| DELETE | `/api/cart/items/{productId}` | Remove a product from your cart |
| POST | `/api/orders` | Place an order |
| GET | `/api/orders` | Show your orders |
| GET | `/api/admin/products` | Show all products (admin) |
| POST | `/api/admin/products` | Add a product (admin) |
| PUT | `/api/admin/products/{id}` | Update a product (admin) |
| PATCH | `/api/admin/products/{id}/enabled` | Turn a product on or off (admin) |

## Try the APIs in Postman

Make a user at `http://localhost:8080/register` first. In Postman, make a new request for each API below. Use `http://localhost:8080` before each path. For example, the products URL is `http://localhost:8080/api/products`.

The API currently asks for HTTP Basic Auth on these requests. In Postman, open the **Authorization** tab, choose **Basic Auth**, and enter your username and password. Admin APIs need an admin account.

### User APIs (run in order)

**1. List products**

- Method: `GET`
- Path: `/api/products`
- Body: none

Use a product ID from the response for `{productId}` below.

**2. Show my cart**

- Method: `GET`
- Path: `/api/cart`
- Body: none

**3. Add a product to my cart**

- Method: `POST`
- Path: `/api/cart/items/{productId}`
- Body type: `raw` → `JSON`

```json
{
  "quantity": 2
}
```

**4. Change the quantity**

- Method: `PUT`
- Path: `/api/cart/items/{productId}`
- Body type: `raw` → `JSON`

```json
{
  "quantity": 3
}
```

**5. Place the order**

- Method: `POST`
- Path: `/api/orders`
- Body: none

**6. Show my orders**

- Method: `GET`
- Path: `/api/orders`
- Body: none

**7. Remove a product from the cart**

- Method: `DELETE`
- Path: `/api/cart/items/{productId}`
- Body: none

### Admin APIs

Use Basic Auth with an admin account for these requests.

**8. List all products**

- Method: `GET`
- Path: `/api/admin/products`
- Body: none

**9. Add a product**

- Method: `POST`
- Path: `/api/admin/products`
- Body type: `raw` → `JSON`

```json
{
  "name": "Notebook",
  "description": "A ruled notebook",
  "price": 4.5,
  "quantity": 20
}
```

**10. Update a product**

- Method: `PUT`
- Path: `/api/admin/products/{id}`
- Body type: `raw` → `JSON`

```json
{
  "price": 5.0,
  "quantity": 15
}
```

**11. Turn a product on or off**

- Method: `PATCH`
- Path: `/api/admin/products/{id}/enabled`
- Body type: `raw` → `JSON`

```json
{
  "enabled": false
}
```

Run the tests with:

```powershell
.\mvnw.cmd test
```
