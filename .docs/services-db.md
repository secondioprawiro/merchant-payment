# Services Database Schema

---

## 1. Auth Service DB

### Table: `accounts`

| Column | Type | Constraints |
|--------|------|-------------|
| `account_id` | UUID | PRIMARY KEY |
| `email` | STRING | UNIQUE, NOT NULL |
| `password` | STRING (hashed) | NOT NULL |
| `role` | ENUM(`user`, `admin`) | DEFAULT `user` |
| `created_at` | TIMESTAMP | NOT NULL |

---

## 2. User & Wallet Service DB

### Table: `profiles`

| Column | Type | Constraints |
|--------|------|-------------|
| `user_id` | UUID | PRIMARY KEY |
| `first_name` | STRING | NOT NULL |
| `last_name` | STRING | NOT NULL |
| `phone_number` | STRING | |

### Table: `wallets`

| Column | Type | Constraints |
|--------|------|-------------|
| `user_id` | UUID | PRIMARY KEY |
| `balance` | BIGINT | NOT NULL |

### Table: `wallet_mutations`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | UUID | PRIMARY KEY |
| `user_id` | UUID | FOREIGN KEY → `wallets.user_id` |
| `amount` | BIGINT | NOT NULL |
| `type` | ENUM | NOT NULL |
| `desc` | STRING | |
| `created_at` | TIMESTAMP | NOT NULL |

---

## 3. Production Service DB

> **Status: External / Third-Party** — read-only via API, tidak ada kontrol langsung terhadap database ini.

### Table: `categories`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | INT | PRIMARY KEY |
| `name` | STRING | NOT NULL |
| `type` | ENUM | NOT NULL |

### Table: `products`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | INT | PRIMARY KEY |
| `category_id` | INT | FOREIGN KEY → `categories.id` |
| `name` | STRING | NOT NULL |
| `price` | BIGINT | NOT NULL |
| `is_active` | BOOLEAN | NOT NULL |

---

## 4. Transaction Service DB

### Table: `product_prices`

| Column | Type | Constraints |
|--------|------|-------------|
| `product_id` | INT | PRIMARY KEY (ref ke external Product Service) |
| `selling_price` | BIGINT | NOT NULL |
| `admin_fee` | BIGINT | NOT NULL |
| `is_active` | BOOLEAN | NOT NULL |
| `updated_at` | TIMESTAMP | NOT NULL |

### Table: `transactions`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | UUID / BIGINT | PRIMARY KEY |
| `user_id` | UUID / BIGINT | ref → `profiles.user_id` |
| `product_id` | INT | ref → `products.id` |
| `target_number` | STRING | NOT NULL |
| `base_price` | BIGINT | NOT NULL |
| `markup_amount` | BIGINT | NOT NULL |
| `status` | ENUM(`pending`, `processing`, `success`, `failed`) | NOT NULL |
| `sn` | STRING | NULLABLE — Serial Number / Token PLN (diisi jika sukses) |
| `created_at` | TIMESTAMP | NOT NULL |
