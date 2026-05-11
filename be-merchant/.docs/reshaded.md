# RESHADED — System Design Document

> Project: **Berijalan Bootcamp — PPOB / Digital Product Platform**

---

## R — Requirements

### Functional Requirements

#### User
- [ ] Register & login dengan email + password
- [ ] Lihat & update profil (nama, nomor HP)
- [ ] Lihat saldo & riwayat mutasi wallet
- [ ] Browse produk by kategori
- [ ] Beli produk (pulsa, token PLN, dll) menggunakan saldo wallet
- [ ] Lihat riwayat transaksi pribadi
- [ ] Lihat detail transaksi (termasuk serial number / token jika sukses)

#### Admin
- [ ] Login — role ditentukan sistem dari DB (`accounts.role`), bukan pilihan user saat login
- [ ] CRUD Merchant *(scope TBD)*
- [ ] CRUD Produk — set selling price & admin fee per produk
- [ ] Lihat semua riwayat transaksi (semua user)
- [ ] Dashboard statistik: total transaksi, revenue, user aktif

### Non-Functional Requirements

| Aspek | Requirement |
|-------|-------------|
| **Availability** | 99.9% uptime untuk flow transaksi — setara ~43 menit downtime/bulan. Standar untuk sistem yang menangani transaksi finansial; downtime = potensi kehilangan transaksi & kepercayaan user |
| **Consistency** | Wallet deduct & transaksi harus atomic — tidak boleh deduct tanpa transaksi tercatat |
| **Security** | Password di-hash (bcrypt), auth via JWT, admin endpoint terlindungi role check |
| **Scalability** | Setiap service bisa ditambah instance-nya secara independen saat load tinggi. Contoh: Transaction Service kelebihan beban saat peak → tambah instance Transaction Service saja, tanpa perlu scale Auth atau User Service |
| **Auditability** | Semua mutasi wallet tersimpan di tabel `wallet_mutations` dengan field `type` (credit/debit) dan `desc` (contoh: `"Pembelian Pulsa 15.000"`). Bisa diakses user via `GET /wallets/mutations` |

### Out of Scope
- Payment gateway eksternal (top up diasumsikan manual / mock)
- Notifikasi (email/SMS/push)
- Refund otomatis — lihat [`reasoning.md`](./reasoning.md) untuk alasan & alur

---

## E — Estimation

> *(Dikosongkan)*

---

## S — Storage Schema / Design

> Referensi lengkap: [`services-db.md`](./services-db.md)

### Storage per Service

| Service | Database | Keterangan |
|---------|----------|------------|
| Auth Service | PostgreSQL | Isolated DB — hanya credential & role |
| User & Wallet Service | PostgreSQL | Isolated DB — profile, wallet, mutasi |
| Production Service | PostgreSQL | External/third-party — read-only via API |
| Transaction Service | PostgreSQL | Isolated DB — pricing layer + transaksi |

### Tabel per Service

| Service | Tabel |
|---------|-------|
| Auth Service | `accounts` |
| User & Wallet Service | `profiles`, `wallets`, `wallet_mutations` |
| Production Service *(external)* | `categories`, `products` |
| Transaction Service | `product_prices`, `transactions` |

### Prinsip
- Setiap service punya database sendiri — no shared DB
- Tidak ada FK cross-database — referensi antar service via ID saja
- Konsistensi dijaga via **event** — pesan yang di-publish ke Kafka saat sesuatu terjadi (contoh: `AccountCreated`), lalu di-consume service lain untuk sync data. Bukan lewat FK atau DB constraint. Lihat [`reasoning.md`](./reasoning.md) untuk detail Kafka & alur event

---

## H — High-Level Architecture

> *(Dikosongkan)*

---

## A — API Design

> Referensi lengkap: [`api-docs.md`](./api-docs.md)

### Ringkasan Endpoints

#### Auth Service
| Method | Endpoint | Akses | Keterangan |
|--------|----------|-------|------------|
| POST | `/auth/register` | Public | Register akun baru |
| POST | `/auth/login` | Public | Login, dapat JWT |

#### User & Wallet Service
| Method | Endpoint | Akses | Keterangan |
|--------|----------|-------|------------|
| GET | `/users/profile` | User | Lihat profil |
| PUT | `/users/profile` | User | Update profil |
| GET | `/wallets/balance` | User | Lihat saldo |
| POST | `/wallets/topup` | User | Top up saldo |
| GET | `/wallets/mutations` | User | Riwayat mutasi |

#### Production Service (External)
| Method | Endpoint | Akses | Keterangan |
|--------|----------|-------|------------|
| GET | `/categories` | User | Daftar kategori |
| GET | `/products` | User | Daftar produk (filter by category) |
| GET | `/products/:id` | User | Detail produk |

#### Transaction Service
| Method | Endpoint | Akses | Keterangan |
|--------|----------|-------|------------|
| POST | `/transactions` | User | Buat transaksi |
| GET | `/transactions` | User | Riwayat transaksi pribadi |
| GET | `/transactions/:id` | User | Detail transaksi |
| GET | `/admin/transactions` | Admin | Semua transaksi semua user |
| POST | `/admin/product-prices` | Admin | Set harga jual produk |
| PUT | `/admin/product-prices/:id` | Admin | Update harga jual |

### Auth Header
```
Authorization: Bearer <jwt_token>
```

### Standard Response Format
```json
{
  "message": "...",
  "data": { ... },
  "meta": { "page": 1, "limit": 10, "total": 100 }
}
```

---

## D — Detailed Design

> *(Dikosongkan)*

---

## E — Evaluation

> *(Dikosongkan)*

---

## D — Decide Next 

> *(Dikosongkan)*
