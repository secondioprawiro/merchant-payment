# Merchant Payment

### Deskripsi Projek
Merchant Payment adalah sistem pembayaran digital untuk pembelian pulsa ponsel dan token listrik PLN. Sistem ini dirancang dengan arsitektur microservices dan mensimulasikan interaksi dengan external system (provider pulsa/PLN) yang dapat menghasilkan response PENDING, SUCCESS, atau FAILED. 

### Fitur Utama
- Pembelian Pulsa
- Pembelian Token PLN
- Transaction History
- External System Simulation
- Failure Handling

### Tech Stack
- Backend = Java 21 & Spring Boot 
- Frontend = Angular 18
- Database = PostgreSQL
- Build Tools = Maven
- Orchestration = Docker

### Disclaimer
Proyek ini adalah simulasi / pembelajaran dan tidak terhubung dengan provider pulsa/PLN sesungguhnya.

---

## Cara Menjalankan Lokal

### Prasyarat
- Java 21
- Maven
- Docker & Docker Compose

### 1. Jalankan Infrastruktur (PostgreSQL + Redis)
```bash
docker-compose up -d
```

### 2. Build Semua Service
```bash
./mvnw clean package -DskipTests
```

### 3. Jalankan Service (urutan penting, buka terminal terpisah)

**Terminal 1 — Registry Service (Eureka)**
```bash
cd registry-service
./mvnw spring-boot:run
```

**Terminal 2 — Gateway Service**
```bash
cd gateway-service
./mvnw spring-boot:run
```

**Terminal 3 — Auth Service**
```bash
cd auth-service
./mvnw spring-boot:run
```

**Terminal 4 — Merchant Service**
```bash
cd merchant-service
./mvnw spring-boot:run
```

**Terminal 5 — Product Service**
```bash
cd product-service
./mvnw spring-boot:run
```

### Port & Akses

| Service          | Port |
|------------------|------|
| Registry (Eureka)| 8761 |
| Gateway          | 8762 |
| Auth Service     | 8001 |
| Product Service  | 8003 |
| Merchant Service | 8004 |

- Eureka dashboard: `http://localhost:8761`
- Semua request melalui gateway: `http://localhost:8762`




