# API Documentation

Base URL: `http://localhost:{port}`

Auth: semua endpoint (kecuali register & login) butuh header:
```
Authorization: Bearer <token>
```

---

## 1. Auth Service

### POST `/auth/register`

Registrasi akun baru. Otomatis trigger pembuatan profile & wallet via event.

**Request**
```json
{
  "email": "user@example.com",
  "password": "secret123",
  "full_name": "Budi Santoso",
  "phone_number": "081234567890"
}
```

**Response `201`**
```json
{
  "message": "Register success",
  "data": {
    "account_id": "uuid",
    "email": "user@example.com",
    "role": "user",
    "created_at": "2026-05-06T10:00:00Z"
  }
}
```

**Response `409`** — email sudah terdaftar
```json
{
  "message": "Email already exists"
}
```

---

### POST `/auth/login`

**Request**
```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

**Response `200`**
```json
{
  "message": "Login success",
  "data": {
    "access_token": "eyJhbGci...",
    "token_type": "Bearer"
  }
}
```

**Response `401`** — email/password salah
```json
{
  "message": "Invalid credentials"
}
```

---

## 2. User & Wallet Service

### GET `/users/profile`

Ambil profil user yang sedang login.

**Response `200`**
```json
{
  "message": "OK",
  "data": {
    "user_id": "uuid",
    "full_name": "Budi Santoso",
    "phone_number": "081234567890"
  }
}
```

---

### PUT `/users/profile`

Update profil user.

**Request**
```json
{
  "full_name": "Budi Santoso",
  "phone_number": "081234567890"
}
```

**Response `200`**
```json
{
  "message": "Profile updated",
  "data": {
    "user_id": "uuid",
    "full_name": "Budi Santoso",
    "phone_number": "081234567890"
  }
}
```

---

### GET `/wallets/balance`

Ambil saldo wallet user.

**Response `200`**
```json
{
  "message": "OK",
  "data": {
    "user_id": "uuid",
    "balance": 150000
  }
}
```

---

### POST `/wallets/topup`

Top up saldo wallet.

**Request**
```json
{
  "amount": 50000
}
```

**Response `200`**
```json
{
  "message": "Top up success",
  "data": {
    "user_id": "uuid",
    "balance": 200000,
    "mutation": {
      "id": "uuid",
      "amount": 50000,
      "type": "credit",
      "desc": "Top up"
    }
  }
}
```

**Response `400`** — amount tidak valid
```json
{
  "message": "Amount must be greater than 0"
}
```

---

### GET `/wallets/mutations`

Riwayat mutasi wallet.

**Query Params**

| Param | Type | Default | Keterangan |
|-------|------|---------|------------|
| `page` | int | 1 | Halaman |
| `limit` | int | 10 | Item per halaman |

**Response `200`**
```json
{
  "message": "OK",
  "data": [
    {
      "id": "uuid",
      "amount": 50000,
      "type": "credit",
      "desc": "Top up",
    },
    {
      "id": "uuid",
      "amount": 15000,
      "type": "debit",
      "desc": "Pembelian Pulsa 15.000"
    }
  ],
  "meta": {
    "page": 1,
    "limit": 10,
    "total": 2
  }
}
```

---

## 3. Production Service (External)

> Read-only. Tidak ada operasi write dari sistem ini.

### GET `/products`

Ambil daftar produk aktif.

**Query Params**

| Param | Type | Keterangan |
|-------|------|------------|
| `category_id` | int | Filter by kategori |
| `page` | int | Halaman |
| `limit` | int | Item per halaman |

**Response `200`**
```json
{
  "message": "OK",
  "data": [
    {
      "id": 1,
      "category_id": 2,
      "name": "Pulsa Telkomsel 15.000",
      "price": 15500,
      "is_active": true
    }
  ],
  "meta": {
    "page": 1,
    "limit": 10,
    "total": 1
  }
}
```

---

### GET `/products/:id`

Ambil detail produk.

**Response `200`**
```json
{
  "message": "OK",
  "data": {
    "id": 1,
    "category_id": 2,
    "name": "Pulsa Telkomsel 15.000",
    "price": 15500,
    "is_active": true
  }
}
```

**Response `404`**
```json
{
  "message": "Product not found"
}
```

---

### GET `/categories`

Ambil daftar kategori produk.

**Response `200`**
```json
{
  "message": "OK",
  "data": [
    {
      "id": 1,
      "name": "Pulsa",
      "type": "prepaid"
    },
    {
      "id": 2,
      "name": "Token PLN",
      "type": "prepaid"
    }
  ]
}
```

---

## 4. Transaction Service

### POST `/transactions`

Buat transaksi pembelian produk. Akan memotong saldo wallet user.

**Request**
```json
{
  "product_id": 1,
  "target_number": "081234567890"
}
```

**Response `201`**
```json
{
  "message": "Transaction created",
  "data": {
    "id": "uuid",
    "product_id": 1,
    "target_number": "081234567890",
    "base_price": 15500,
    "markup_amount": 500,
    "final_price": 16000,
    "status": "pending",
    "sn": null,
    "created_at": "2026-05-06T10:00:00Z"
  }
}
```

**Response `400`** — saldo tidak cukup
```json
{
  "message": "Insufficient balance"
}
```

**Response `404`** — produk tidak ditemukan / tidak aktif
```json
{
  "message": "Product not found or inactive"
}
```

---

### GET `/transactions`

Riwayat transaksi user.

**Query Params**

| Param | Type | Default | Keterangan |
|-------|------|---------|------------|
| `page` | int | 1 | Halaman |
| `limit` | int | 10 | Item per halaman |
| `status` | string | - | Filter: `pending`, `processing`, `success`, `failed` |

**Response `200`**
```json
{
  "message": "OK",
  "data": [
    {
      "id": "uuid",
      "product_id": 1,
      "target_number": "081234567890",
      "final_price": 16000,
      "status": "success",
      "sn": "PLN-TOKEN-1234567890",
      "created_at": "2026-05-06T10:00:00Z"
    }
  ],
  "meta": {
    "page": 1,
    "limit": 10,
    "total": 1
  }
}
```

---

### GET `/transactions/:id`

Detail satu transaksi.

**Response `200`**
```json
{
  "message": "OK",
  "data": {
    "id": "uuid",
    "product_id": 1,
    "target_number": "081234567890",
    "base_price": 15500,
    "markup_amount": 500,
    "final_price": 16000,
    "status": "success",
    "sn": "PLN-TOKEN-1234567890",
    "created_at": "2026-05-06T10:00:00Z"
  }
}
```

**Response `404`**
```json
{
  "message": "Transaction not found"
}
```

---

## 5. Admin Endpoints

> Role `admin` only.

### POST `/admin/product-prices`

Set harga jual produk (selling price dari external Product Service).

**Request**
```json
{
  "product_id": 1,
  "selling_price": 16000,
  "admin_fee": 500
}
```

**Response `201`**
```json
{
  "message": "Product price created",
  "data": {
    "product_id": 1,
    "selling_price": 16000,
    "admin_fee": 500,
    "is_active": true,
    "updated_at": "2026-05-06T10:00:00Z"
  }
}
```

---

### PUT `/admin/product-prices/:product_id`

Update harga jual produk.

**Request**
```json
{
  "selling_price": 17000,
  "admin_fee": 1000,
  "is_active": true
}
```

**Response `200`**
```json
{
  "message": "Product price updated",
  "data": {
    "product_id": 1,
    "selling_price": 17000,
    "admin_fee": 1000,
    "is_active": true,
    "updated_at": "2026-05-06T11:00:00Z"
  }
}
```

---

## Error Response Format

Semua error mengikuti format:

```json
{
  "message": "Deskripsi error"
}
```

| Status Code | Keterangan |
|-------------|------------|
| `400` | Bad Request — input tidak valid |
| `401` | Unauthorized — token tidak ada / expired |
| `403` | Forbidden — role tidak punya akses |
| `404` | Not Found — resource tidak ditemukan |
| `409` | Conflict — duplikasi data |
| `500` | Internal Server Error |
