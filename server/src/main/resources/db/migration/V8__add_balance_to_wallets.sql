-- wallets tablosuna balance ekle
ALTER TABLE wallets
ADD COLUMN IF NOT EXISTS balance NUMERIC(18,4) NOT NULL DEFAULT 0.0000;

-- eski kayıtlar varsa null kalmasın
UPDATE wallets
SET balance = 0.0000
WHERE balance IS NULL;
