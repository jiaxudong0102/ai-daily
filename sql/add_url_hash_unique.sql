-- For an articles table previously created by JPA.
-- Inspect duplicates first; ALTER TABLE fails if any exist.
USE ai_daily;

SELECT SHA2(url, 256) AS url_hash, COUNT(*) AS duplicate_count
FROM articles
GROUP BY SHA2(url, 256)
HAVING COUNT(*) > 1;

-- Run only when the query above returns no rows and the index is absent.
ALTER TABLE articles
    ADD COLUMN url_hash BINARY(32)
        GENERATED ALWAYS AS (UNHEX(SHA2(url, 256))) STORED,
    ADD UNIQUE KEY uk_articles_url_hash (url_hash);
