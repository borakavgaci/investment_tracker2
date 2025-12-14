-- DE (Germany) örnekleri
INSERT INTO stocks (id, symbol, company_name, current_value, created_at, country)
VALUES
  (gen_random_uuid(), 'SAP.DE', 'SAP SE', 100.0000, now(), 'DE'),
  (gen_random_uuid(), 'SIE.DE', 'Siemens AG', 150.0000, now(), 'DE'),
  (gen_random_uuid(), 'ALV.DE', 'Allianz SE', 200.0000, now(), 'DE')
ON CONFLICT (symbol) DO NOTHING;

-- GB (United Kingdom) örnekleri
INSERT INTO stocks (id, symbol, company_name, current_value, created_at, country)
VALUES
  (gen_random_uuid(), 'HSBA.L', 'HSBC Holdings plc', 50.0000, now(), 'GB'),
  (gen_random_uuid(), 'BP.L',   'BP p.l.c.', 35.0000, now(), 'GB'),
  (gen_random_uuid(), 'AZN.L',  'AstraZeneca plc', 120.0000, now(), 'GB')
ON CONFLICT (symbol) DO NOTHING;

-- CA (Canada) örnekleri
INSERT INTO stocks (id, symbol, company_name, current_value, created_at, country)
VALUES
  (gen_random_uuid(), 'RY.TO', 'Royal Bank of Canada', 110.0000, now(), 'CA'),
  (gen_random_uuid(), 'TD.TO', 'Toronto-Dominion Bank', 95.0000, now(), 'CA'),
  (gen_random_uuid(), 'SHOP.TO', 'Shopify Inc.', 70.0000, now(), 'CA')
ON CONFLICT (symbol) DO NOTHING;

ALTER TABLE stocks
ADD COLUMN IF NOT EXISTS last_price_updated_at TIMESTAMP NULL;
