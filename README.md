# 🛍️ LWA Microservices Project

This project implements a **Microservices Architecture** for a simple shopping system with product and order management.

---

## 🏗️ Architecture Overview

- **Eureka Server** — Service Discovery
- **Product Service** — Manages items and variants
- **Order Service** — Handles order creation and stock synchronization

---

## ⚙️ Tech Stack

| Component | Technology |
|------------|-------------|
| Language | Java 17 |
| Framework | Spring Boot, Spring Cloud (Eureka, Feign, Resilience4j) |
| Fault Tolerance | Circuit Breaker, Retry, Fallback |
| Concurrency Control | Optimistic Atomic Lock (stock/order updates) |
| Database | H2 (file-based) |
| API Docs | Swagger / OpenAPI |

---

## 🚀 How to Run

### 0️⃣ Set up H2 Database Directory

Edit the following line in **application.properties** (for order and product services):

```properties
spring.datasource.url=jdbc:h2:file:/Users/jafarshodiq/Documents/database-h2/mydb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1
```

👉 Replace `/Users/jafarshodiq/Documents/database-h2/` with your preferred path.

---

### 1️⃣ Start Services

Run these in order:

1. **Eureka Server**
   ```bash
   ./mvnw spring-boot:run -pl lwa-eureka-service
   ```
2. **Product Service**
   ```bash
   ./mvnw spring-boot:run -pl lwa-product-service
   ```
3. **Order Service**
   ```bash
   ./mvnw spring-boot:run -pl lwa-order-service
   ```

---

## 🛒 How to Use

### Step 1. Create Items
POST `/items`  
Creates a new item with one or more variants.

### Step 2. Create Order
POST `/orders`  
Places an order for one or more product variants.

### Step 3. Complete Order
PATCH `/orders/{id}/complete`  
Completes an order and adjusts stock levels.

---

## 🧠 Notes

- Eureka handles automatic service registration and discovery.
- Feign clients use logical service names (e.g., `lwa-product-service`) instead of URLs.
- Stock updates use optimistic locking to prevent race conditions.
- Swagger UI available at `/swagger-ui.html` for each service.

---

## 📂 Services Overview

| Service | Port | Description |
|----------|------|-------------|
| **Eureka Service** | `8761` | Service Discovery Server |
| **Product Service** | `8082` | Manages products and variants |
| **Order Service** | `8081` | Handles orders and stock updates |

---

## 🧪 Testing

Unit tests are provided for:
- Service layer (`VariantService`, `OrderService`)
- Repository layer (in-memory H2)

Run tests:
```bash
./mvnw test
```

---

## 📜 License

This project is for educational and demonstration purposes only.
