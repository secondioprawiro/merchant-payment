# Architectural Reasoning

Penjelasan keputusan desain di balik struktur microservices ini.

---

## 1. Kenapa `accounts` dan `profiles` Dipisah?

### Prinsip

Setiap service punya **single responsibility** di level bisnis:

| Service | Tanggung Jawab |
|---------|----------------|
| Auth Service | **Siapa kamu?** — identity, credential, autentikasi |
| User & Wallet Service | **Info & uang kamu** — profil, saldo, mutasi |
| Production Service | **Apa yang dijual?** — katalog produk (external) |
| Transaction Service | **Apa yang dibeli?** — transaksi, pricing, serial number |

### Alasan Pemisahan

- Auth Service tidak peduli nama atau nomor HP user — hanya credential
- User Service tidak peduli password — hanya data personal
- Auth bisa diganti OAuth/SSO tanpa sentuh User Service
- Masing-masing bisa di-scale dan di-deploy secara independen

---

## 2. Konsistensi Data Antar Service

`profiles.user_id` = `accounts.account_id` — nilai sama, tapi **tidak ada FK cross-database**. Konsistensi dijaga lewat event:

```
Client → POST /register
           │
           ▼
       Auth Service
       INSERT accounts (account_id, email, password, role)
           │
           │ publish: AccountCreated { account_id, email }
           ▼
       Message Broker (RabbitMQ / Kafka)
           │
           ├──► User Service  → INSERT profiles (user_id = account_id)
           └──► Wallet Service → INSERT wallets (user_id = account_id, balance = 0)
```

> **Tradeoff:** Eventual consistency — ada jeda kecil antara account dibuat dan profile tersedia. Acceptable untuk kebanyakan use case.

---

## 3. Aturan: No Cross-Service Foreign Key

| Pendekatan | Verdict |
|------------|---------|
| FK cross-database | ❌ Tight coupling, DB-level dependency |
| Shared database | ❌ Defeating the purpose of microservices |
| Event-driven sync | ✅ Loose coupling, eventual consistency |
| API call saat butuh data | ✅ OK untuk read, hati-hati latency |

---

## 4. Kenapa `product_prices` Ada di Transaction Service?

### Masalah

Production Service adalah **external/third-party** — tidak bisa tambah kolom `selling_price` atau `admin_fee` di database mereka.

### Solusi: Anti-Corruption Layer

Buat tabel `product_prices` di Transaction Service DB sebagai pricing layer internal:

```
Client
  │
  ▼
Transaction Service
  ├── GET /products/{id}  →  Production Service (ambil base_price)
  ├── SELECT selling_price FROM product_prices WHERE product_id = ?
  └── final_price = selling_price (sudah include admin_fee)
```

### Kenapa Bukan di Tempat Lain?

| Opsi | Masalah |
|------|---------|
| Hardcode di code | Ganti fee = redeploy |
| Di API Gateway | Gateway bukan tempat business logic |
| Bikin Pricing Service tersendiri | Over-engineering untuk scope ini |
| Di Transaction Service DB | ✅ Pricing adalah bagian dari domain transaksi |

### Kapan Bikin Pricing Service Tersendiri?

Jika ada multiple markup rule (per user tier, per region, promo), ada tim tersendiri yang manage pricing, atau scale sangat besar. Untuk scope bootcamp ini, tabel `product_prices` di Transaction Service sudah cukup.

---

## 5. Kenapa Tidak Ada Refund Otomatis?

### Alasan

Produk yang dijual (pulsa, token PLN) bersifat **irreversible** — begitu serial number / token ter-deliver ke user, produk sudah "terpakai" di sisi provider eksternal. Tidak ada mekanisme untuk "menarik kembali" token yang sudah dikeluarkan.

### Alur Jika Transaksi Gagal

```
Transaction Service
  │
  ├── Status: pending → processing
  │
  ├── [Gagal di provider] → status: failed
  │         │
  │         ▼
  │   publish: TransactionFailed { transaction_id, user_id, amount }
  │         │
  │         ▼
  │   Wallet Service consume → INSERT wallet_mutations (type: credit, desc: "Refund - gagal")
  │                          → UPDATE wallets SET balance = balance + amount
  │
  └── [Sukses] → status: success, sn diisi → tidak ada refund
```

> Refund hanya terjadi otomatis jika transaksi **gagal sebelum produk di-deliver**. Jika produk sudah di-deliver (sn/token terisi), refund harus manual via admin.

---

## 6. Kafka sebagai Message Broker

### Kenapa Kafka?

| Alasan | Penjelasan |
|--------|------------|
| **Decoupling** | Auth Service tidak perlu tahu User Service & Wallet Service ada — cukup publish event |
| **Async** | Register tidak perlu tunggu profile & wallet terbuat — response lebih cepat |
| **Reliability** | Event tersimpan di Kafka, tidak hilang meski consumer (User/Wallet Service) sempat down |
| **Scalability** | Consumer bisa di-scale independen untuk consume event lebih cepat |

### Event & Service yang Terlibat

| Event | Publisher | Consumer | Aksi |
|-------|-----------|----------|------|
| `AccountCreated` | Auth Service | User & Wallet Service | Buat `profiles` + `wallets` |
| `TransactionCompleted` | Transaction Service | Wallet Service | Deduct balance + catat `wallet_mutations` (debit) |
| `TransactionFailed` | Transaction Service | Wallet Service | Kembalikan balance + catat `wallet_mutations` (credit) |

### Alur Lengkap: Register

```
Client → POST /auth/register
              │
              ▼
         Auth Service
         INSERT accounts
              │
              │──── publish ──────► Kafka topic: account.created
                                         │
                         ┌───────────────┘
                         │
              ┌──────────▼──────────┐
              │                     │
         User Service          Wallet Service
         INSERT profiles        INSERT wallets
         (user_id = account_id) (balance = 0)
```

### Alur Lengkap: Transaksi

```
Client → POST /transactions
              │
              ▼
         Transaction Service
         1. GET product dari Production Service (sync HTTP)
         2. Deduct wallet via Wallet Service (sync HTTP)
         3. Call provider eksternal
         4. INSERT transactions (status: success/failed)
              │
              │──── publish ──────► Kafka topic: transaction.completed / transaction.failed
                                         │
                                    Wallet Service
                                    INSERT wallet_mutations
                                    (catat debit/refund credit)
```
