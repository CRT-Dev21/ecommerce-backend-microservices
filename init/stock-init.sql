CREATE TABLE IF NOT EXISTS product_stocks (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    qty INTEGER NOT NULL,
    category VARCHAR(255),
    image_name VARCHAR(255),
    minimum_stock INTEGER DEFAULT 10,
    location VARCHAR(255),
    seller_id UUID,
    product_code VARCHAR(100) UNIQUE,
    version INTEGER DEFAULT 0
);
