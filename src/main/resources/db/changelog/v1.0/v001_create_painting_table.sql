CREATE TABLE paintings (
                           id UUID PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           technique VARCHAR(100),
                           year INTEGER,
                           print_size VARCHAR(50),
                           print_price DECIMAL(10,2),
                           original_available BOOLEAN DEFAULT true,
                           original_price DECIMAL(15,2),
                           image_url_1 VARCHAR(500),
                           image_url_2 VARCHAR(500),
                           image_url_3 VARCHAR(500),
                           image_url_4 VARCHAR(500),
                           image_url_5 VARCHAR(500),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);