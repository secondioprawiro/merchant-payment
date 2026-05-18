# API Documentation — Merchant Payment System

**Base URL:** `http://localhost:8762`  
**Auth:** Bearer token di header `Authorization: Bearer <token>`  
**Response format semua endpoint:**
```json
{
  "message": "...",
  "data": {}
}
```

---

## Auth Service

### POST /gateway/auth/register
Daftarkan akun + buat merchant secara otomatis.  
**Auth:** Tidak diperlukan

**Request Body:**
```json
{
  "email": "merchant@example.com",
  "password": "password123",
  "namaMerchant": "Toko Saya"
}
```

**Validasi:**
| Field | Aturan |
|-------|--------|
| email | Wajib, format email valid |
| password | Wajib, minimal 8 karakter |
| namaMerchant | Wajib |

**Response 201 Created:**
```json
{
  "message": "Register berhasil",
  "data": {
    "email": "merchant@example.com",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

**Response 400 — Email sudah terdaftar:**
```json
{
  "message": "Email already exists",
  "data": null
}
```

**Response 400 — Validasi gagal:**
```json
{
  "message": "Data tidak valid",
  "data": ["Format email tidak valid!", "Password minimal 8 karakter!"]
}
```

---

### POST /gateway/auth/login
Login dan dapatkan JWT token.  
**Auth:** Tidak diperlukan

**Request Body:**
```json
{
  "email": "merchant@example.com",
  "password": "password123"
}
```

**Validasi:**
| Field | Aturan |
|-------|--------|
| email | Wajib, format email valid |
| password | Wajib |

**Response 200 OK:**
```json
{
  "message": "Login berhasil",
  "data": {
    "email": "merchant@example.com",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

**Response 400 — Kredensial salah:**
```json
{
  "message": "Email atau password salah",
  "data": null
}
```

---

## Merchant Service

### GET /gateway/merchant
Ambil semua merchant yang aktif.  
**Auth:** Required | **Role:** ADMIN

**Headers:**
```
Authorization: Bearer <token>
```

**Response 200 OK:**
```json
{
  "message": "Success",
  "data": [
    {
      "kodeMerchant": "MCH-A1B2C3D4",
      "namaMerchant": "Toko Saya"
    }
  ]
}
```

**Response 403 — Bukan ADMIN:**
```json
{
  "message": "Access denied",
  "data": null
}
```

---

### GET /gateway/merchant/me
Ambil detail merchant milik user yang sedang login.  
**Auth:** Required | **Role:** USER

**Headers:**
```
Authorization: Bearer <token>
```

**Response 200 OK:**
```json
{
  "message": "Success",
  "data": {
    "merchantId": "550e8400-e29b-41d4-a716-446655440000",
    "kodeMerchant": "MCH-A1B2C3D4",
    "namaMerchant": "Toko Saya",
    "email": "merchant@example.com"
  }
}
```

**Response 404 — Merchant tidak ditemukan:**
```json
{
  "message": "Merchant tidak ditemukan",
  "data": null
}
```

---

### GET /gateway/merchant/{merchantId}
Ambil detail merchant beserta email akun.  
**Auth:** Required | **Role:** ADMIN

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameter:**
| Parameter | Tipe | Keterangan |
|-----------|------|------------|
| merchantId | UUID | ID merchant |

**Response 200 OK:**
```json
{
  "message": "Success",
  "data": {
    "merchantId": "550e8400-e29b-41d4-a716-446655440000",
    "kodeMerchant": "MCH-A1B2C3D4",
    "namaMerchant": "Toko Saya",
    "email": "merchant@example.com"
  }
}
```

**Response 403 — Bukan ADMIN:**
```json
{
  "message": "Access denied",
  "data": null
}
```

**Response 404 — Merchant tidak ditemukan:**
```json
{
  "message": "Merchant tidak ditemukan",
  "data": null
}
```

---

### PUT /gateway/merchant/{merchantId}
Update merchant. Field yang diproses berbeda berdasarkan role.  
**Auth:** Required | **Role:** ADMIN atau USER pemilik merchant

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameter:**
| Parameter | Tipe | Keterangan |
|-----------|------|------------|
| merchantId | UUID | ID merchant |

**Request Body** (semua field opsional):
```json
{
  "namaMerchant": "Toko Baru",
  "status": "ACTIVE",
  "email": "baru@example.com",
  "password": "newpassword123"
}
```

> **ADMIN:** Hanya `namaMerchant` dan `status` yang diproses. Field `email` dan `password` diabaikan.  
> **USER (pemilik):** Hanya `namaMerchant`, `email`, dan `password` yang diproses. Field `status` diabaikan.

**Nilai `status` yang valid:** `ACTIVE` | `INACTIVE`

**Validasi (jika diisi):**
| Field | Aturan |
|-------|--------|
| email | Format email valid |
| password | Minimal 8 karakter |

**Response 200 OK:**
```json
{
  "message": "Merchant updated",
  "data": {
    "merchantId": "550e8400-e29b-41d4-a716-446655440000",
    "namaMerchant": "Toko Baru",
    "email": "baru@example.com"
  }
}
```

**Response 403 — Bukan pemilik atau bukan ADMIN:**
```json
{
  "message": "Access denied",
  "data": null
}
```

**Response 404 — Merchant tidak ditemukan:**
```json
{
  "message": "Merchant tidak ditemukan",
  "data": null
}
```

---

### DELETE /gateway/merchant/{merchantId}
Soft delete merchant (status → INACTIVE, isDeleted → true).  
**Auth:** Required | **Role:** ADMIN atau USER pemilik merchant

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameter:**
| Parameter | Tipe | Keterangan |
|-----------|------|------------|
| merchantId | UUID | ID merchant |

**Response 200 OK:**
```json
{
  "message": "Merchant deleted",
  "data": null
}
```

**Response 403 — Bukan pemilik atau bukan ADMIN:**
```json
{
  "message": "Access denied",
  "data": null
}
```

**Response 404 — Merchant tidak ditemukan atau sudah dihapus:**
```json
{
  "message": "Merchant tidak ditemukan",
  "data": null
}
```

---

## Product Service

### GET /gateway/product
Ambil semua produk. Bisa filter berdasarkan type.  
**Auth:** Required

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameter (opsional):**
| Parameter | Nilai | Keterangan |
|-----------|-------|------------|
| type | `PULSA` / `PLN` | Filter berdasarkan jenis produk |

**Contoh:** `GET /gateway/product?type=PULSA`

**Response 200 OK:**
```json
{
  "message": "Success",
  "data": [
    {
      "productId": "PULSA_10K",
      "productName": "PULSA 10.000",
      "basePrice": 10000,
      "price": 11000,
      "type": "PULSA",
      "status": "AVAILABLE"
    },
    {
      "productId": "TOKEN_PLN_20K",
      "productName": "TOKEN PLN 20.000",
      "basePrice": 20000,
      "price": 21500,
      "type": "PLN",
      "status": "AVAILABLE"
    }
  ]
}
```

---

### GET /gateway/product/{productId}
Ambil detail satu produk.  
**Auth:** Required

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameter:**
| Parameter | Tipe | Keterangan |
|-----------|------|------------|
| productId | String | ID produk (contoh: `PULSA_10K`) |

**Response 200 OK:**
```json
{
  "message": "Success",
  "data": {
    "productId": "PULSA_10K",
    "productName": "PULSA 10.000",
    "basePrice": 10000,
    "price": 11000,
    "type": "PULSA",
    "status": "AVAILABLE"
  }
}
```

**Response 404 — Produk tidak ditemukan:**
```json
{
  "message": "Produk tidak ditemukan",
  "data": null
}
```

---

## Transaction Service

### POST /gateway/transaction
Beli produk (pulsa / token PLN).  
**Auth:** Required | **Role:** USER (merchant aktif)

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "productId": "PULSA_10K",
  "nomorTujuan": "08123456789"
}
```

**Validasi:**
| Field | Aturan |
|-------|--------|
| productId | Wajib, tidak boleh kosong |
| nomorTujuan | Wajib, tidak boleh kosong |
| nomorTujuan (PULSA) | 10–13 digit angka |
| nomorTujuan (PLN) | 11–12 digit angka |

**Daftar Product ID yang tersedia:**
| productId | Nama | Harga | Type |
|-----------|------|-------|------|
| PULSA_10K | PULSA 10.000 | 11.000 | PULSA |
| PULSA_25K | PULSA 25.000 | 26.000 | PULSA |
| PULSA_50K | PULSA 50.000 | 11.000 | PULSA |
| PULSA_100K | PULSA 100.000 | 11.000 | PULSA |
| TOKEN_PLN_20K | TOKEN PLN 20.000 | 21.500 | PLN |
| TOKEN_PLN_50K | TOKEN PLN 50.000 | 51.500 | PLN |
| TOKEN_PLN_100K | TOKEN PLN 100.000 | 101.500 | PLN |
| TOKEN_PLN_1000K | TOKEN PLN 1000.000 | 1.150.000 | PLN — NOT AVAILABLE |

**Response 200 — Transaksi berhasil:**
```json
{
  "message": "Transaksi berhasil",
  "data": {
    "transactionId": "550e8400-e29b-41d4-a716-446655440000",
    "merchantId": "...",
    "refId": "REF1715500000000",
    "productId": "PULSA_10K",
    "productName": "PULSA 10.000",
    "nomorTujuan": "08123456789",
    "amount": 11000,
    "status": "SUCCESS",
    "failureReason": null,
    "transactionDate": "2026-05-12T10:00:00"
  }
}
```

**Response 200 — Transaksi gagal (provider):**
```json
{
  "message": "Transaksi gagal",
  "data": {
    "transactionId": "...",
    "status": "FAILED",
    "failureReason": "Gangguan jaringan provider",
    ...
  }
}
```

**Response 400 — Merchant tidak aktif:**
```json
{
  "message": "Merchant tidak aktif",
  "data": null
}
```

**Response 400 — Nomor tujuan tidak valid:**
```json
{
  "message": "Nomor HP tidak valid",
  "data": null
}
```

**Response 404 — Produk tidak ditemukan:**
```json
{
  "message": "Produk tidak ditemukan",
  "data": null
}
```

---

### GET /gateway/transaction
Ambil semua transaksi dengan pagination dan filter tanggal opsional.  
**Auth:** Required  
**Role:** ADMIN (semua transaksi) | USER (transaksi merchant sendiri)

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
| Parameter | Tipe | Default | Keterangan |
|-----------|------|---------|------------|
| page | int | 0 | Halaman ke-n (0-based) |
| size | int | 20 | Jumlah item per halaman |
| startDate | LocalDate | - | Filter awal tanggal (format: `YYYY-MM-DD`) |
| endDate | LocalDate | - | Filter akhir tanggal (format: `YYYY-MM-DD`) |

> `startDate` dan `endDate` harus diisi keduanya atau tidak sama sekali.

**Response 200 OK:**
```json
{
  "message": "Success",
  "data": {
    "content": [
      {
        "transactionId": "550e8400-e29b-41d4-a716-446655440000",
        "merchantId": "...",
        "namaMerchant": "Toko A",
        "refId": "REF1715500000000",
        "productId": "PULSA_10K",
        "productName": "PULSA 10.000",
        "nomorTujuan": "08123456789",
        "amount": 11000,
        "status": "SUCCESS",
        "failureReason": null,
        "transactionDate": "2026-05-12T10:00:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "size": 20,
    "number": 0
  }
}
```

---

### GET /gateway/transaction/{transactionId}
Ambil detail satu transaksi.  
**Auth:** Required  
**Role:** ADMIN atau USER pemilik transaksi

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameter:**
| Parameter | Tipe | Keterangan |
|-----------|------|------------|
| transactionId | UUID | ID transaksi |

**Response 200 OK:**
```json
{
  "message": "Success",
  "data": {
    "transactionId": "550e8400-e29b-41d4-a716-446655440000",
    "merchantId": "...",
    "refId": "REF1715500000000",
    "productId": "PULSA_10K",
    "productName": "PULSA 10.000",
    "nomorTujuan": "08123456789",
    "amount": 11000,
    "status": "SUCCESS",
    "failureReason": null,
    "transactionDate": "2026-05-12T10:00:00"
  }
}
```

**Response 403 — Bukan pemilik transaksi:**
```json
{
  "message": "Tidak memiliki akses ke transaksi ini",
  "data": null
}
```

**Response 404 — Transaksi tidak ditemukan:**
```json
{
  "message": "Transaksi tidak ditemukan",
  "data": null
}
```

---

## Error Responses Umum

| HTTP Status | Kondisi |
|-------------|---------|
| 400 | Request tidak valid / validasi gagal |
| 401 | Token tidak ada atau tidak valid / expired |
| 403 | Tidak punya akses (role tidak sesuai / bukan pemilik) |
| 404 | Data tidak ditemukan |
| 500 | Kesalahan server / gagal hubungi service lain |

**Response 401 — Token tidak ada / invalid:**
```json
{
  "message": "Token tidak valid atau sudah expired",
  "data": null
}
```

**Response 500 — Server error:**
```json
{
  "message": "Terjadi kesalahan pada server",
  "data": null
}
```

---

## Port Services (Tanpa Gateway)

| Service | Port |
|---------|------|
| gateway-service | 8762 |
| auth-service | 8001 |
| merchant-service | (cek application.yaml) |
| product-service | (cek application.yaml) |
| registry-service (Eureka) | 8761 |
