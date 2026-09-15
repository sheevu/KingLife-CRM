package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// ---------------------------------------------------------------------------
// 1. Visitors & Event Tracking
// ---------------------------------------------------------------------------
@Entity(tableName = "visitors")
data class VisitorEntity(
    @PrimaryKey val visitorId: String,
    val firstVisitEpoch: Long = System.currentTimeMillis(),
    val lastVisitEpoch: Long = System.currentTimeMillis(),
    val numberOfVisits: Int = 1,
    val sessionCount: Int = 1,
    val landingPage: String = "/lucknow-weight-loss",
    val utmSource: String = "google_ads",
    val utmMedium: String = "cpc",
    val utmCampaign: String = "lucknow_gomtinagar_wellness",
    val city: String = "Lucknow",
    val device: String = "Mobile Android",
    val mergedIntoLeadId: String? = null
)

@Entity(tableName = "universal_events")
data class UniversalEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val visitorId: String,
    val leadId: String? = null,
    val customerId: String? = null,
    val eventType: String, // PAGE_VIEW, CTA_CLICK, FORM_START, FORM_STEP_COMPLETE, FORM_SUBMIT, WHATSAPP_CLICK, VISIT_CHECKIN
    val eventDataJson: String = "{}",
    val timestamp: Long = System.currentTimeMillis(),
    val source: String = "Android PWA"
)

// ---------------------------------------------------------------------------
// 2. Database-Driven Services Catalog
// ---------------------------------------------------------------------------
@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slug: String,
    val category: String,
    val shortDescription: String,
    val fullDescription: String,
    val durationMins: Int = 45,
    val priceInr: Double = 999.0,
    val isActive: Boolean = true,
    val displayOrder: Int = 1,
    val whatsappKeyword: String = "weight-loss"
)

// ---------------------------------------------------------------------------
// 3. Leads & Acquisition Funnel
// ---------------------------------------------------------------------------
@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey val id: String,
    val visitorId: String? = null,
    val name: String,
    val phone: String,
    val email: String? = null,
    val ageRange: String = "26-35",
    val preferredLanguage: String = "Hinglish",
    val locality: String = "Gomti Nagar",
    val goal: String = "Weight Management",
    val timeline: String = "Immediately",
    val activityLevel: String = "Moderate",
    val dietPreference: String = "Vegetarian",
    val preferredSlot: String = "Morning (9am - 12pm)",
    val stage: String = "Lead Captured", // Pipeline stage
    val leadScore: Int = 20,
    val assignedCoach: String = "Coach Priya Sharma",
    val utmCampaign: String = "lucknow_gomtinagar_wellness",
    val whatsappClicked: Boolean = false,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

// ---------------------------------------------------------------------------
// 4. Customers & Retention (Customer 360)
// ---------------------------------------------------------------------------
@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val leadId: String? = null,
    val name: String,
    val phone: String,
    val email: String? = null,
    val locality: String = "Hazratganj, Lucknow",
    val assignedCoach: String = "Dr. Ananya Verma",
    val stage: String = "Active Customer", // Active Customer, Review Due, Renewal Due, Renewed, At Risk, Dormant, Win-back
    val totalVisits: Int = 8,
    val visitsThisMonth: Int = 4,
    val missedVisits: Int = 0,
    val currentStreak: Int = 4,
    val lastVisitDate: String = "Today",
    val nextVisitDate: String = "Tomorrow, 10:30 AM",
    val lifetimeValueInr: Double = 14999.0,
    val joinedDate: String = "15 Aug 2026",
    val dietPreference: String = "Vegetarian",
    val programEnrolled: String = "60-Day Metabolic Reset & Fat Loss"
)

// ---------------------------------------------------------------------------
// 5. Appointments & Visit Tracking
// ---------------------------------------------------------------------------
@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val customerOrLeadName: String,
    val phone: String,
    val serviceName: String,
    val coachName: String,
    val appointmentDate: String, // e.g. "15 Sep 2026"
    val timeSlot: String,        // e.g. "11:00 AM - 11:45 AM"
    val status: String = "Scheduled", // Scheduled, Attended, Missed, Cancelled
    val notes: String = "Initial body composition and BMI evaluation"
)

@Entity(tableName = "visits")
data class VisitEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val customerName: String,
    val visitNumber: Int = 1,
    val visitDate: String, // e.g. "15 Sep 2026"
    val visitType: String = "In-Person Consultation",
    val checkInTime: String = "10:15 AM",
    val checkOutTime: String? = "11:00 AM",
    val coachName: String = "Dr. Ananya Verma",
    val serviceName: String = "Weight Loss Review",
    val attendanceStatus: String = "Checked-In", // Checked-In, Completed, Missed
    val notes: String = "Checked weight, energy score improved, diet adjusted",
    val nextVisitDate: String = "18 Sep 2026"
)

// ---------------------------------------------------------------------------
// 6. Wellness Progress Records
// ---------------------------------------------------------------------------
@Entity(tableName = "progress_records")
data class ProgressRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: String,
    val recordDate: String, // e.g. "Week 1", "Week 2", "Week 3", "Week 4"
    val weightKg: Double,
    val waistCm: Double,
    val energyRating: Int = 8, // 1 to 10
    val activityLevel: String = "Moderate Walking 7k steps",
    val adherencePct: Int = 90,
    val coachNotes: String = "Consistent meal timing, steady fat loss observed"
)

// ---------------------------------------------------------------------------
// 7. Staff Tasks & WhatsApp Automations
// ---------------------------------------------------------------------------
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String = "Follow-up", // WhatsApp, Call, Review, Winback, Onboarding
    val dueDate: String = "Today",
    val status: String = "Pending", // Pending, Done
    val priority: String = "High",  // Low, Medium, High
    val assignedTo: String = "Coach Priya",
    val relatedEntityName: String = "Amit Srivastava"
)

@Entity(tableName = "whatsapp_messages")
data class WhatsAppMessageEntity(
    @PrimaryKey val id: String,
    val phone: String,
    val customerName: String,
    val direction: String, // INBOUND, OUTBOUND
    val messageText: String,
    val templateName: String? = null,
    val status: String = "Read", // Sent, Delivered, Read, Failed
    val trackingToken: String? = null,
    val timestampFormatted: String = "10:45 AM"
)

@Entity(tableName = "automation_rules")
data class AutomationRuleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val triggerEvent: String, // lead_created, appointment_missed, customer_inactive_7d, visit_completed
    val conditionSummary: String,
    val actionSummary: String,
    val isActive: Boolean = true
)
