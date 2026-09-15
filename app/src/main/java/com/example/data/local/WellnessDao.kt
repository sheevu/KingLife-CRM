package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface WellnessDao {

    // Services
    @Query("SELECT * FROM services ORDER BY displayOrder ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    // Leads
    @Query("SELECT * FROM leads ORDER BY createdAtEpoch DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity)

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Query("UPDATE leads SET stage = :newStage WHERE id = :leadId")
    suspend fun updateLeadStage(leadId: String, newStage: String)

    @Query("UPDATE leads SET leadScore = :newScore WHERE id = :leadId")
    suspend fun updateLeadScore(leadId: String, newScore: Int)

    // Customers
    @Query("SELECT * FROM customers ORDER BY totalVisits DESC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    fun getCustomerById(id: String): Flow<CustomerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    // Appointments
    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateAppointmentStatus(id: String, status: String)

    // Visits
    @Query("SELECT * FROM visits ORDER BY id DESC")
    fun getAllVisits(): Flow<List<VisitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: VisitEntity)

    @Query("UPDATE visits SET attendanceStatus = :status, checkOutTime = :checkOut WHERE id = :id")
    suspend fun updateVisitStatus(id: String, status: String, checkOut: String?)

    // Progress Records
    @Query("SELECT * FROM progress_records WHERE customerId = :customerId ORDER BY id ASC")
    fun getProgressForCustomer(customerId: String): Flow<List<ProgressRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressRecord(record: ProgressRecordEntity)

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: String, status: String)

    // WhatsApp Messages
    @Query("SELECT * FROM whatsapp_messages ORDER BY id DESC")
    fun getAllWhatsAppMessages(): Flow<List<WhatsAppMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWhatsAppMessage(message: WhatsAppMessageEntity)

    // Automation Rules
    @Query("SELECT * FROM automation_rules")
    fun getAllAutomationRules(): Flow<List<AutomationRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomationRules(rules: List<AutomationRuleEntity>)

    @Query("UPDATE automation_rules SET isActive = :isActive WHERE id = :id")
    suspend fun toggleAutomationRule(id: String, isActive: Boolean)

    // Visitors & Events
    @Query("SELECT * FROM visitors LIMIT 1")
    fun getVisitor(): Flow<VisitorEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVisitor(visitor: VisitorEntity)

    @Insert
    suspend fun logEvent(event: UniversalEventEntity)

    @Query("SELECT * FROM universal_events ORDER BY id DESC LIMIT 50")
    fun getRecentEvents(): Flow<List<UniversalEventEntity>>
}
