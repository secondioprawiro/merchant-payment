# Brainstorming: Project 3 - Pulsa & PLN via Merchant

---

## Konteks: Apa yang Sudah Ada vs Apa yang Baru

### Sistem yang Sudah Didesain (kemarin)

```
User → Transaction Service → Production Service (external catalog)
                           → Wallet Service (potong saldo)
                           → Kafka (event transaksi)
```

User beli produk pakai **saldo wallet**. Produk dari Production Service (external). Ini alur untuk **end-user langsung**.

### Project 3: Merchant Scope (BARU)

```
Merchant System → Merchant API → Simulasi External Provider
                              → Simpan Transaction History
```

Ini alur berbeda. **Merchant adalah reseller/agen**, bukan user biasa. Tidak ada wallet. Tidak ada Production Service. Produk **static**.

---

## Siapa Actor-nya?

| Actor | Peran |
|-------|-------|
| **Merchant** | Reseller/agen (kios pulsa, warung, alfamart). Punya sistem sendiri (POS/aplikasi) yang memanggil API kita. |
| **Pelanggan Merchant** | Orang yang beli di kios merchant. Kita **tidak tahu** mereka — yang kita tahu hanya nomor tujuan mereka. |
| **External Provider** | Telkom/PLN — disimulasikan dengan random SUCCESS/FAILED. |

---

## Analogi Nyata

Bayangkan warung Bu Siti yang jual pulsa:
1. Pelanggan datang: "Mau beli pulsa 25.000 ke nomor 08123..."
2. Bu Siti buka aplikasi (sistem merchant) → input nomor HP pelanggan
3. Sistem Bu Siti kirim request ke API kita: merchant=BU_SITI, product=PULSA_25K, target=08123...
4. API kita proses → simulasi kirim ke Telkom → dapat hasil SUCCESS/FAILED
5. Bu Siti dapat konfirmasi → kasih tahu pelanggan

**Kita = provider/agregator yang melayani merchant.** Merchant bayar lewat sistem billing tersendiri (diluar scope project ini).

---

## Mengapa Produk Static?

Production Service (existing) adalah katalog produk **umum** yang bisa berubah-ubah. Project 3 punya produk **tetap dan sederhana**:

```
Pulsa:
  - PULSA_10K  → Rp 10.000
  - PULSA_25K  → Rp 25.000
  - PULSA_50K  → Rp 50.000

Token PLN:
  - PLN_20K   → Rp 20.000
  - PLN_50K   → Rp 50.000
  - PLN_100K  → Rp 100.000
```

Static = bisa disimpan sebagai enum/constant di code, atau tabel yang tidak perlu di-update via UI. Untuk keperluan bootcamp ini, static source sudah cukup.

---

## Alur Pembelian — Detail

```
POST /merchant/purchase
  Body: { merchant_id atau merchant_code, product_id, nomor_tujuan }
```

### Step 1: Validasi Merchant

Cek Redis dulu → kalau ada, pakai data Redis.
Kalau tidak ada → ambil dari DB → simpan ke Redis.

Yang dicek:
- Merchant dengan ID/kode tersebut ada di DB?
- Status merchant = `ACTIVE`?

Jika tidak valid → `400 Bad Request` / `404 Not Found`

### Step 2: Validasi Produk

Cek Redis dulu → kalau ada, pakai data Redis.
Kalau tidak ada → ambil dari static source → simpan ke Redis.

Yang dicek:
- Product ID valid (ada di daftar static products)?
- Produk aktif?

Jika tidak valid → `404 Not Found`

### Step 3: Validasi Nomor Tujuan

Format berbeda tergantung jenis produk:

| Jenis Produk | Nomor Tujuan | Format |
|-------------|--------------|--------|
| Pulsa | Nomor HP | `08XXXXXXXXXX` atau `628XXXXXXXXXX` — 10-13 digit, mulai 08 atau 62 |
| Token PLN | Nomor Meter / ID Pelanggan | 11-12 digit angka |

Validasi cukup dengan **format/regex** — kita tidak bisa cek apakah nomor benar-benar exist (itu urusan external system).

Jika tidak valid → `400 Bad Request`

### Step 4: Simulasi External System

Setelah semua validasi lolos, panggil "external system". Karena ini simulasi:

```java
// Simulasi delay jaringan
Thread.sleep(random 100-500ms);

// Hasil random
boolean isSuccess = Math.random() > 0.3; // 70% sukses, 30% gagal
```

Tidak ada HTTP call nyata. Cukup random + delay untuk simulasi.

### Step 5: Tentukan Hasil

| Hasil | Action |
|-------|--------|
| `SUCCESS` | Status = SUCCESS, failure_reason = null |
| `FAILED` | Status = FAILED, failure_reason = pesan error (contoh: "Nomor tidak terdaftar", "Gangguan sistem provider") |

### Step 6: Simpan Transaction History

INSERT ke tabel `merchant_transactions`:

```
transaction_id   → UUID (generate baru)
merchant_id      → dari request
product_id       → dari request
nomor_tujuan     → dari request
amount           → dari product (harga produk)
status           → SUCCESS / FAILED
failure_reason   → null jika success, pesan error jika failed
transaction_date → sekarang
```

Response dikembalikan ke merchant system dengan hasil transaksi.

---

## Data Model

### Tabel: `merchants`

