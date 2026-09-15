package com.example.data.repository

import com.example.data.local.WellnessDao
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
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class WellnessCrmRepository(private val dao: WellnessDao) {

    val services: Flow<List<ServiceEntity>> = dao.getAllServices()
    val leads: Flow<List<LeadEntity>> = dao.getAllLeads()
    val customers: Flow<List<CustomerEntity>> = dao.getAllCustomers()
    val appointments: Flow<List<AppointmentEntity>> = dao.getAllAppointments()
    val visits: Flow<List<VisitEntity>> = dao.getAllVisits()
    val tasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val whatsappMessages: Flow<List<WhatsAppMessageEntity>> = dao.getAllWhatsAppMessages()
    val automationRules: Flow<List<AutomationRuleEntity>> = dao.getAllAutomationRules()
    val recentEvents: Flow<List<UniversalEventEntity>> = dao.getRecentEvents()
    val visitor: Flow<VisitorEntity?> = dao.getVisitor()

    fun getCustomerById(id: String): Flow<CustomerEntity?> = dao.getCustomerById(id)
    fun getProgressForCustomer(id: String): Flow<List<ProgressRecordEntity>> = dao.getProgressForCustomer(id)

    suspend fun submitOnboardingLead(
        name: String,
        phone: String,
        locality: String,
        goal: String,
        timeline: String,
        dietPreference: String,
        activityLevel: String,
        preferredSlot: String,
        language: String
    ): LeadEntity {
        val leadId = "lead-" + UUID.randomUUID().toString().take(6)
        val score = 35 // Base + Form Complete score

        val lead = LeadEntity(
            id = leadId,
            name = name,
            phone = phone,
            locality = locality,
            goal = goal,
            timeline = timeline,
            dietPreference = dietPreference,
            activityLevel = activityLevel,
            preferredSlot = preferredSlot,
            preferredLanguage = language,
            stage = "Wellness Goal Selected",
            leadScore = score,
            assignedCoach = if (goal == "Nutrition & Diet") "Coach Priya Sharma" else "Dr. Ananya Verma"
        )
        dao.insertLead(lead)

        dao.logEvent(
            UniversalEventEntity(
                visitorId = "vis-lucknow-anon",
                leadId = leadId,
                eventType = "FORM_SUBMIT",
                eventDataJson = "{\"goal\":\"$goal\",\"phone\":\"$phone\",\"locality\":\"$locality\"}"
            )
        )

        return lead
    }

    suspend fun updateLeadStage(leadId: String, newStage: String) {
        dao.updateLeadStage(leadId, newStage)
        dao.logEvent(
            UniversalEventEntity(
                visitorId = "vis-crm-staff",
                leadId = leadId,
                eventType = "STAGE_UPDATE",
                eventDataJson = "{\"stage\":\"$newStage\"}"
            )
        )
    }

    suspend fun updateLeadScore(leadId: String, newScore: Int) {
        dao.updateLeadScore(leadId, newScore)
    }

    suspend fun bookAppointment(
        customerOrLeadName: String,
        phone: String,
        serviceName: String,
        coachName: String,
        date: String,
        timeSlot: String,
        notes: String
    ) {
        val aptId = "apt-" + UUID.randomUUID().toString().take(6)
        val apt = AppointmentEntity(
            id = aptId,
            customerOrLeadName = customerOrLeadName,
            phone = phone,
            serviceName = serviceName,
            coachName = coachName,
            appointmentDate = date,
            timeSlot = timeSlot,
            status = "Scheduled",
            notes = notes
        )
        dao.insertAppointment(apt)

        dao.logEvent(
            UniversalEventEntity(
                visitorId = "vis-booking",
                eventType = "BOOKING_COMPLETE",
                eventDataJson = "{\"service\":\"$serviceName\",\"date\":\"$date\"}"
            )
        )
    }

    suspend fun updateAppointmentStatus(id: String, status: String) {
        dao.updateAppointmentStatus(id, status)
    }

    suspend fun checkInCustomer(customerId: String, customerName: String, coachName: String, serviceName: String) {
        val visitId = "vst-" + UUID.randomUUID().toString().take(6)
        val visit = VisitEntity(
            id = visitId,
            customerId = customerId,
            customerName = customerName,
            visitNumber = 1,
            visitDate = "Today",
            checkInTime = "Just now",
            checkOutTime = null,
            coachName = coachName,
            serviceName = serviceName,
            attendanceStatus = "Checked-In",
            notes = "In-person visit logged at Lucknow Center reception desk"
        )
        dao.insertVisit(visit)

        dao.logEvent(
            UniversalEventEntity(
                visitorId = "reception-terminal",
                customerId = customerId,
                eventType = "VISIT_CHECKIN",
                eventDataJson = "{\"coach\":\"$coachName\",\"service\":\"$serviceName\"}"
            )
        )
    }

    suspend fun checkOutVisit(visitId: String) {
        dao.updateVisitStatus(visitId, "Completed", "Just now")
    }

    suspend fun recordProgress(
        customerId: String,
        dateLabel: String,
        weightKg: Double,
        waistCm: Double,
        energyRating: Int,
        activityLevel: String,
        adherencePct: Int,
        notes: String
    ) {
        val record = ProgressRecordEntity(
            customerId = customerId,
            recordDate = dateLabel,
            weightKg = weightKg,
            waistCm = waistCm,
            energyRating = energyRating,
            activityLevel = activityLevel,
            adherencePct = adherencePct,
            coachNotes = notes
        )
        dao.insertProgressRecord(record)
    }

    suspend fun toggleTask(taskId: String, currentStatus: String) {
        val nextStatus = if (currentStatus == "Pending") "Done" else "Pending"
        dao.updateTaskStatus(taskId, nextStatus)
    }

    suspend fun toggleAutomationRule(ruleId: String, currentActive: Boolean) {
        dao.toggleAutomationRule(ruleId, !currentActive)
    }

    suspend fun sendWhatsAppMessage(
        phone: String,
        customerName: String,
        messageText: String,
        templateName: String? = null
    ) {
        val token = "wa_tk_" + UUID.randomUUID().toString().take(6)
        val message = WhatsAppMessageEntity(
            id = "wa-" + UUID.randomUUID().toString().take(6),
            phone = phone,
            customerName = customerName,
            direction = "OUTBOUND",
            messageText = messageText,
            templateName = templateName,
            status = "Sent",
            trackingToken = token,
            timestampFormatted = "Just now"
        )
        dao.insertWhatsAppMessage(message)

        dao.logEvent(
            UniversalEventEntity(
                visitorId = "wa-engine",
                eventType = "WHATSAPP_MESSAGE",
                eventDataJson = "{\"token\":\"$token\",\"phone\":\"$phone\"}"
            )
        )
    }
}
