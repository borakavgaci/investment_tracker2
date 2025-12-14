-- holdings tablosuna created_at ekle
ALTER TABLE holdings
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP(6);

-- eski kayıtlar varsa boş kalmasın istersen doldur (opsiyonel)
UPDATE holdings
SET created_at = NOW()
WHERE created_at IS NULL;
