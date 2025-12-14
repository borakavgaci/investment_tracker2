-- holdings tablosuna avg_cost ekle
ALTER TABLE holdings
ADD COLUMN IF NOT EXISTS avg_cost NUMERIC(18,4) NOT NULL DEFAULT 0.0000;

-- Eski veriler varsa (null kalmış olabilir) garantiye al
UPDATE holdings
SET avg_cost = 0.0000
WHERE avg_cost IS NULL;
