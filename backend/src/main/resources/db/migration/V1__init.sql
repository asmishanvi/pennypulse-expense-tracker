CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(12,2) NOT NULL,
    category VARCHAR(64) NOT NULL,
    note TEXT,
    expense_date DATE NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE budgets (
    month VARCHAR(7) PRIMARY KEY,
    amount NUMERIC(12,2) NOT NULL
);

CREATE TABLE recurring_expenses (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(12,2) NOT NULL,
    category VARCHAR(64) NOT NULL,
    note TEXT,
    day_of_month INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE recurring_applied (
    id BIGSERIAL PRIMARY KEY,
    recurring_id BIGINT NOT NULL,
    month VARCHAR(7) NOT NULL,
    UNIQUE(recurring_id, month)
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    emoji VARCHAR(16) NOT NULL
);
