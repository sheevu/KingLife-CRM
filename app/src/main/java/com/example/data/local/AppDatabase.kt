package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppointmentEntity
import com.example.data.model.AutomationRuleEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.LeadEntity
import com.example.data.model.ProgressRecordEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UniversalEventEntity
import com.example.data.model.VisitEntity
import com.example.data.model.VisitorEntity
import com.example.data.model.WhatsAppMessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        VisitorEntity::class,
        UniversalEventEntity::class,
        ServiceEntity::class,
        LeadEntity::class,
        CustomerEntity::class,
        AppointmentEntity::class,
        VisitEntity::class,
        ProgressRecordEntity::class,
        TaskEntity::class,
        WhatsAppMessageEntity::class,
        AutomationRuleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wellnessDao(): WellnessDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthy_king_life_wellness_crm.db"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.wellnessDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: WellnessDao) {
                // 1. Initial 12 Services Catalog (Database Driven per prompt)
                val services = listOf(
                    ServiceEntity(
                        id = "srv-1",
                        name = "Weight Management Consultation",
                        slug = "weight-management-consultation",
                        category = "Weight Management",
                        shortDescription = "Comprehensive body composition analysis, metabolism scan, and custom weight target setting.",
                        fullDescription = "Our clinical wellness coaches in Lucknow evaluate fat percentage, visceral fat, muscle mass, and baseline hydration to tailor a sustainable weight loss plan.",
                        durationMins = 45,
                        priceInr = 999.0,
                        isActive = true,
                        displayOrder = 1,
                        whatsappKeyword = "weight-management-consult"
                    ),
                    ServiceEntity(
                        id = "srv-2",
                        name = "Clinical Diet & Nutrition Counselling",
                        slug = "nutrition-counselling",
                        category = "Diet & Nutrition Counselling",
                        shortDescription = "Customized Awadhi & North Indian nutrition plans tailored for Vegetarian, Eggetarian, and Non-Veg.",
                        fullDescription = "Evidence-based meal restructuring without starving. High protein, balanced glycemic index, and locally sourced Lucknow produce.",
                        durationMins = 45,
                        priceInr = 1499.0,
                        isActive = true,
                        displayOrder = 2,
                        whatsappKeyword = "diet-plan"
                    ),
                    ServiceEntity(
                        id = "srv-3",
                        name = "Rapid Fat Loss & Toning Programme",
                        slug = "rapid-fat-loss",
                        category = "Weight Management Programmes",
                        shortDescription = "Intensive 60-day guided program with weekly progress scans and coach accountability.",
                        fullDescription = "Achieve 4-8 kg healthy fat reduction with metabolic reset routines, daily WhatsApp meal checks, and posture toning.",
                        durationMins = 60,
                        priceInr = 4999.0,
                        isActive = true,
                        displayOrder = 3,
                        whatsappKeyword = "fat-loss-program"
                    ),
                    ServiceEntity(
                        id = "srv-4",
                        name = "Personal Training & Core Fitness",
                        slug = "personal-training",
                        category = "Personal Training",
                        shortDescription = "1-on-1 guided functional fitness routines adapted to your joint health and mobility level.",
                        fullDescription = "Safe, high-burn functional workouts engineered to protect knees and lower back while accelerating thermogenesis.",
                        durationMins = 50,
                        priceInr = 3499.0,
                        isActive = true,
                        displayOrder = 4,
                        whatsappKeyword = "fitness-training"
                    ),
                    ServiceEntity(
                        id = "srv-5",
                        name = "Lifestyle Coaching & Habit Loops",
                        slug = "lifestyle-coaching",
                        category = "Lifestyle Coaching",
                        shortDescription = "Overcome late-night cravings, work stress, sleep deprivation, and sedentary habits.",
                        fullDescription = "Behavioral coaching sessions addressing root cause habits, stress management, and hydration routines.",
                        durationMins = 30,
                        priceInr = 1299.0,
                        isActive = true,
                        displayOrder = 5,
                        whatsappKeyword = "lifestyle-coach"
                    ),
                    ServiceEntity(
                        id = "srv-6",
                        name = "Metabolic Reset Wellness Programme",
                        slug = "metabolic-reset",
                        category = "Wellness Programmes",
                        shortDescription = "Restore cellular energy, optimize digestion, and balance metabolic markers naturally.",
                        fullDescription = "A 30-day guided holistic journey targeting lethargy, insulin sensitivity, and persistent weight plateaus.",
                        durationMins = 60,
                        priceInr = 5999.0,
                        isActive = true,
                        displayOrder = 6,
                        whatsappKeyword = "metabolic-wellness"
                    )
                )
                dao.insertServices(services)

                // 2. Initial Lucknow Leads across Funnel
                val leads = listOf(
                    LeadEntity(
                        id = "lead-101",
                        name = "Amitabh Srivastava",
                        phone = "+91 94150 28419",
                        email = "amitabh.s@gmail.com",
                        ageRange = "36-45",
                        preferredLanguage = "Hindi",
                        locality = "Gomti Nagar, Lucknow",
                        goal = "Weight Management",
                        timeline = "Immediately",
                        activityLevel = "Sedentary",
                        dietPreference = "Vegetarian",
                        preferredSlot = "Morning (9am - 12pm)",
                        stage = "Appointment Scheduled",
                        leadScore = 80,
                        assignedCoach = "Dr. Ananya Verma",
                        whatsappClicked = true
                    ),
                    LeadEntity(
                        id = "lead-102",
                        name = "Sneha Rastogi",
                        phone = "+91 98390 11422",
                        email = "sneha.rastogi@outlook.com",
                        ageRange = "26-35",
                        preferredLanguage = "Hinglish",
                        locality = "Hazratganj, Lucknow",
                        goal = "Nutrition & Diet",
                        timeline = "This Week",
                        activityLevel = "Moderate",
                        dietPreference = "Eggetarian",
                        preferredSlot = "Evening (4pm - 7pm)",
                        stage = "WhatsApp Started",
                        leadScore = 65,
                        assignedCoach = "Coach Priya Sharma",
                        whatsappClicked = true
                    ),
                    LeadEntity(
                        id = "lead-103",
                        name = "Rajeshwar Pandey",
                        phone = "+91 87654 99120",
                        email = "rpandey.lko@yahoo.co.in",
                        ageRange = "46-55",
                        preferredLanguage = "English",
                        locality = "Aliganj, Lucknow",
                        goal = "Energy & Active Lifestyle",
                        timeline = "This Month",
                        activityLevel = "Light Walking",
                        dietPreference = "Vegetarian",
                        preferredSlot = "Morning (9am - 12pm)",
                        stage = "Lead Captured",
                        leadScore = 40,
                        assignedCoach = "Coach Priya Sharma",
                        whatsappClicked = false
                    ),
                    LeadEntity(
                        id = "lead-104",
                        name = "Pooja Trivedi",
                        phone = "+91 91294 33018",
                        email = "pooja.trivedi@gmail.com",
                        ageRange = "26-35",
                        preferredLanguage = "Hinglish",
                        locality = "Indira Nagar, Lucknow",
                        goal = "Weight Management",
                        timeline = "Immediately",
                        activityLevel = "Moderate",
                        dietPreference = "Vegetarian",
                        preferredSlot = "Evening (4pm - 7pm)",
                        stage = "Consultation Completed",
                        leadScore = 90,
                        assignedCoach = "Dr. Ananya Verma",
                        whatsappClicked = true
                    )
                )
                leads.forEach { dao.insertLead(it) }

                // 3. Initial Customers for Customer 360 & Retention
                val customers = listOf(
                    CustomerEntity(
                        id = "cust-201",
                        leadId = "lead-100",
                        name = "Dr. Neha Agarwal",
                        phone = "+91 94500 81234",
                        email = "neha.agarwal.dr@gmail.com",
                        locality = "Gomti Nagar Phase 2, Lucknow",
                        assignedCoach = "Dr. Ananya Verma",
                        stage = "Active Customer",
                        totalVisits = 12,
                        visitsThisMonth = 5,
                        missedVisits = 0,
                        currentStreak = 6,
                        lastVisitDate = "14 Sep 2026",
                        nextVisitDate = "17 Sep 2026, 10:00 AM",
                        lifetimeValueInr = 18499.0,
                        joinedDate = "01 Jul 2026",
                        dietPreference = "Vegetarian",
                        programEnrolled = "90-Day Complete Metabolic Transformation"
                    ),
                    CustomerEntity(
                        id = "cust-202",
                        leadId = "lead-099",
                        name = "Vikramaditya Singh",
                        phone = "+91 98891 76543",
                        email = "vikram.singh@gmail.com",
                        locality = "Mahanagar, Lucknow",
                        assignedCoach = "Coach Priya Sharma",
                        stage = "At Risk",
                        totalVisits = 5,
                        visitsThisMonth = 0,
                        missedVisits = 2,
                        currentStreak = 0,
                        lastVisitDate = "04 Sep 2026",
                        nextVisitDate = "Overdue (Last visit 11 days ago)",
                        lifetimeValueInr = 7999.0,
                        joinedDate = "10 Aug 2026",
                        dietPreference = "Non-Vegetarian",
                        programEnrolled = "30-Day Lean Muscle & Fat Cut"
                    ),
                    CustomerEntity(
                        id = "cust-203",
                        leadId = "lead-098",
                        name = "Sunita Mehrotra",
                        phone = "+91 97932 44556",
                        email = "sunita.m@gmail.com",
                        locality = "Hazratganj, Lucknow",
                        assignedCoach = "Dr. Ananya Verma",
                        stage = "Renewal Due",
                        totalVisits = 22,
                        visitsThisMonth = 6,
                        missedVisits = 1,
                        currentStreak = 4,
                        lastVisitDate = "13 Sep 2026",
                        nextVisitDate = "16 Sep 2026, 11:30 AM",
                        lifetimeValueInr = 29999.0,
                        joinedDate = "15 May 2026",
                        dietPreference = "Vegetarian",
                        programEnrolled = "Gold Wellness & Maintenance Journey"
                    )
                )
                customers.forEach { dao.insertCustomer(it) }

                // 4. Progress records for Dr. Neha Agarwal
                val progressRecords = listOf(
                    ProgressRecordEntity(
                        customerId = "cust-201",
                        recordDate = "Start (Jul 01)",
                        weightKg = 82.4,
                        waistCm = 96.0,
                        energyRating = 4,
                        activityLevel = "Light (3k steps)",
                        adherencePct = 70,
                        coachNotes = "Baseline assessment completed at Gomti Nagar center. High motivation."
                    ),
                    ProgressRecordEntity(
                        customerId = "cust-201",
                        recordDate = "Week 4 (Aug 01)",
                        weightKg = 79.1,
                        waistCm = 92.5,
                        energyRating = 6,
                        activityLevel = "Moderate (6k steps)",
                        adherencePct = 85,
                        coachNotes = "Initial fat loss milestone reached (-3.3 kg). Digestion normalized."
                    ),
                    ProgressRecordEntity(
                        customerId = "cust-201",
                        recordDate = "Week 8 (Sep 01)",
                        weightKg = 76.2,
                        waistCm = 89.0,
                        energyRating = 8,
                        activityLevel = "Active (8k steps + Yoga)",
                        adherencePct = 92,
                        coachNotes = "Energy levels excellent. Inches dropping consistently without hunger."
                    ),
                    ProgressRecordEntity(
                        customerId = "cust-201",
                        recordDate = "Latest (Sep 14)",
                        weightKg = 74.8,
                        waistCm = 87.2,
                        energyRating = 9,
                        activityLevel = "Energetic & Consistent",
                        adherencePct = 95,
                        coachNotes = "Total loss: -7.6 kg. Patient feeling youthful, posture and endurance transformed."
                    )
                )
                progressRecords.forEach { dao.insertProgressRecord(it) }

                // 5. Today's Appointments
                val appointments = listOf(
                    AppointmentEntity(
                        id = "apt-301",
                        customerOrLeadName = "Amitabh Srivastava",
                        phone = "+91 94150 28419",
                        serviceName = "Weight Management Consultation",
                        coachName = "Dr. Ananya Verma",
                        appointmentDate = "Today",
                        timeSlot = "10:30 AM - 11:15 AM",
                        status = "Scheduled",
                        notes = "In-person Lucknow evaluation for belly fat reduction"
                    ),
                    AppointmentEntity(
                        id = "apt-302",
                        customerOrLeadName = "Dr. Neha Agarwal",
                        phone = "+91 94500 81234",
                        serviceName = "Bi-Weekly Progress Review",
                        coachName = "Dr. Ananya Verma",
                        appointmentDate = "Today",
                        timeSlot = "11:30 AM - 12:00 PM",
                        status = "Attended",
                        notes = "Progress review and maintenance renewal consultation"
                    ),
                    AppointmentEntity(
                        id = "apt-303",
                        customerOrLeadName = "Vikramaditya Singh",
                        phone = "+91 98891 76543",
                        serviceName = "Re-engagement Consultation",
                        coachName = "Coach Priya Sharma",
                        appointmentDate = "Today",
                        timeSlot = "04:30 PM - 05:00 PM",
                        status = "Missed",
                        notes = "Follow-up after 10-day inactivity alert"
                    )
                )
                appointments.forEach { dao.insertAppointment(it) }

                // 6. Recent Visits
                val visits = listOf(
                    VisitEntity(
                        id = "vst-401",
                        customerId = "cust-201",
                        customerName = "Dr. Neha Agarwal",
                        visitNumber = 12,
                        visitDate = "14 Sep 2026",
                        visitType = "Progress Review",
                        checkInTime = "11:28 AM",
                        checkOutTime = "12:05 PM",
                        coachName = "Dr. Ananya Verma",
                        serviceName = "Weight Loss Review",
                        attendanceStatus = "Completed",
                        notes = "Weight checked (74.8 kg), waist 87.2 cm. Very positive.",
                        nextVisitDate = "17 Sep 2026"
                    ),
                    VisitEntity(
                        id = "vst-402",
                        customerId = "cust-203",
                        customerName = "Sunita Mehrotra",
                        visitNumber = 22,
                        visitDate = "13 Sep 2026",
                        visitType = "Maintenance Session",
                        checkInTime = "04:10 PM",
                        checkOutTime = "04:55 PM",
                        coachName = "Dr. Ananya Verma",
                        serviceName = "Gold Wellness",
                        attendanceStatus = "Completed",
                        notes = "Discussed renewal discount package.",
                        nextVisitDate = "16 Sep 2026"
                    )
                )
                visits.forEach { dao.insertVisit(it) }

                // 7. Tasks
                val tasks = listOf(
                    TaskEntity(
                        id = "tsk-501",
                        title = "WhatsApp Win-back reminder to Vikramaditya (7d inactive)",
                        type = "Winback",
                        dueDate = "Today",
                        status = "Pending",
                        priority = "High",
                        assignedTo = "Coach Priya Sharma",
                        relatedEntityName = "Vikramaditya Singh"
                    ),
                    TaskEntity(
                        id = "tsk-502",
                        title = "Prepare renewal proposal for Sunita Mehrotra",
                        type = "Review",
                        dueDate = "Today",
                        status = "Pending",
                        priority = "Medium",
                        assignedTo = "Dr. Ananya Verma",
                        relatedEntityName = "Sunita Mehrotra"
                    ),
                    TaskEntity(
                        id = "tsk-503",
                        title = "Confirm tomorrow's 10am Lucknow appointment with Sneha",
                        type = "WhatsApp",
                        dueDate = "Tomorrow",
                        status = "Pending",
                        priority = "High",
                        assignedTo = "Receptionist Ritu",
                        relatedEntityName = "Sneha Rastogi"
                    )
                )
                tasks.forEach { dao.insertTask(it) }

                // 8. Automation Rules
                val automations = listOf(
                    AutomationRuleEntity(
                        id = "aut-601",
                        name = "New Lead WhatsApp Welcome & Coach Assignment",
                        triggerEvent = "lead_created",
                        conditionSummary = "Goal = Weight Management, Lucknow locality detected",
                        actionSummary = "Assign Dr. Ananya, dispatch Hindi/English welcome template, create day-1 task",
                        isActive = true
                    ),
                    AutomationRuleEntity(
                        id = "aut-602",
                        name = "Day 7 Inactivity At-Risk Winback Loop",
                        triggerEvent = "customer_inactive_7d",
                        conditionSummary = "Total visits >= 3 AND days_since_last_visit >= 7",
                        actionSummary = "Mark customer as AT_RISK, queue WhatsApp friendly nudge, assign coach task",
                        isActive = true
                    ),
                    AutomationRuleEntity(
                        id = "aut-603",
                        name = "Appointment Missed Instant Re-booking WhatsApp",
                        triggerEvent = "appointment_missed",
                        conditionSummary = "Status changes to Missed",
                        actionSummary = "Deduct 20 lead score points, dispatch re-booking link within 30 minutes",
                        isActive = true
                    ),
                    AutomationRuleEntity(
                        id = "aut-604",
                        name = "Visit Check-in Hydration & Diet Log Alert",
                        triggerEvent = "visit_completed",
                        conditionSummary = "Attendance status = Completed",
                        actionSummary = "Increment visit streak counter, send customized post-consultation guide",
                        isActive = true
                    )
                )
                dao.insertAutomationRules(automations)

                // 9. WhatsApp Messages Sample Log
                val waMessages = listOf(
                    WhatsAppMessageEntity(
                        id = "wa-701",
                        phone = "+91 94150 28419",
                        customerName = "Amitabh Srivastava",
                        direction = "OUTBOUND",
                        messageText = "Namaste Amitabh Ji! 🙏 Welcome to Healthy King LIFE Weight Loss Wellness Center, Lucknow. Your personalized Weight Management evaluation is confirmed for today at 10:30 AM at Gomti Nagar center.",
                        templateName = "appointment_confirmed_lucknow",
                        status = "Read",
                        trackingToken = "wa_tk_94150_wm",
                        timestampFormatted = "09:15 AM"
                    ),
                    WhatsAppMessageEntity(
                        id = "wa-702",
                        phone = "+91 94150 28419",
                        customerName = "Amitabh Srivastava",
                        direction = "INBOUND",
                        messageText = "Ji, thank you! Main Gomti Nagar office se aa raha hoon. Location pin mil gaya hai.",
                        templateName = null,
                        status = "Delivered",
                        trackingToken = "wa_tk_94150_wm",
                        timestampFormatted = "09:22 AM"
                    ),
                    WhatsAppMessageEntity(
                        id = "wa-703",
                        phone = "+91 98891 76543",
                        customerName = "Vikramaditya Singh",
                        direction = "OUTBOUND",
                        messageText = "Namaste Vikram Ji! We missed you at Healthy King LIFE Lucknow this week. Consistency is key to unlocking high energy! Would you like us to reschedule your body toning checkup for Friday?",
                        templateName = "retention_winback_day7",
                        status = "Delivered",
                        trackingToken = "wa_tk_retention_vikram",
                        timestampFormatted = "Yesterday, 04:00 PM"
                    )
                )
                waMessages.forEach { dao.insertWhatsAppMessage(it) }

                // 10. Visitor Record
                dao.saveVisitor(
                    VisitorEntity(
                        visitorId = "vis-lucknow-anon-8821",
                        landingPage = "/lucknow-weight-loss-center",
                        utmSource = "instagram_ads",
                        utmCampaign = "lucknow_diwali_wellness_offer",
                        city = "Lucknow"
                    )
                )
            }
        }
    }
}
