CREATE TABLE orders (
    id UUID PRIMARY KEY,
    buyer_name VARCHAR(255) NOT NULL,
    buyer_email VARCHAR(255) NOT NULL,
    shipping_address TEXT,
    total_amount DECIMAL(12,2),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    painting_id UUID NOT NULL,
    painting_name VARCHAR(255) NOT NULL,
    image_url VARCHAR(500),
    price DECIMAL(10,2) NOT NULL
);
