CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- USERS
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(100) NOT NULL,
  surname VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  is_admin BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- STOCKS
CREATE TABLE IF NOT EXISTS stocks (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  symbol VARCHAR(20) NOT NULL UNIQUE,
  company_name VARCHAR(255) NOT NULL,
  current_value NUMERIC(18, 4) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  country VARCHAR(100) NOT NULL
);

-- STOCK VALUE HISTORY
CREATE TABLE IF NOT EXISTS stocks_value_history (
  stock_id UUID NOT NULL,
  date TIMESTAMP NOT NULL,
  value NUMERIC(18, 4) NOT NULL,
  currency VARCHAR(10) NOT NULL,
  PRIMARY KEY (stock_id, date),
  CONSTRAINT fk_history_stock
    FOREIGN KEY (stock_id) REFERENCES stocks(id)
    ON DELETE CASCADE
);

-- WALLETS
CREATE TABLE IF NOT EXISTS wallets (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL UNIQUE,
  free_balance NUMERIC(18, 4) NOT NULL DEFAULT 0,
  last_updated TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_wallet_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
);

-- WALLET TRANSACTIONS
CREATE TABLE IF NOT EXISTS wallet_transactions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  wallet_id UUID NOT NULL,
  type VARCHAR(30) NOT NULL,
  amount NUMERIC(18, 4) NOT NULL CHECK (amount > 0),
  transaction_date TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_wallet_tx_wallet
    FOREIGN KEY (wallet_id) REFERENCES wallets(id)
    ON DELETE CASCADE
);

-- HOLDINGS (composite PK)
CREATE TABLE IF NOT EXISTS holdings (
  user_id UUID NOT NULL,
  stock_id UUID NOT NULL,
  quantity NUMERIC(18, 4) NOT NULL DEFAULT 0,
  purchase_price NUMERIC(18, 4) NOT NULL DEFAULT 0,
  purchase_date DATE,
  PRIMARY KEY (user_id, stock_id),
  CONSTRAINT fk_holdings_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_holdings_stock
    FOREIGN KEY (stock_id) REFERENCES stocks(id)
    ON DELETE CASCADE
);

-- TRADES enum + table
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'trade_type') THEN
    CREATE TYPE trade_type AS ENUM ('BUY', 'SELL');
  END IF;
END$$;

CREATE TABLE IF NOT EXISTS trades (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  stock_id UUID NOT NULL,
  wallet_id UUID NOT NULL,
  type trade_type NOT NULL,
  quantity NUMERIC(18, 4) NOT NULL CHECK (quantity > 0),
  price NUMERIC(18, 4) NOT NULL CHECK (price >= 0),
  trade_datetime TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_trades_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_trades_stock
    FOREIGN KEY (stock_id) REFERENCES stocks(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_trades_wallet
    FOREIGN KEY (wallet_id) REFERENCES wallets(id)
    ON DELETE CASCADE
);
