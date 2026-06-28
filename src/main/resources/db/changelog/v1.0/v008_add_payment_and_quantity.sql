ALTER TABLE orders ADD COLUMN shipping_street VARCHAR(255);
ALTER TABLE orders ADD COLUMN shipping_postal_code VARCHAR(20);
ALTER TABLE orders ADD COLUMN shipping_region VARCHAR(150);
ALTER TABLE orders ADD COLUMN shipping_country VARCHAR(150);
ALTER TABLE orders ADD COLUMN currency VARCHAR(10) DEFAULT 'EUR';
ALTER TABLE orders ADD COLUMN paypal_order_id VARCHAR(64);
ALTER TABLE orders ADD COLUMN paypal_capture_id VARCHAR(64);

ALTER TABLE order_items ADD COLUMN quantity INTEGER NOT NULL DEFAULT 1;
