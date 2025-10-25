# 🛍️ LWA Product Service

## Prerequisites
- **Java 17** (JDK 17)
- **Maven 3.9+**
- Running **Eureka Service** for service discovery

> Make sure `JAVA_HOME` points to a Java 17 installation:
>
> ```bash
> java -version
> ```

> Example to set `JAVA_HOME`:
>
> ```bash
> export JAVA_HOME=/Users/youruser/Library/Java/JavaVirtualMachines/corretto-17.0.15/Contents/Home
> ```
>  ```bash
> export PATH=$JAVA_HOME/bin:$PATH
> ```

## Overview
The **Product Service** manages items and variants (color, size, price, stock).  
It is registered with Eureka and communicates with the Order Service via Feign.

---


---

## 🏃‍♂️ Run the Service
```bash
mvn spring-boot:run
```

Swagger Docs: [http://localhost:8082/product/swagger-ui.html](http://localhost:8082/swagger-ui.html)

---

## 🧪 Run Tests
```bash
mvn test
```

This will execute all JUnit tests including:
- `VariantServiceTest`
- Repository tests

---

## 🧾 Example API Usage

### ➕ Create Item
```bash
curl -X POST "http://localhost:8082/product/items" -H "Content-Type: application/json" -d '{
  "name": "T-Shirt",
  "description": "Premium cotton T-shirt with multiple colors and sizes",
  "variants": [
    {"color": "Black", "size": "M", "price": 150000, "stock": 25}
  ]
}'
```

### 🔍 Get All Variants
```bash
curl -X GET "http://localhost:8082/product/variants"
```

### 📉 Reduce Stock
```bash
curl -X 'PUT' \
  'http://localhost:8082/product/variants/1/reduce-stock?quantity=2' \
  -H 'accept: */*'
```
