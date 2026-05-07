# Services Database Schema

> **Project 3 — Pulsa & PLN via Merchant**
>
> Single shared PostgreSQL database `berijalan_db` (port 5432). All services connect to the same DB but logically own their own tables.

---

## 1. Auth Service

### Table: `mst_account`

Owned by: **auth-service**

| Column | Type | Constraints |
|--------|------|-------------|
| `account_id` | UUID | PRIMARY KEY |
| `email` | VARCHAR | UNIQUE, NOT NULL |
| `password` | VARCHAR (BCrypt hashed) | NOT NULL |
| `role` | ENUM(`ADMIN`, `USER`) | DEFAULT `USER` |
| `created_at` | TIMESTAMP | NOT NULL |

**Purpose:** Admin login. Project 3 hanya butuh role `ADMIN` untuk endpoint CRUD merchant. Token JWT berisi `userId`, `email`, `role`.

---

## 2. Merchant Service

### Table: `mst_merchant`

Owned by: **merchant-service**

| Column | Type | Constraints |
|--------|------|-------------|
| `merchant_id` | UUID | PRIMARY KEY |
| `nama_merchant` | VARCHAR | NOT NULL |
| `kode_merchant` | VARCHAR | UNIQUE, NOT NULL |
| `status` | ENUM(`ACTIVE`, `INACTIVE`) | NOT NULL, DEFAULT `ACTIVE` |
| `created_date` | TIMESTAMP | NOT NULL |
| `updated_date` | TIMESTAMP | NOT NULL |

### Table: `mst_merchant_transaction`

Owned by: **merchant-service**

| Column | Type | Constraints |
|--------|------|-------------|
| `transaction_id` | UUID | PRIMARY KEY |
| `merchant_id` | UUID | NOT NULL — ref logis ke `mst_merchant.merchant_id` |
| `product_id` | VARCHAR | NOT NULL — ref ke `DigitalProduct` enum (PULSA_10K, PLN_50K, dll) |
| `nomor_tujuan` | VARCHAR | NOT NULL — nomor HP atau nomor meter PLN |
| `amount` | BIGINT | NOT NULL — harga produk saat transaksi |
| `status` | ENUM(`SUCCESS`, `FAILED`) | NOT NULL |
| `failure_reason` | VARCHAR | NULL jika SUCCESS |
| `transaction_date` | TIMESTAMP | NOT NULL |

**Index:** `merchant_id`, `transaction_date`

### Static — `DigitalProduct` enum (tidak ada di DB)

| Product ID | Name | Type | Price |
|------------|------|------|-------|
| `PULSA_10K` | Pulsa 10.000 | PULSA | 10.000 |
| `PULSA_25K` | Pulsa 25.000 | PULSA | 25.000 |
| `PULSA_50K` | Pulsa 50.000 | PULSA | 50.000 |
| `PLN_20K` | Token PLN 20.000 | PLN | 20.000 |
| `PLN_50K` | Token PLN 50.000 | PLN | 50.000 |
| `PLN_100K` | Token PLN 100.000 | PLN | 100.000 |

---

## 3. Production Service (Mock External Provider)

> **Stateless** — tidak ada DB. Hanya simulasi panggilan ke Telkom (Pulsa) atau PLN (Token).
>
> Endpoint: `POST /external/purchase`. Hasil random SUCCESS/FAILED dengan probabilitas yang dikonfigurasi via `provider.success-rate` (default 0.7).

---

## Redis Cache Keys

Cache disimpan di Redis (port 6379), digunakan oleh **merchant-service**:

| Cache Name | Key Pattern | TTL | Isi |
|------------|-------------|-----|-----|
| `merchant` | `merchant::<merchant_id>` | 300 detik | `MerchantResponse` JSON |
| `merchant_by_code` | `merchant_by_code::<kode>` | 300 detik | `MerchantResponse` JSON |
| `products` | `products::all`, `products::<product_id>` | 3600 detik | `List<ProductResponse>` / `ProductResponse` JSON |

**Invalidate:** Cache `merchant` dan `merchant_by_code` di-evict otomatis saat create/update/delete merchant.

---

## Service Ports

| Service | Port |
|---------|------|
| registry-service (Eureka) | 8761 |
| gateway-service | 8080 |
| auth-service | 8001 |
| production-service | 8003 |
| merchant-service | 8004 |
| postgres | 5432 |
| redis | 6379 |
