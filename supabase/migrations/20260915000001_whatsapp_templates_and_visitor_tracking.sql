-- ==========================================================
-- Healthy King LIFE Weight Loss Wellness Center - Supabase Migration
-- 1. Approved WhatsApp Message Templates & Delivery Tracking
-- 2. Anonymous Visitor Tracking & Identity Stitching
-- 3. Lead & Event Automation Rules
-- ==========================================================

-- 1. WhatsApp Templates Table (Approved Meta Message Templates)
CREATE TABLE IF NOT EXISTS whatsapp_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_name TEXT UNIQUE NOT NULL,
    category TEXT NOT NULL CHECK (category IN ('MARKETING', 'UTILITY', 'AUTHENTICATION')),
    language TEXT NOT NULL DEFAULT 'en',
    status TEXT NOT NULL DEFAULT 'APPROVED' CHECK (status IN ('APPROVED', 'PENDING', 'REJECTED', 'PAUSED')),
    header_type TEXT DEFAULT 'TEXT',
    header_content TEXT,
    body_text TEXT NOT NULL,
    footer_text TEXT DEFAULT 'Healthy King LIFE • Lucknow Wellness Center',
    interactive_type TEXT DEFAULT 'BUTTONS' CHECK (interactive_type IN ('BUTTONS', 'LIST', 'CTA_URL', 'NONE')),
    interactive_buttons JSONB DEFAULT '[]'::jsonb,
    variables JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Index for quick template lookups
CREATE INDEX IF NOT EXISTS idx_whatsapp_templates_name ON whatsapp_templates(template_name);

