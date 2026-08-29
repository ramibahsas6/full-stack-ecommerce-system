CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20),
    country VARCHAR(100),
    city VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_edited BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(255) DEFAULT 'USER'
);

INSERT INTO users (username, first_name, last_name, email, phone, country, city, password, role) VALUES
('ramibahsas','Rami', 'Bahsas', 'ramibahsas@gmail.com', '0502277910', 'israel', 'buqata', '$2a$10$K78Qy75RrDNQcAolPojuM.sI.otXpP23xhZYJ7p2fXrIMoI.k2ehO', 'USER'),
('amitaygabay', 'Amitay', 'Gabay', 'amitaygabay1@gmail.com', '0504380333', 'israel','38 Erez St', '$2a$10$K78Qy75RrDNQcAolPojuM.sI.otXpP23xhZYJ7p2fXrIMoI.k2ehO', 'ADMIN'),
('meladwily', 'Melad', 'Wily', 'meladwily@gmail.com', '0542449892', 'israel', 'buqata', '$2a$10$K78Qy75RrDNQcAolPojuM.sI.otXpP23xhZYJ7p2fXrIMoI.k2ehO', 'ADMIN');

CREATE TABLE items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    photo_url VARCHAR(3000),
    price_usd DECIMAL(10,2) NOT NULL CHECK (price_usd >= 0),
    stock INT NOT NULL CHECK (stock >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_edited BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- INSERT INTO items (title, description, photo_url, price_usd, stock) VALUES
-- ('Sunglasses Classic', 'Polarized sunglasses', 'https://example.com/img/sunglasses.jpg', 29.99, 25),
-- ('Wireless Earbuds', 'Bluetooth 5.2 earbuds', 'https://example.com/img/earbuds.jpg', 59.99, 40),
-- ('Coffee Maker 1.5L', 'Automatic coffee maker', 'https://example.com/img/coffee_maker.jpg', 89.50, 10),
-- ('Running Shoes', 'Lightweight running shoes', 'https://example.com/img/shoes.jpg', 75.00, 20),
-- ('Yoga Mat', 'Non-slip yoga mat', 'https://example.com/img/yogamat.jpg', 19.99, 50),
-- ('Smartwatch X2', 'Fitness smartwatch', 'https://example.com/img/smartwatch.jpg', 129.99, 15),
-- ('Leather Wallet', 'Genuine leather bi-fold', 'https://example.com/img/wallet.jpg', 35.00, 30),
-- ('Desk Lamp LED', 'Adjustable lamp', 'https://example.com/img/lampe.jpg', 22.49, 5),
-- ('Backpack 20L', 'Water-resistant', 'https://example.com/img/backpack.jpg', 45.00, 12),
-- ('Sunscreen SPF50', 'SPF50 sunscreen 100ml', 'https://example.com/img/sunscreen.jpg', 8.99, 60);

INSERT INTO items (title, description, photo_url, price_usd, stock)
VALUES
('Classic Leather Watch', 'Elegant classic leather watch', 'https://images.unsplash.com/photo-1523170335258-f5ed11844a49?q=80&w=1180&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 99, 10),
('Sport Sneakers', 'Lightweight breathable sport shoes', 'https://images.unsplash.com/photo-1603787081207-362bcef7c144?q=80&w=765&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 79, 15),
('Sunglasses', 'Stylish UV-protection sunglasses', 'https://images.unsplash.com/photo-1732139637227-ff5c7522fd9d?q=80&w=736&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 60, 20),
('Women’s Perfume', 'Elegant floral women fragrance', 'https://images.unsplash.com/photo-1617943539287-d6fe110ac7ad?q=80&w=1077&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 109, 20),
('White T-Shirt', 'Soft basic cotton shirt', 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 20, 25),
('Summer Dress', 'Light floral summer dress', 'https://images.unsplash.com/flagged/photo-1585052201332-b8c0ce30972f?q=80&w=735&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 35, 30),
('Wireless Earbuds', 'Compact wireless stereo earbuds', 'https://images.unsplash.com/photo-1606135185526-1bd767d76d65?q=80&w=1332&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 33, 35),
('Lipstick', 'Long-lasting premium lipstick', 'https://images.unsplash.com/photo-1626895872564-b691b6877b83?q=80&w=764&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 9, 40),
('Leather Wallet', 'Durable handcrafted leather wallet', 'https://images.unsplash.com/photo-1620109176813-e91290f6c795?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 55, 45),
('Backpack', 'Spacious everyday travel backpack', 'https://images.unsplash.com/photo-1547949003-9792a18a2601?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 88, 50),
('Mechanical Keyboard', 'Tactile backlit mechanical keyboard', 'https://images.unsplash.com/photo-1592424002053-21f369ad7fdb?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 46, 55),
('Gaming Mouse', 'Ergonomic high-precision mouse', 'https://images.unsplash.com/photo-1628832307345-7404b47f1751?q=80&w=1183&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 15, 5),
('Smartphone iPhone 17', 'Latest generation premium smartphone', 'https://images.unsplash.com/photo-1759588071814-1ba7c5761af4?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 1150, 8),
('Over-Ear Headphones', 'Comfortable noise-isolating headphones', 'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?q=80&w=688&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 95, 9),
('Scented Candle', 'Relaxing aromatic home candle', 'https://images.unsplash.com/photo-1596433809252-260c2745dfdd?q=80&w=765&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 5, 11),
('Face Cream', 'Hydrating daily facial cream', 'https://images.unsplash.com/photo-1641130290711-01c4c4558562?q=80&w=627&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 11, 12),
('Thermal Water Bottle', 'Stainless steel thermal bottle', 'https://images.unsplash.com/photo-1616740540792-3daec604777d?q=80&w=764&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 10, 13),
('Winter Jacket', 'Warm insulated winter jacket', 'https://images.unsplash.com/photo-1706765779494-2705542ebe74?q=80&w=1051&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 90, 14),
('High Heels', 'Elegant stylish high heels', 'https://images.unsplash.com/photo-1581101767113-1677fc2beaa8?q=80&w=880&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 59, 16),
('Laptop Bag', 'Protective laptop carrying bag', 'https://images.unsplash.com/photo-1554412664-6e7b242f969d?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 45, 17),
('Electric Shaver', 'Smooth fast electric shaver', 'https://images.unsplash.com/photo-1646376241249-f261e72c2029?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 60, 20),
('Hair Straightener', 'Ceramic quick heat straightener', 'https://images.unsplash.com/photo-1620331307581-1e7d27da7ab6?q=80&w=1111&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 55, 20),
('Men’s Perfume', 'Fresh long-lasting men fragrance', 'https://images.unsplash.com/photo-1719176010239-389baa89f350?q=80&w=627&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 122, 20),
('Office Chair', 'Comfortable ergonomic office chair', 'https://images.unsplash.com/photo-1750306957077-b74e45fe1819?q=80&w=764&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 45, 10),
('LED Desk Lamp', 'Compact adjustable desk lamp', 'https://images.unsplash.com/photo-1708414338277-52f15b8e5082?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 19, 10),
('Luxury Blanket', 'Soft warm luxury blanket', 'https://images.unsplash.com/photo-1607300110843-b3994a493d98?q=80&w=735&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 22, 10),
('Power Bank', 'Portable fast-charging power bank', 'https://images.unsplash.com/photo-1585995603413-eb35b5f4a50b?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 28, 10),
('Compact Camera', 'Lightweight compact travel camera', 'https://images.unsplash.com/photo-1471293413508-2cb95f2d2e9a?q=80&w=1010&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 27, 15),
('Travel Bag', 'Durable stylish travel bag', 'https://images.unsplash.com/photo-1557160836-f3a6d1afaab2?q=80&w=735&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 38, 15),
('Silver Ring', 'Minimalist polished silver ring', 'https://images.unsplash.com/photo-1589674781759-c21c37956a44?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 23, 15);


CREATE TABLE favorites (
    username VARCHAR(100) NOT NULL,
    item_id BIGINT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    edited BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (username, item_id),
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    shipping_country VARCHAR(100),
    shipping_city VARCHAR(100),
    total_price DECIMAL(12,2) NOT NULL DEFAULT 0,
    status ENUM('TEMP', 'CLOSE') NOT NULL DEFAULT 'TEMP',
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

INSERT INTO orders (username,shipping_country,shipping_city,status)
VALUES
('meladwily','israel', 'buqata', 'CLOSE'),
('meladwily','israel', 'buqata', 'CLOSE'),
('meladwily','israel', 'buqata', 'TEMP'),
('ramibahsas', 'israel', 'buqata', 'TEMP'),
('amitaygabay', 'israel', '38 Erez St', 'TEMP');

UPDATE orders SET total_price = 761 WHERE id = 1;
UPDATE orders SET total_price = 593 WHERE id = 2;

CREATE TABLE order_items (
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 0),
    price_at_purchase DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (order_id, item_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id)
);

INSERT INTO order_items (order_id, item_id, quantity, price_at_purchase, total_price)
VALUES
(2, 1, 2, 99, 198),

(3, 2, 1, 79, 79),
(1, 1, 4, 99, 396),
(1, 2, 2, 79, 158),
(2, 2, 5, 79, 395),
(5, 1, 2, 99, 198),
(1, 3, 3, 60, 180),
(5, 2, 15, 79, 1185),
(3, 1, 2, 99, 198),
(4, 2, 14, 79, 1106),
(4, 1, 9, 99, 891);
