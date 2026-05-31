-- ==========================================
-- TẠO DATABASE
-- ==========================================
CREATE DATABASE IF NOT EXISTS hunggaming_shop
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hunggaming_shop;

-- ==========================================
-- BẢNG USERS (người dùng + admin)
-- ==========================================
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    username    VARCHAR(50) UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(15),
    role        ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- BẢNG PRODUCTS (sản phẩm)
-- ==========================================
CREATE TABLE IF NOT EXISTS products (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    specifications  TEXT,                         -- thông số kỹ thuật
    origin          VARCHAR(100),                 -- xuất xứ
    price           DECIMAL(15, 0) NOT NULL,
    stock           INT NOT NULL DEFAULT 0,
    category        VARCHAR(100),
    brand           VARCHAR(100),
    image_url       VARCHAR(500),
    is_featured     BOOLEAN DEFAULT FALSE,        -- sản phẩm nổi bật trang chủ
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- BẢNG CARTS (giỏ hàng - mỗi user 1 cart)
-- ==========================================
CREATE TABLE IF NOT EXISTS carts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL UNIQUE,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- BẢNG CART_ITEMS (sản phẩm trong giỏ)
-- ==========================================
CREATE TABLE IF NOT EXISTS cart_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id     BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT NOT NULL DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_product (cart_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- BẢNG ORDERS (đơn hàng)
-- ==========================================
CREATE TABLE IF NOT EXISTS orders (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    full_name           VARCHAR(100) NOT NULL,    -- thông tin thanh toán
    email               VARCHAR(100) NOT NULL,
    phone               VARCHAR(15) NOT NULL,
    address             TEXT NOT NULL,
    total_amount        DECIMAL(15, 0) NOT NULL,
    status              ENUM('PENDING', 'CONFIRMED', 'DELIVERED', 'CANCELLED')
                        NOT NULL DEFAULT 'PENDING',
    payment_method      ENUM('QR', 'CARD', 'CASH') NOT NULL,
    payment_status      ENUM('UNPAID', 'PAID') NOT NULL DEFAULT 'UNPAID',
    note                TEXT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- BẢNG ORDER_ITEMS (sản phẩm trong đơn hàng)
-- ==========================================
CREATE TABLE IF NOT EXISTS order_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT NOT NULL,
    product_id  BIGINT,
    product_name    VARCHAR(200) NOT NULL,        -- lưu tên lúc mua (tránh mất khi xóa SP)
    product_image   VARCHAR(500),
    price           DECIMAL(15, 0) NOT NULL,      -- giá lúc mua
    quantity        INT NOT NULL,
    subtotal        DECIMAL(15, 0) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- BẢNG PAYMENTS (thanh toán)
-- ==========================================
CREATE TABLE IF NOT EXISTS payments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT NOT NULL UNIQUE,
    method          ENUM('QR', 'CARD', 'CASH') NOT NULL,
    amount          DECIMAL(15, 0) NOT NULL,
    status          ENUM('PENDING', 'SUCCESS', 'FAILED') NOT NULL DEFAULT 'PENDING',
    transaction_id  VARCHAR(100),                 -- mã giao dịch (nếu có)
    paid_at         DATETIME,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- DỮ LIỆU MẪU - ADMIN
-- Password: admin123 (đã bcrypt)
-- ==========================================
INSERT INTO users (full_name, username, email, password, phone, role) VALUES
('Admin', 'admin', 'admin@shop.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0123456789', 'ADMIN');

-- ==========================================
-- DỮ LIỆU MẪU - SẢN PHẨM
-- ==========================================
INSERT INTO products (name, description, specifications, origin, price, stock, category, brand, image_url, is_featured) VALUES
('Laptop ASUS ROG Strix G16',
 'Laptop gaming ASUS ROG Strix G16 với RTX 4070, màn hình 165Hz, thiết kế mạnh mẽ.',
 'CPU: Intel Core i9-13980HX\nRAM: 16GB DDR5\nSSD: 1TB NVMe\nGPU: RTX 4070 8GB\nMàn hình: 16" FHD 165Hz\nPin: 90Wh',
 'Đài Loan', 45990000, 20, 'Laptop', 'ASUS', 'https://product.hstatic.net/200000837185/product/laptopgamingasusrogstrixg16g614ju-n4132w_344e3c472ce540a1b1fd1a771f2a94d0_master.png', TRUE),

('MacBook Air M3 13 inch',
 'MacBook Air M3 mỏng nhẹ, hiệu năng tốt cho học tập, văn phòng và sáng tạo nội dung.',
 'Chip: Apple M3\nRAM: 8GB\nSSD: 256GB\nMàn hình: 13.6" Liquid Retina\nPin: 18 giờ\nKhối lượng: 1.24kg',
 'Mỹ', 28990000, 25, 'Laptop', 'Apple', 'https://maccenter.vn/App_images/MacBookAir-13-M3-Midnight-A.jpg', TRUE),

('PC Gaming RTX 4070 Super',
 'Bộ PC gaming hiệu năng cao, phù hợp chơi game AAA, livestream và render video.',
 'CPU: Intel Core i7\nRAM: 32GB DDR5\nSSD: 1TB NVMe\nGPU: RTX 4070 Super\nPSU: 750W Gold\nCase: RGB Airflow',
 'Việt Nam', 38990000, 12, 'PC Gaming', 'Hung Build', 'https://pcmarket.vn/media/product/10883_dsc00105_1.jpg', TRUE),

('Màn hình LG UltraGear 27 inch 165Hz',
 'Màn hình gaming 27 inch tần số quét 165Hz, màu sắc đẹp, phản hồi nhanh.',
 'Kích thước: 27 inch\nĐộ phân giải: QHD\nTần số quét: 165Hz\nTấm nền: IPS\nThời gian phản hồi: 1ms',
 'Hàn Quốc', 6990000, 35, 'Màn hình', 'LG', 'https://product.hstatic.net/200000722513/product/24gq50f_-_165hz_2db992d3606549d8ba0ce4700999f4a3_master.jpg', TRUE),

('Bàn phím cơ Keychron K8 Pro',
 'Bàn phím cơ không dây layout TKL, switch êm, hỗ trợ macOS và Windows.',
 'Layout: TKL\nSwitch: Brown\nKết nối: Bluetooth/USB-C\nKeycap: PBT\nĐèn nền: RGB',
 'Trung Quốc', 2490000, 80, 'Bàn phím', 'Keychron', 'https://bizweb.dktcdn.net/thumb/1024x1024/100/329/122/products/ban-phim-co-tkl-khong-day-keychron-k8-pro-white-led-hotswap-gateron-g-pro-switch-4-05041ac1-d1b3-4e21-9021-7a9a419a6530.jpg?v=1692600770003', TRUE),

('Chuột Logitech MX Master 3S',
 'Chuột không dây cao cấp Logitech MX Master 3S, cảm biến 8000 DPI, kết nối đa thiết bị.',
 'DPI: 200-8000\nPin: 70 ngày\nKết nối: Bluetooth + USB Receiver\nSố nút: 7\nKhối lượng: 141g',
 'Thụy Sĩ', 2190000, 200, 'Chuột', 'Logitech', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/c/h/chuot-choi-game-co-day-logitech-g502-hero.png', TRUE),

('Tai nghe Sony WH-1000XM5',
 'Tai nghe chống ồn cao cấp từ Sony, âm thanh Hi-Res, pin 30 giờ.',
 'Driver: 30mm\nChống ồn: Active Noise Cancelling\nPin: 30 giờ\nKết nối: Bluetooth 5.2\nTrọng lượng: 250g',
 'Nhật Bản', 8490000, 100, 'Tai nghe', 'Sony', 'https://bizweb.dktcdn.net/100/340/129/products/wh-ch520-hong-pink-cuongphanvn.jpg?v=1745118456730', TRUE),

('iPhone 15 Pro Max 256GB',
 'iPhone 15 Pro Max với chip A17 Pro mạnh mẽ, camera 48MP, màn hình Super Retina XDR 6.7 inch.',
 'Chip: A17 Pro\nRAM: 8GB\nBộ nhớ: 256GB\nMàn hình: 6.7" Super Retina XDR\nCamera: 48MP + 12MP + 12MP\nPin: 4422mAh',
 'Mỹ', 34990000, 50, 'Điện thoại', 'Apple', 'https://cdn.tgdd.vn/Products/Images/42/305658/iphone-15-pro-max-blue-1-1-750x500.jpg', TRUE);
