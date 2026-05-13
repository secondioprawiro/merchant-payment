# Merchant Payment System

> Sistem pembayaran digital untuk pembelian **pulsa ponsel** dan **token listrik PLN** berbasis arsitektur **microservices**.

---

## Deskripsi Proyek

Merchant Payment adalah platform yang mensimulasikan ekosistem pembayaran digital. Platform ini memungkinkan merchant untuk melayani pembelian pulsa dan token PLN kepada pelanggan. Sistem ini dibangun dengan arsitektur microservices menggunakan Spring Boot dan dilengkapi frontend Angular.

External system (product-service) mensimulasikan provider pulsa/PLN dan menghasilkan response **SUCCESS** atau **FAILED** secara acak sesuai probabilitas yang dikonfigurasi.

### Analogi Sistem

```
Customer → [Frontend Angular] → [Gateway :8762] → [Merchant Service :8004]
                                                        ↓
                                               [Product Service :8003]
                                             (simulasi Telkomsel / PLN)
```

- **Admin** mengelola data merchant melalui dashboard.
- **Merchant** melakukan transaksi pembelian produk digital (pulsa/PLN) atas nama pelanggan.
- **Product Service** berperan sebagai external provider yang memproses transaksi.

---

## Fitur Utama

| Fitur | Keterangan |
|-------|-----------|
| 🔐 Autentikasi JWT | Register & login akun dengan role `ADMIN` / `USER` |
| 🏪 Manajemen Merchant | CRUD merchant dengan kode unik, status ACTIVE/INACTIVE |
| 📦 Katalog Produk | Daftar produk pulsa & token PLN (enum statis di product-service) |
| 💳 Transaksi Pembelian | Beli pulsa/token dengan deduct saldo, simulasi hasil SUCCESS/FAILED |
| 📜 Riwayat Transaksi | Histori transaksi dengan filter tanggal & paginasi |
| 📊 Dashboard Statistik | Stats admin (global) & stats merchant (per merchant) |
| ⚡ Redis Caching | Cache data merchant & produk untuk performa optimal |

---

## Tech Stack

| Layer | Teknologi |
|-------|-----------|
| **Backend** | Java 21, Spring Boot 3.x |
| **Frontend** | Angular 18 (SSR dengan Angular Universal) |
| **Database** | PostgreSQL 16 |
| **Cache** | Redis 7 + RedisInsight |
| **Service Discovery** | Netflix Eureka |
| **API Gateway** | Spring Cloud Gateway |
| **Inter-service Comm.** | OpenFeign |
| **Security** | Spring Security + JWT (JJWT) |
| **Build Tools** | Maven (multi-module) |
| **Containerization** | Docker & Docker Compose |

---

## Arsitektur Microservices

```
FINAL/
├── be-merchant/                  # Backend (Maven multi-module)
│   ├── registry-service/         # Eureka Server → port 8761
│   ├── gateway-service/          # API Gateway → port 8762
│   ├── auth-service/             # Autentikasi & JWT → port 8001
│   ├── product-service/          # Mock External Provider → port 8003
│   ├── merchant-service/         # Core Business Logic → port 8004
│   └── docker-compose.yml        # PostgreSQL + Redis + RedisInsight
│
└── fe-merchant/                  # Frontend Angular 18
    └── src/app/features/
        ├── auth/                 # Login & Register
        ├── dashboard/            # Halaman utama merchant
        ├── products/             # Katalog produk
        ├── riwayat-transaksi/    # Histori transaksi
        ├── profil/               # Profil merchant
        ├── admin/                # Panel admin
        └── modal/                # Komponen modal
```

---

## Database Schema

Single PostgreSQL database `berijalan_db` digunakan bersama, tiap service memiliki tabelnya sendiri secara logis.

### Auth Service — `mst_account`

| Kolom | Tipe | Constraint |
|-------|------|------------|
| `account_id` | UUID | PRIMARY KEY |
| `email` | VARCHAR | UNIQUE, NOT NULL |
| `password` | VARCHAR (BCrypt) | NOT NULL |
| `role` | ENUM(`ADMIN`, `USER`) | DEFAULT `USER` |
| `created_at` | TIMESTAMP | NOT NULL |

### Merchant Service — `mst_merchant`

| Kolom | Tipe | Constraint |
|-------|------|------------|
| `merchant_id` | UUID | PRIMARY KEY |
| `nama_merchant` | VARCHAR | NOT NULL |
| `kode_merchant` | VARCHAR | UNIQUE, NOT NULL |
| `status` | ENUM(`ACTIVE`, `INACTIVE`) | DEFAULT `ACTIVE` |
| `created_date` | TIMESTAMP | NOT NULL |
| `updated_date` | TIMESTAMP | NOT NULL |

### Merchant Service — `mst_merchant_transaction`

| Kolom | Tipe | Constraint |
|-------|------|------------|
| `transaction_id` | UUID | PRIMARY KEY |
| `merchant_id` | UUID | NOT NULL |
| `product_id` | VARCHAR | NOT NULL (ref ke `DigitalProduct` enum) |
| `nomor_tujuan` | VARCHAR | NOT NULL (nomor HP / nomor meter PLN) |
| `amount` | BIGINT | NOT NULL |
| `status` | ENUM(`SUCCESS`, `FAILED`) | NOT NULL |
| `failure_reason` | VARCHAR | NULL jika SUCCESS |
| `transaction_date` | TIMESTAMP | NOT NULL |

