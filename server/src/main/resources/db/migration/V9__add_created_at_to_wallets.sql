-- wallets tablosuna created_at ekle
ALTER TABLE wallets
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP(6);

-- eski kayıtlar varsa doldur (opsiyonel ama iyi)
UPDATE wallets
SET created_at = NOW()
WHERE created_at IS NULL;
