-- Tabella Autori
CREATE TABLE IF NOT EXISTS authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NULL,
    last_name VARCHAR(100) NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

-- Tabella Post (con chiave esterna che punta all'Autore)
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    publish_date TIMESTAMP NULL,
    author_id BIGINT,
    CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);

-- Tabella Commenti (con chiave esterna che punta al Post)
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text TEXT NOT NULL,
    post_id BIGINT,
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);