### Product Service — Stateless (tidak ada DB)

Produk didefinisikan sebagai enum `DigitalProduct`:

| Product ID | Nama | Tipe | Harga |
|------------|------|------|-------|
| `PULSA_10K` | Pulsa 10.000 | PULSA | 10.000 |
| `PULSA_25K` | Pulsa 25.000 | PULSA | 25.000 |
| `PULSA_50K` | Pulsa 50.000 | PULSA | 50.000 |
| `PLN_20K` | Token PLN 20.000 | PLN | 20.000 |
| `PLN_50K` | Token PLN 50.000 | PLN | 50.000 |
| `PLN_100K` | Token PLN 100.000 | PLN | 100.000 |

---

## Redis Cache

Cache digunakan oleh **merchant-service** (port Redis: `6379`):

| Cache | Key Pattern | TTL |
|-------|-------------|-----|
| Merchant by ID | `merchant::<merchant_id>` | 300 detik |
| Merchant by Kode | `merchant_by_code::<kode>` | 300 detik |
| Semua Produk | `products::all` | 3600 detik |
| Produk by ID | `products::<product_id>` | 3600 detik |

Cache `merchant` dan `merchant_by_code` di-evict otomatis saat create/update/delete merchant.

---

## Port & Akses

| Service | Port | Keterangan |
|---------|------|-----------|
| Registry (Eureka) | `8761` | Service discovery dashboard |
| Gateway | `8762` | Entry point semua request API |
| Auth Service | `8001` | Autentikasi & manajemen akun |
| Product Service | `8003` | Mock external provider pulsa/PLN |
| Merchant Service | `8004` | Core business logic |
| PostgreSQL | `5432` | Database utama |
| Redis | `6379` | Caching |
| RedisInsight | `5540` | GUI monitoring Redis |
| Frontend Angular | `4200` | Web UI (development) |

> **Semua request API harus melalui Gateway:** `http://localhost:8762`

---

## Cara Menjalankan Lokal

### Prasyarat

- Java 21
- Maven 3.x
- Docker & Docker Compose
- Node.js 18+ & Angular CLI (untuk frontend)

### 1. Clone & Masuk ke Direktori Backend

```bash
cd be-merchant
```

### 2. Jalankan Infrastruktur (PostgreSQL + Redis + RedisInsight)

```bash
docker-compose up -d
```

Ini akan menjalankan:
- PostgreSQL 16 → `localhost:5432`, database `berijalan_db`
- Redis 7 → `localhost:6379`
- RedisInsight → `http://localhost:5540`

### 3. Build Semua Service

```bash
./mvnw clean package -DskipTests
```

### 4. Jalankan Service (urutan penting)

> Buka terminal terpisah untuk setiap service.

**Terminal 1 — Registry Service (Eureka)**
```bash
cd registry-service
./mvnw spring-boot:run
```
Tunggu hingga Eureka dashboard aktif di `http://localhost:8761`

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

**Terminal 4 — Product Service** *(mock external provider)*
```bash
cd product-service
./mvnw spring-boot:run
```

**Terminal 5 — Merchant Service**
```bash
cd merchant-service
./mvnw spring-boot:run
```

### 5. Jalankan Frontend

```bash
cd ../fe-merchant
npm install
ng serve
```

Frontend akan berjalan di `http://localhost:4200`

---

## API Endpoints Ringkas

Semua request melalui Gateway `http://localhost:8762`. Endpoint yang membutuhkan autentikasi harus menyertakan header:
```
Authorization: Bearer <token>
```

### Auth Service
| Method | Endpoint | Keterangan | Auth |
|--------|----------|-----------|------|
| POST | `/auth/register` | Registrasi akun baru | ❌ |
| POST | `/auth/login` | Login & dapatkan JWT | ❌ |

### Merchant Service
| Method | Endpoint | Keterangan | Role |
|--------|----------|-----------|------|
| GET | `/merchant` | List semua merchant | ADMIN |
| GET | `/merchant/me` | Detail merchant sendiri | USER |
| GET | `/merchant/{merchantId}` | Detail merchant by ID | ADMIN |
| PUT | `/merchant/{merchantId}` | Update merchant | ADMIN/Owner |
| DELETE | `/merchant/{merchantId}` | Hapus merchant | ADMIN/Owner |
| POST | `/transaction` | Beli produk (pulsa/PLN) | USER |
| GET | `/transaction` | Riwayat transaksi (filter: tanggal, paginasi) | USER/ADMIN |
| GET | `/transaction/{transactionId}` | Detail transaksi | USER/ADMIN |
| GET | `/stats/admin` | Statistik global | ADMIN |
| GET | `/stats/merchant` | Statistik per merchant | USER/ADMIN |

### Product Service
| Method | Endpoint | Keterangan | Auth |
|--------|----------|-----------|------|
| GET | `/product` | List semua produk (filter by `type`) | ❌ |
| GET | `/product/{productId}` | Detail produk | ❌ |
| POST | `/product/transaction` | Proses transaksi ke provider (internal) | ❌ |

---

## Disclaimer

> Proyek ini adalah **simulasi / pembelajaran** dan tidak terhubung dengan provider pulsa atau PLN sesungguhnya. Seluruh transaksi bersifat fiktif dan hanya untuk keperluan edukasi dalam program **Berijalan Bootcamp**.