| Column | Type | Keterangan |
|--------|------|------------|
| `merchant_id` | UUID | PRIMARY KEY |
| `nama_merchant` | VARCHAR | Nama merchant |
| `kode_merchant` | VARCHAR | UNIQUE — kode unik merchant (misal: "KIOS001") |
| `status` | ENUM(`ACTIVE`, `INACTIVE`) | Status merchant |
| `created_date` | TIMESTAMP | Auto-set saat create |
| `updated_date` | TIMESTAMP | Auto-update saat update |

### Produk Digital (Static)

Tidak perlu tabel — disimpan sebagai enum atau hardcoded list:

```java
public enum DigitalProduct {
    PULSA_10K("PULSA_10K", "Pulsa 10.000", "PULSA", 10000),
    PULSA_25K("PULSA_25K", "Pulsa 25.000", "PULSA", 25000),
    PULSA_50K("PULSA_50K", "Pulsa 50.000", "PULSA", 50000),
    PLN_20K("PLN_20K", "Token PLN 20.000", "PLN", 20000),
    PLN_50K("PLN_50K", "Token PLN 50.000", "PLN", 50000),
    PLN_100K("PLN_100K", "Token PLN 100.000", "PLN", 100000);
}
```

### Tabel: `merchant_transactions`

| Column | Type | Keterangan |
|--------|------|------------|
| `transaction_id` | UUID | PRIMARY KEY |
| `merchant_id` | UUID | ref → merchants.merchant_id |
| `product_id` | VARCHAR | ref ke static product code |
| `nomor_tujuan` | VARCHAR | Nomor HP atau Nomor Meter |
| `amount` | BIGINT | Harga produk |
| `status` | ENUM(`SUCCESS`, `FAILED`) | Hasil transaksi |
| `failure_reason` | VARCHAR | NULL jika success |
| `transaction_date` | TIMESTAMP | Waktu transaksi |

---

## Redis Caching

### Cache Merchant

```
Key: "merchant:{merchant_id}" atau "merchant:code:{kode_merchant}"
Value: JSON object merchant
TTL: 5 menit (atau sampai di-invalidate)
```

Flow:
1. Request masuk → cek Redis key `merchant:{id}`
2. Cache HIT → pakai langsung
3. Cache MISS → ambil dari DB → simpan ke Redis → pakai

Invalidate saat:
- PUT/PATCH merchant (update data)
- DELETE merchant
- Merchant status berubah

### Cache Product List

```
Key: "products:all"
Value: JSON array semua produk
TTL: 1 jam (produk static, jarang berubah)
```

Flow:
1. Request produk → cek Redis key `products:all`
2. Cache HIT → pakai langsung
3. Cache MISS → ambil dari static source → simpan ke Redis

Invalidate: manual (karena produk static, cache invalidation jarang terjadi)

---

## Endpoint yang Dibutuhkan

### Merchant CRUD

| Method | Endpoint | Keterangan |
|--------|----------|------------|
| POST | `/merchants` | Tambah merchant baru |
| GET | `/merchants` | List semua merchant |
| GET | `/merchants/{id}` | Detail merchant |
| PUT | `/merchants/{id}` | Update merchant |
| DELETE | `/merchants/{id}` | Hapus merchant |

### Product Digital

| Method | Endpoint | Keterangan |
|--------|----------|------------|
| GET | `/products` | List produk (dari cache/static) |
| GET | `/products/{id}` | Detail produk |

### Purchase & History

| Method | Endpoint | Keterangan |
|--------|----------|------------|
| POST | `/merchant/purchase` | Beli produk via merchant |
| GET | `/merchant/transactions` | Riwayat transaksi (bisa filter by merchant) |
| GET | `/merchant/transactions/{id}` | Detail transaksi |

---

## Hubungan dengan Sistem yang Sudah Ada

Project 3 adalah **domain terpisah** dari sistem transaksi user kemarin.

| Aspek | Sistem Kemarin | Project 3 |
|-------|---------------|-----------|
| Actor | End-user (punya akun) | Merchant (sistem eksternal) |
| Produk | Production Service (external catalog) | Static list (Pulsa & PLN) |
| Bayar | Potong wallet balance | Tidak ada — billing terpisah di luar scope |
| Auth | JWT token user | Merchant ID / kode merchant |
| Redis | (opsional) | Wajib untuk merchant + product |
| Transaksi | `transactions` table | `merchant_transactions` table baru |

### Apakah Perlu Service Baru?

Pilihan 1: **Tambah ke transaction-service**
- Pro: Tidak perlu service baru, lebih sederhana
- Con: Mencampur domain (user transaction vs merchant transaction)

Pilihan 2: **Buat merchant-service baru**
- Pro: Separation of concerns, sesuai microservices principle
- Con: Butuh setup baru (DB, Redis connection, port)

Untuk bootcamp, **merchant-service baru** lebih clean dan menunjukkan pemahaman microservices. Tapi kalau waktu terbatas, tambah ke transaction-service juga bisa.

---

## Ringkasan Singkat

```
Merchant punya kode unik → kirim request beli produk
→ Sistem validasi merchant (Redis/DB) → validasi produk (Redis/static)
→ validasi format nomor tujuan → simulasi call ke provider
→ random hasil SUCCESS/FAILED → simpan ke transaction history
→ return hasil ke merchant
```

Tidak ada user account. Tidak ada wallet. Tidak ada Production Service. Murni: **merchant request → validasi → simulasi → catat → return hasil**.
