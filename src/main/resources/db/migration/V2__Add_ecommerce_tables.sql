-- Create turma enum type (PostgreSQL)
DO $$ BEGIN
    CREATE TYPE turma_enum AS ENUM (
        'PRIMEIRO_A', 'PRIMEIRO_B', 'PRIMEIRO_C', 'PRIMEIRO_D',
        'SEGUNDO_A', 'SEGUNDO_B', 'SEGUNDO_C', 'SEGUNDO_D', 'SEGUNDO_D_MARTA',
        'TERCEIRO_A', 'TERCEIRO_B', 'TERCEIRO_C', 'TERCEIRO_D'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Create payment_status enum type
DO $$ BEGIN
    CREATE TYPE payment_status_enum AS ENUM ('PENDENTE', 'PAGO');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Update users table to add new fields
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS full_name VARCHAR(100) NOT NULL DEFAULT '',
ADD COLUMN IF NOT EXISTS turma turma_enum NOT NULL DEFAULT 'PRIMEIRO_A',
ADD COLUMN IF NOT EXISTS has_paid BOOLEAN NOT NULL DEFAULT FALSE;

-- Update existing users to have default values
UPDATE users SET full_name = COALESCE(first_name || ' ' || last_name, '') WHERE full_name = '';
UPDATE users SET turma = 'PRIMEIRO_A' WHERE turma IS NULL;

-- Add constraints
ALTER TABLE users 
ADD CONSTRAINT IF NOT EXISTS chk_users_turma CHECK (turma IS NOT NULL),
ADD CONSTRAINT IF NOT EXISTS chk_users_full_name CHECK (full_name IS NOT NULL AND length(full_name) > 0);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    image_url VARCHAR(500),
    available BOOLEAN NOT NULL DEFAULT TRUE,
    kit_discount_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00 CHECK (kit_discount_percentage >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create kits table
CREATE TABLE IF NOT EXISTS kits (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    image_url VARCHAR(500),
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create cart_items table
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT,
    kit_id BIGINT,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2),
    total_price DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_cart_item_product_or_kit CHECK (
        (product_id IS NOT NULL AND kit_id IS NULL) OR 
        (product_id IS NULL AND kit_id IS NOT NULL)
    )
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    payment_status payment_status_enum NOT NULL DEFAULT 'PENDENTE',
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    pix_key VARCHAR(100),
    payment_date TIMESTAMP,
    payment_proof_url VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT,
    kit_id BIGINT,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2),
    total_price DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_order_item_product_or_kit CHECK (
        (product_id IS NOT NULL AND kit_id IS NULL) OR 
        (product_id IS NULL AND kit_id IS NOT NULL)
    )
);

-- Create junction tables for turma restrictions
CREATE TABLE IF NOT EXISTS product_turmas (
    product_id BIGINT NOT NULL,
    turma turma_enum NOT NULL,
    PRIMARY KEY (product_id, turma)
);

CREATE TABLE IF NOT EXISTS kit_turmas (
    kit_id BIGINT NOT NULL,
    turma turma_enum NOT NULL,
    PRIMARY KEY (kit_id, turma)
);

-- Create junction table for kit products
CREATE TABLE IF NOT EXISTS kit_products (
    kit_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    PRIMARY KEY (kit_id, product_id)
);

-- Add foreign keys
ALTER TABLE cart_items 
ADD CONSTRAINT IF NOT EXISTS fk_cart_items_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
ADD CONSTRAINT IF NOT EXISTS fk_cart_items_product 
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
ADD CONSTRAINT IF NOT EXISTS fk_cart_items_kit 
    FOREIGN KEY (kit_id) REFERENCES kits(id) ON DELETE CASCADE;

ALTER TABLE orders 
ADD CONSTRAINT IF NOT EXISTS fk_orders_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE order_items 
ADD CONSTRAINT IF NOT EXISTS fk_order_items_order 
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
ADD CONSTRAINT IF NOT EXISTS fk_order_items_product 
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
ADD CONSTRAINT IF NOT EXISTS fk_order_items_kit 
    FOREIGN KEY (kit_id) REFERENCES kits(id) ON DELETE CASCADE;

ALTER TABLE product_turmas 
ADD CONSTRAINT IF NOT EXISTS fk_product_turmas_product 
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;

ALTER TABLE kit_turmas 
ADD CONSTRAINT IF NOT EXISTS fk_kit_turmas_kit 
    FOREIGN KEY (kit_id) REFERENCES kits(id) ON DELETE CASCADE;

ALTER TABLE kit_products 
ADD CONSTRAINT IF NOT EXISTS fk_kit_products_kit 
    FOREIGN KEY (kit_id) REFERENCES kits(id) ON DELETE CASCADE,
ADD CONSTRAINT IF NOT EXISTS fk_kit_products_product 
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_products_available ON products(available);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_kits_available ON kits(available);
CREATE INDEX IF NOT EXISTS idx_kits_name ON kits(name);
CREATE INDEX IF NOT EXISTS idx_cart_items_user ON cart_items(user_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_product ON cart_items(product_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_kit ON cart_items(kit_id);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(payment_status);
CREATE INDEX IF NOT EXISTS idx_orders_order_number ON orders(order_number);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_items(product_id);
CREATE INDEX IF NOT EXISTS idx_order_items_kit ON order_items(kit_id);
CREATE INDEX IF NOT EXISTS idx_users_turma ON users(turma);
CREATE INDEX IF NOT EXISTS idx_users_has_paid ON users(has_paid);
CREATE INDEX IF NOT EXISTS idx_users_full_name ON users(full_name);

-- Create indexes for junction tables
CREATE INDEX IF NOT EXISTS idx_product_turmas_turma ON product_turmas(turma);
CREATE INDEX IF NOT EXISTS idx_kit_turmas_turma ON kit_turmas(turma);
CREATE INDEX IF NOT EXISTS idx_kit_products_product ON kit_products(product_id);

-- Add triggers for updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER IF NOT EXISTS update_products_updated_at 
    BEFORE UPDATE ON products 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER IF NOT EXISTS update_kits_updated_at 
    BEFORE UPDATE ON kits 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER IF NOT EXISTS update_cart_items_updated_at 
    BEFORE UPDATE ON cart_items 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER IF NOT EXISTS update_orders_updated_at 
    BEFORE UPDATE ON orders 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER IF NOT EXISTS update_order_items_updated_at 
    BEFORE UPDATE ON order_items 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER IF NOT EXISTS update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
