-- ==========================================================
-- Healthy King LIFE Weight Loss Wellness Center - Supabase Schema
-- Market: Lucknow, Uttar Pradesh, India
-- ==========================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Visitors & Universal Events
CREATE TABLE IF NOT EXISTS visitors (
    visitor_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_visit TIMESTAMPTZ DEFAULT NOW(),
    last_visit TIMESTAMPTZ DEFAULT NOW(),
    number_of_visits INT DEFAULT 1,
    session_count INT DEFAULT 1,
    landing_page TEXT,
    utm_source TEXT,
    utm_medium TEXT,
    utm_campaign TEXT,
    city TEXT DEFAULT 'Lucknow',
    device TEXT,
    browser TEXT
);

CREATE TABLE IF NOT EXISTS events (
    event_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visitor_id UUID REFERENCES visitors(visitor_id) ON DELETE SET NULL,
    lead_id UUID,
    customer_id UUID,
    event_type TEXT NOT NULL,
    event_data JSONB DEFAULT '{}'::jsonb,
    timestamp TIMESTAMPTZ DEFAULT NOW(),
    source TEXT DEFAULT 'web'
);

-- 2. Services Catalog (Database Driven)
CREATE TABLE IF NOT EXISTS services (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    slug TEXT UNIQUE NOT NULL,
    category TEXT NOT NULL,
    short_description TEXT,
    full_description TEXT,
    duration_mins INT DEFAULT 45,
    price_inr DECIMAL(10,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 1,
    whatsapp_keyword TEXT
);

-- 3. Leads & Pipeline
CREATE TABLE IF NOT EXISTS leads (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visitor_id UUID REFERENCES visitors(visitor_id) ON DELETE SET NULL,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT,
    age_range TEXT,
    preferred_language TEXT DEFAULT 'English',
    locality TEXT DEFAULT 'Gomti Nagar',
    goal TEXT NOT NULL,
    timeline TEXT DEFAULT 'Immediately',
    activity_level TEXT,
    diet_preference TEXT DEFAULT 'Vegetarian',
    preferred_slot TEXT,
    stage TEXT DEFAULT 'Lead Captured',
    lead_score INT DEFAULT 20,
    assigned_coach TEXT,
    utm_campaign TEXT,
    whatsapp_clicked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. Customers & Wellness Profile
CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    lead_id UUID REFERENCES leads(id) ON DELETE SET NULL,
    name TEXT NOT NULL,
    phone TEXT NOT NULL UNIQUE,
    email TEXT,
    locality TEXT DEFAULT 'Lucknow',
    assigned_coach TEXT,
    stage TEXT DEFAULT 'Active Customer',
    total_visits INT DEFAULT 0,
    missed_visits INT DEFAULT 0,
    current_streak INT DEFAULT 0,
    last_visit_date TIMESTAMPTZ,
    next_visit_date TIMESTAMPTZ,
    lifetime_value DECIMAL(12,2) DEFAULT 0.00,
    joined_date TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS progress_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID REFERENCES customers(id) ON DELETE CASCADE,
    record_date TIMESTAMPTZ DEFAULT NOW(),
    weight_kg DECIMAL(5,2),
    waist_cm DECIMAL(5,2),
    energy_rating INT CHECK (energy_rating BETWEEN 1 AND 10),
    activity_level TEXT,
    adherence_pct INT DEFAULT 85,
    coach_notes TEXT
);

-- 5. Appointments & Visits
CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_or_lead_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    service_id UUID REFERENCES services(id) ON DELETE SET NULL,
    coach_name TEXT,
    appointment_date DATE NOT NULL,
    time_slot TEXT NOT NULL,
    status TEXT DEFAULT 'Scheduled',
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS visits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID REFERENCES customers(id) ON DELETE CASCADE,
    visit_number INT DEFAULT 1,
    visit_date DATE NOT NULL,
    check_in TIMESTAMPTZ DEFAULT NOW(),
    check_out TIMESTAMPTZ,
    coach_name TEXT,
    service_id UUID REFERENCES services(id) ON DELETE SET NULL,
    attendance_status TEXT DEFAULT 'Checked-In',
    notes TEXT,
    next_visit_date DATE
);

-- 6. WhatsApp Tracking & Conversations
CREATE TABLE IF NOT EXISTS whatsapp_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone TEXT NOT NULL,
    direction TEXT NOT NULL CHECK (direction IN ('INBOUND', 'OUTBOUND')),
    message_text TEXT NOT NULL,
    template_name TEXT,
    status TEXT DEFAULT 'Delivered',
    tracking_token TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 7. Tasks & Automations
CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    type TEXT DEFAULT 'Follow-up',
    due_date DATE NOT NULL,
    status TEXT DEFAULT 'Pending',
    priority TEXT DEFAULT 'Medium',
    assigned_to TEXT,
    related_id UUID
);

CREATE TABLE IF NOT EXISTS automation_rules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    trigger_event TEXT NOT NULL,
    condition_field TEXT,
    condition_value TEXT,
    action_type TEXT NOT NULL,
    action_payload JSONB DEFAULT '{}'::jsonb,
    is_active BOOLEAN DEFAULT TRUE
);

-- Initial Services Seed Data
INSERT INTO services (name, slug, category, short_description, duration_mins, price_inr, is_active, display_order) VALUES
('Weight Management Consultation', 'weight-management-consultation', 'Weight Management', 'Personalized body composition analysis, metabolism review, and goal mapping.', 45, 999.00, true, 1),
('Rapid Fat Loss & Toning Programme', 'rapid-fat-loss', 'Weight Management Programmes', 'Targeted weight reduction program with daily meal guidance & coach check-ins.', 60, 4999.00, true, 2),
('Clinical Nutrition & Diet Counselling', 'nutrition-diet-counselling', 'Diet & Nutrition Counselling', 'Customized Indian diet plans (veg, eggetarian, non-veg) for long-term health.', 45, 1499.00, true, 3),
('Lifestyle Coaching & Habit Building', 'lifestyle-coaching', 'Lifestyle Coaching', 'Overcome stress eating, sleep irregularities, and lethargy with behavioral habit loops.', 30, 1199.00, true, 4),
('Personalized Fitness & Mobility Programme', 'fitness-programmes', 'Fitness Programmes', 'Low-impact, joint-friendly, functional fitness designed for sustainable weight loss.', 45, 3499.00, true, 5),
('Metabolic Reset Wellness Programme', 'metabolic-reset', 'Wellness Programmes', 'Revitalize energy levels, detoxify, and optimize gut health.', 60, 5999.00, true, 6);
