# Automation & Retention Rules
## Healthy King LIFE Weight Loss Wellness Center

### 1. Retention Engine Rules
- **At-Risk Trigger**: If a customer has completed 3+ visits in total, but has not logged a check-in for 7 consecutive days:
  - System automatically marks status as `AT_RISK`.
  - Day 7: Automated WhatsApp reminder sent ("Namaste {{name}}, we missed seeing you at Healthy King LIFE Lucknow this week!").
  - Day 10: Task generated for assigned wellness coach to perform telephone follow-up.
  - Day 14: Win-back offer / complimentary body analysis checkup WhatsApp voucher sent.
  - Day 21: High-priority manual manager call scheduled.

### 2. Lead Scoring Rules
- Website visit: +5
- Returning visit: +10
- Service page viewed: +10
- Onboarding form started: +15
- Onboarding completed: +20
- WhatsApp conversation started: +20
- Appointment booked: +25
- Appointment attended: +30
- Deductions:
  - No response for 7 days: -10
  - Missed appointment: -20
  - Inactive 30 days: -20

### 3. Pipeline Automation Matrix
| Trigger | Condition | Actions |
|---|---|---|
| `lead_created` | goal = 'Weight Management' | Assign Coach (Dr. Verma/Coach Priya), Send WhatsApp Welcome, Create Initial Assessment Task |
| `appointment_booked` | slot confirmed | Send WhatsApp Confirmation with Google Maps Lucknow location pin |
| `appointment_missed` | status = 'Missed' | Recalculate lead score (-20), Queue automated reschedule WhatsApp within 2 hours |
| `visit_completed` | attendance = 'Attended' | Update visit count & streak counter, send post-visit hydration/meal reminder |
| `renewal_due` | 7 days remaining | Alert Owner & Coach, Send WhatsApp renewal appreciation bonus |
