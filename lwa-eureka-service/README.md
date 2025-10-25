# 🌐 LWA Eureka Service

## Overview
The **Eureka Service** acts as the service discovery server for the LWA microservices system.

All other services (Product and Order) register themselves here.

---

## 🏃‍♂️ Run the Service
```bash
mvn spring-boot:run
```

Then open: [http://localhost:8761](http://localhost:8761)

You should see:
- `LWA-PRODUCT-SERVICE`
- `LWA-ORDER-SERVICE`

Both with `status = UP`. 

NOTED: You need to run the service and will see the status
