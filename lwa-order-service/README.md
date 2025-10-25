# 🧾 LWA Order Service

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
The **Order Service** manages order creation and processing.  
It communicates with the Product Service via Feign to:
- Fetch variant info
- Reduce stock atomically
- Ensure retry/circuit-breaker protection

---

## 🏃‍♂️ Run the Service
```bash
mvn spring-boot:run
```

Swagger Docs: [http://localhost:8081/order/swagger-ui.html](http://localhost:8081/order/swagger-ui/index.html)

---

## 🧪 Run Tests
```bash
mvn test
```

This will run:
- `OrderServiceTest`
- Feign communication mocks

---

## 🛒 Example Order Creation

```bash
curl -X 'POST' \
  'http://localhost:8081/order/orders' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "customerName": "jafar",
  "orderItems": [
    {
      "variantId": 1,
      "quantity": 2
    }
  ]
}'
```
