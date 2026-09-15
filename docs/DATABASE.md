# Database Schema & Data Models
## Healthy King LIFE Weight Loss Wellness Center

### Core Tables & Entities
1. `visitors`: (visitor_id, first_visit, last_visit, session_count, landing_page, utm_source, utm_campaign, city)
2. `leads`: (id, visitor_id, name, phone, email, locality, goal, timeline, diet_preference, stage, lead_score, assigned_coach, created_at)
3. `customers`: (id, lead_id, name, phone, email, locality, assigned_coach, stage, total_visits, missed_visits, current_streak, lifetime_value)
4. `services`: (id, name, slug, category, short_description, duration_mins, price_inr, is_active)
5. `appointments`: (id, customer_or_lead_name, phone, service_name, coach_name, appointment_date, time_slot, status, notes)
6. `visits`: (id, customer_id, customer_name, visit_date, visit_type, check_in_time, check_out_time, coach_name, attendance_status, streak_count)
7. `wellness_profiles`: (id, customer_id, height_cm, target_weight_kg, dietary_restrictions, baseline_energy, health_goals)
8. `progress_records`: (id, customer_id, record_date, weight_kg, waist_cm, energy_rating, adherence_pct, coach_notes)
9. `whatsapp_messages`: (id, phone, direction, message_text, template_name, status, tracking_token, timestamp)
10. `tasks`: (id, title, type, due_date, status, priority, assigned_to, related_id)
11. `automation_rules`: (id, name, trigger, condition_field, condition_value, action_type, action_payload, is_active)
12. `audit_logs`: (id, user_id, action, entity, entity_id, payload, timestamp)
