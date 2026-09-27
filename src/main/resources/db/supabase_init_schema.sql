-- ==============================================================================
-- AZANI INTERNET SERVICE PROVIDER INFORMATION SYSTEM
-- Safe Supabase (PostgreSQL) DDL Schema (CREATE TABLE IF NOT EXISTS)
-- Will NOT drop or delete existing user data on application restart!
-- ==============================================================================

-- 1. Learning Institutions Table
CREATE TABLE IF NOT EXISTS institutions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(30) NOT NULL, -- PRIMARY_SCHOOL, JUNIOR_SCHOOL, SENIOR_SCHOOL, COLLEGE
    physical_address VARCHAR(255) NOT NULL,
    registration_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, DEFAULTER, DISCONNECTED, PENDING_INSTALLATION
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Contact Person Details Table
CREATE TABLE IF NOT EXISTS contact_persons (
    id SERIAL PRIMARY KEY,
    institution_id INT NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    designation VARCHAR(80) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(100) NOT NULL,
    national_id VARCHAR(50) NOT NULL,
    CONSTRAINT fk_contact_institution FOREIGN KEY (institution_id) 
        REFERENCES institutions(id) ON DELETE CASCADE
);

-- 3. Infrastructure & Readiness Assessment Table
CREATE TABLE IF NOT EXISTS infrastructure_requirements (
    id SERIAL PRIMARY KEY,
    institution_id INT NOT NULL UNIQUE,
    num_users INT NOT NULL DEFAULT 0,
    is_ready BOOLEAN NOT NULL DEFAULT FALSE,
    pcs_purchased INT NOT NULL DEFAULT 0,
    lan_nodes INT NOT NULL DEFAULT 0,
    pc_cost DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    lan_cost DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    base_installation_fee DECIMAL(12,2) NOT NULL DEFAULT 10000.00,
    total_installation_cost DECIMAL(12,2) NOT NULL DEFAULT 10000.00,
    CONSTRAINT fk_infra_institution FOREIGN KEY (institution_id) 
        REFERENCES institutions(id) ON DELETE CASCADE
);

-- 4. Internet Service Subscriptions Table
CREATE TABLE IF NOT EXISTS subscriptions (
    id SERIAL PRIMARY KEY,
    institution_id INT NOT NULL UNIQUE,
    bandwidth_mbps INT NOT NULL, -- 4, 10, 20, 25, 50
    base_monthly_cost DECIMAL(12,2) NOT NULL,
    is_upgraded BOOLEAN NOT NULL DEFAULT FALSE,
    discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    final_monthly_cost DECIMAL(12,2) NOT NULL,
    start_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_sub_institution FOREIGN KEY (institution_id) 
        REFERENCES institutions(id) ON DELETE CASCADE
);

-- 5. Monthly Bills Table
CREATE TABLE IF NOT EXISTS bills (
    id SERIAL PRIMARY KEY,
    institution_id INT NOT NULL,
    billing_month INT NOT NULL,
    billing_year INT NOT NULL,
    base_charge DECIMAL(12,2) NOT NULL,
    overdue_fine DECIMAL(12,2) NOT NULL DEFAULT 0.00, -- 15% surcharge if overdue
    reconnection_fee DECIMAL(12,2) NOT NULL DEFAULT 0.00, -- KSh 1,000 surcharge
    total_due DECIMAL(12,2) NOT NULL,
    amount_paid DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    due_date DATE NOT NULL,
    disconnection_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, PAID, OVERDUE, DISCONNECTED
    CONSTRAINT fk_bill_institution FOREIGN KEY (institution_id) 
        REFERENCES institutions(id) ON DELETE CASCADE
);

-- 6. Captured Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    institution_id INT NOT NULL,
    payment_type VARCHAR(40) NOT NULL, -- REGISTRATION, INSTALLATION, MONTHLY, RECONNECTION
    amount DECIMAL(12,2) NOT NULL,
    payment_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    reference_no VARCHAR(60) NOT NULL,
    notes VARCHAR(255),
    CONSTRAINT fk_payment_institution FOREIGN KEY (institution_id) 
        REFERENCES institutions(id) ON DELETE CASCADE
);

-- Create Indexes for fast querying
CREATE INDEX IF NOT EXISTS idx_institutions_category ON institutions(category);
CREATE INDEX IF NOT EXISTS idx_bills_status ON bills(status);
CREATE INDEX IF NOT EXISTS idx_payments_institution ON payments(institution_id);
