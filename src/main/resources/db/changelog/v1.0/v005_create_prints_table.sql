CREATE TABLE prints (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    height DECIMAL(10,2),
    width DECIMAL(10,2),
    price DECIMAL(10,2),
    image_url_1 VARCHAR(500),
    image_url_2 VARCHAR(500),
    image_url_3 VARCHAR(500),
    image_url_4 VARCHAR(500),
    image_url_5 VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