-- Enhance whatsapp_messages table with delivery status tracking & WhatsApp message ID (wamid)
ALTER TABLE IF EXISTS whatsapp_messages 
    ADD COLUMN IF NOT EXISTS wamid TEXT,
    ADD COLUMN IF NOT EXISTS lead_id UUID REFERENCES leads(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS customer_id UUID REFERENCES customers(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS interactive_type TEXT DEFAULT 'BUTTON',
    ADD COLUMN IF NOT EXISTS interactive_payload JSONB DEFAULT '{}'::jsonb,
    ADD COLUMN IF NOT EXISTS selected_button_id TEXT,
    ADD COLUMN IF NOT EXISTS error_message TEXT,
    ADD COLUMN IF NOT EXISTS delivered_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS read_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT NOW();

CREATE INDEX IF NOT EXISTS idx_whatsapp_messages_wamid ON whatsapp_messages(wamid);
CREATE INDEX IF NOT EXISTS idx_whatsapp_messages_status ON whatsapp_messages(status);
CREATE INDEX IF NOT EXISTS idx_whatsapp_messages_phone ON whatsapp_messages(phone);

-- 2. Enhanced Anonymous Visitor Tracking Tables
ALTER TABLE IF EXISTS visitors
    ADD COLUMN IF NOT EXISTS cookie_id TEXT,
    ADD COLUMN IF NOT EXISTS user_agent TEXT,
    ADD COLUMN IF NOT EXISTS total_time_on_site_seconds INT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS page_views_count INT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS cta_clicks_count INT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS merged_lead_id UUID REFERENCES leads(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS merged_customer_id UUID REFERENCES customers(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS merged_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS is_identified BOOLEAN DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_visitors_cookie_id ON visitors(cookie_id);

CREATE TABLE IF NOT EXISTS visitor_page_views (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visitor_id UUID NOT NULL REFERENCES visitors(visitor_id) ON DELETE CASCADE,
    page_url TEXT NOT NULL,
    page_title TEXT,
    referrer TEXT,
    dwell_time_seconds INT DEFAULT 0,
    utm_source TEXT,
    utm_medium TEXT,
    utm_campaign TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_page_views_visitor ON visitor_page_views(visitor_id);

CREATE TABLE IF NOT EXISTS visitor_cta_clicks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visitor_id UUID NOT NULL REFERENCES visitors(visitor_id) ON DELETE CASCADE,
    lead_id UUID REFERENCES leads(id) ON DELETE SET NULL,
    cta_id TEXT NOT NULL,
    cta_text TEXT,
    cta_destination TEXT,
    page_url TEXT,
    click_timestamp TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_cta_clicks_visitor ON visitor_cta_clicks(visitor_id);

-- 3. Identity Stitching & Visitor Data Merging Stored Procedure
CREATE OR REPLACE FUNCTION merge_visitor_data(
    p_visitor_id UUID,
    p_lead_id UUID,
    p_phone TEXT
) RETURNS JSONB AS $$
DECLARE
    v_page_count INT;
    v_cta_count INT;
    v_total_time INT;
    v_score_boost INT := 0;
BEGIN
    -- Calculate historical stats from anonymous visitor
    SELECT COUNT(*), COALESCE(SUM(dwell_time_seconds), 0)
    INTO v_page_count, v_total_time
    FROM visitor_page_views
    WHERE visitor_id = p_visitor_id;

    SELECT COUNT(*)
    INTO v_cta_count
    FROM visitor_cta_clicks
    WHERE visitor_id = p_visitor_id;

    -- Calculate lead score boost: +5 per page view, +10 per CTA click, +10 if time > 60s
    v_score_boost := (v_page_count * 5) + (v_cta_count * 10);
    IF v_total_time >= 60 THEN
        v_score_boost := v_score_boost + 10;
    END IF;

    -- Update the visitor record as merged
    UPDATE visitors
    SET merged_lead_id = p_lead_id,
        merged_at = NOW(),
        is_identified = TRUE,
        page_views_count = v_page_count,
        cta_clicks_count = v_cta_count,
        total_time_on_site_seconds = v_total_time
    WHERE visitor_id = p_visitor_id;

    -- Stitch historical events and CTA clicks to the new lead
    UPDATE events
    SET lead_id = p_lead_id
    WHERE visitor_id = p_visitor_id;

    UPDATE visitor_cta_clicks
    SET lead_id = p_lead_id
    WHERE visitor_id = p_visitor_id;

    -- Boost lead score and link visitor_id in leads table
    UPDATE leads
    SET visitor_id = p_visitor_id,
        lead_score = lead_score + v_score_boost,
        updated_at = NOW()
    WHERE id = p_lead_id;

    -- Log an identity merge audit event
    INSERT INTO events (visitor_id, lead_id, event_type, event_data, source)
    VALUES (
        p_visitor_id,
        p_lead_id,
        'identity_stitched',
        jsonb_build_object(
            'phone', p_phone,
            'historical_page_views', v_page_count,
            'historical_cta_clicks', v_cta_count,
            'total_dwell_seconds', v_total_time,
            'lead_score_boost', v_score_boost
        ),
        'supabase_rpc'
    );

    RETURN jsonb_build_object(
        'success', true,
        'visitor_id', p_visitor_id,
        'lead_id', p_lead_id,
        'score_boost', v_score_boost,
        'stitched_events_count', v_page_count + v_cta_count
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 4. Automatic Lead Automation Trigger (Welcome WhatsApp + Interactive Buttons)
CREATE OR REPLACE FUNCTION trigger_welcome_whatsapp_on_lead()
RETURNS TRIGGER AS $$
DECLARE
    v_template RECORD;
    v_msg_id UUID;
    v_tracking_token TEXT;
    v_welcome_text TEXT;
BEGIN
    -- Only trigger on new leads or when stage changes to 'WhatsApp Started'
    IF (TG_OP = 'INSERT') OR (TG_OP = 'UPDATE' AND NEW.stage = 'WhatsApp Started' AND OLD.stage <> 'WhatsApp Started') THEN
        
        -- Generate unique tracking token
        v_tracking_token := 'wa_tk_' || SUBSTRING(NEW.id::text, 1, 8);

        -- Fetch approved welcome template
        SELECT * INTO v_template
        FROM whatsapp_templates
        WHERE template_name = 'welcome_wellness_lucknow'
        LIMIT 1;

        -- Prepare personalized body
        v_welcome_text := 'Namaste ' || NEW.name || ' Ji! 🙏 Welcome to Healthy King LIFE Lucknow. We have reserved your personalized wellness evaluation for ' || NEW.goal || ' at Gomti Nagar.';

        -- Insert queued outbound WhatsApp message with interactive buttons
        INSERT INTO whatsapp_messages (
            lead_id,
            phone,
            direction,
            message_text,
            template_name,
            status,
            tracking_token,
            interactive_type,
            interactive_payload
        ) VALUES (
            NEW.id,
            NEW.phone,
            'OUTBOUND',
            v_welcome_text,
            'welcome_wellness_lucknow',
            'queued',
            v_tracking_token,
            'BUTTON',
            jsonb_build_object(
                'buttons', jsonb_build_array(
                    jsonb_build_object('id', 'book_eval', 'title', 'Book Evaluation'),
                    jsonb_build_object('id', 'learn_more', 'title', 'Learn More'),
                    jsonb_build_object('id', 'call_center', 'title', 'Call Lucknow Center')
                )
            )
        ) RETURNING id INTO v_msg_id;

        -- Log event
        INSERT INTO events (lead_id, event_type, event_data, source)
        VALUES (
            NEW.id,
            'whatsapp_welcome_queued',
            jsonb_build_object(
                'message_id', v_msg_id,
                'tracking_token', v_tracking_token,
                'phone', NEW.phone,
                'template', 'welcome_wellness_lucknow'
            ),
            'database_trigger'
        );

    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS trg_lead_welcome_whatsapp ON leads;
CREATE TRIGGER trg_lead_welcome_whatsapp
    AFTER INSERT OR UPDATE ON leads
    FOR EACH ROW
    EXECUTE FUNCTION trigger_welcome_whatsapp_on_lead();

-- 5. Seed Approved WhatsApp Message Templates
INSERT INTO whatsapp_templates (
    template_name,
    category,
    language,
    status,
    header_type,
    header_content,
    body_text,
    footer_text,
    interactive_type,
    interactive_buttons,
    variables
) VALUES 
(
    'welcome_wellness_lucknow',
    'UTILITY',
    'en',
    'APPROVED',
    'TEXT',
    'Healthy King LIFE Lucknow',
    'Namaste {{1}} Ji! 🙏 Welcome to Healthy King LIFE Weight Loss Wellness Center, Lucknow. We received your request for {{2}}. Our clinical nutrition & wellness coaches at Gomti Nagar are excited to help you achieve lasting vitality.\n\nPlease select your preferred next step below:',
    'Gomti Nagar • Lucknow • Healthy King LIFE',
    'BUTTONS',
    '[
        {"id": "book_eval", "title": "Book Evaluation"},
        {"id": "learn_more", "title": "Learn More"},
        {"id": "talk_coach", "title": "Talk to Coach"}
    ]'::jsonb,
    '["client_name", "wellness_goal"]'::jsonb
),
(
    'whatsapp_started_welcome',
    'MARKETING',
    'en',
    'APPROVED',
    'TEXT',
    'Lucknow Wellness Center',
    'Namaste {{1}} Ji! 🙏 Thank you for reaching out to Healthy King LIFE on WhatsApp. We have mapped out special evaluation slots for Gomti Nagar and Hazratganj residents today.\n\nWould you like to reserve your 45-minute Body Composition & Metabolism Analysis slot?',
    'Healthy King LIFE Lucknow • +91 94150 12345',
    'BUTTONS',
    '[
        {"id": "book_eval", "title": "Book Evaluation"},
        {"id": "view_pricing", "title": "View Pricing"},
        {"id": "center_location", "title": "Center Location"}
    ]'::jsonb,
    '["client_name"]'::jsonb
),
(
    'appointment_confirmed_lucknow',
    'UTILITY',
    'en',
    'APPROVED',
    'TEXT',
    'Appointment Confirmed',
    'Great news {{1}} Ji! Your in-center consultation for {{2}} is confirmed for {{3}} at {{4}} with {{5}}.\n\n📍 Location: Healthy King LIFE Center, 2nd Floor, Gomti Nagar Commercial Hub, Lucknow.',
    'Please arrive 10 mins prior for body composition test',
    'BUTTONS',
    '[
        {"id": "get_directions", "title": "Get Directions"},
        {"id": "reschedule_slot", "title": "Reschedule"}
    ]'::jsonb,
    '["client_name", "service_name", "date", "time", "coach_name"]'::jsonb
),
(
    'retention_streak_nudge',
    'UTILITY',
    'hi',
    'APPROVED',
    'TEXT',
    'Healthy King LIFE Nudge',
    'Namaste {{1}} Ji! 🙏 Humne notice kiya ki pichle 7 dinon se aapka center visit miss hua hai. Consistent progress ke liye aapka follow-up session zaroori hai. Kya hum aapka slot reserve karein?',
    'Aapki health, hamari priority • Healthy King LIFE',
    'BUTTONS',
    '[
        {"id": "book_eval", "title": "Slot Book Karein"},
        {"id": "call_coach", "title": "Coach Se Baat Karein"}
    ]'::jsonb,
    '["client_name"]'::jsonb
)
ON CONFLICT (template_name) DO UPDATE 
SET status = 'APPROVED',
    interactive_buttons = EXCLUDED.interactive_buttons,
    body_text = EXCLUDED.body_text;
