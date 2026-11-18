CREATE DATABASE IF NOT EXISTS ai_daily
    CHARACTER SET utf8mb4;

USE ai_daily;

CREATE TABLE IF NOT EXISTS articles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    published_at BIGINT DEFAULT NULL,
    source VARCHAR(255) DEFAULT NULL,
    source_id BIGINT DEFAULT NULL,
    title VARCHAR(255) DEFAULT NULL,
    url VARCHAR(2048) NOT NULL,
    text TEXT DEFAULT NULL,
    summary TEXT DEFAULT NULL,
    category VARCHAR(255) DEFAULT NULL,
    ai_status VARCHAR(255) DEFAULT NULL,
    retry_count BIGINT DEFAULT NULL,
    last_error VARCHAR(255) DEFAULT NULL,
    interest_score BIGINT DEFAULT NULL,
    url_hash BINARY(32)
        GENERATED ALWAYS AS (UNHEX(SHA2(url, 256))) STORED,
    PRIMARY KEY (id),
    UNIQUE KEY uk_articles_url_hash (url_hash)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;
