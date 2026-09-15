# System Architecture
## Healthy King LIFE Weight Loss Wellness Center

### 1. Hybrid Multi-Client Architecture
- **Mobile Native Runtime**: Kotlin + Jetpack Compose + Android Architecture Components (ViewModel, Coroutines, StateFlow, Room Database for instant offline CRM and check-in access).
- **Backend & Cloud Services**: Supabase (PostgreSQL, Row Level Security, Realtime subscriptions, Auth, Edge Functions, Storage).
- **Web / PWA Compatibility**: React + Vite + TypeScript + Tailwind CSS + shadcn/ui deployable to Cloudflare Pages & Workers, packaged for Android via native wrapper / Capacitor.
- **WhatsApp Cloud API Integration**: Direct Meta webhook ingress with HMAC verification, automated interactive message rendering, tracking token generator (`/wa/[service]?lead=[id]`), and dispatch queue.

### 2. Event Driven Tracking
- Anonymous visitors receive persistent `visitor_id`.
- Every CTA click, service view, form step, and WhatsApp tap logs an immutable event with session and UTM context.
- Identity stitch: when a visitor provides a phone number (+91), historical anonymous events merge into the new lead record.

### 3. Security & Access Boundaries
- **RBAC**: Super Admin, Owner, Center Manager, Coach, Reception, Marketing, Finance, Read Only.
- **Least Privilege**:
  - Receptionists see appointments and check-in status, not private coach notes.
  - Marketing accesses acquisition cohorts, not sensitive physiological measurements.
  - Coaches see assigned clients and their wellness logs.
  - Owners and Managers have complete visibility.
