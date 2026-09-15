package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
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
import com.example.data.repository.WellnessCrmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppPersona(val label: String, val badge: String) {
    PUBLIC("Public Landing & Onboarding", "Customer"),
    OWNER("Owner Dashboard", "Management"),
    COACH("Coach Dashboard", "Clinical"),
    RECEPTION("Reception Terminal", "Front Desk"),
    KANBAN("Lead Pipeline (Kanban)", "CRM"),
    CUSTOMER_PORTAL("My Wellness Portal", "Member"),
    WHATSAPP_ENGINE("WhatsApp & Automation", "Engine")
}

data class OnboardingFormState(
    val goal: String = "Weight Management",
    val timeline: String = "Immediately",
    val name: String = "",
    val phone: String = "",
    val ageRange: String = "26-35",
    val locality: String = "Gomti Nagar",
    val activityLevel: String = "Moderate",
    val dietPreference: String = "Vegetarian",
    val preferredSlot: String = "Morning (9am - 12pm)",
    val language: String = "Hinglish"
)

class WellnessCrmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WellnessCrmRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = WellnessCrmRepository(db.wellnessDao())
    }

    // Repositories StateFlows
    val services: StateFlow<List<ServiceEntity>> = repository.services
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leads: StateFlow<List<LeadEntity>> = repository.leads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<CustomerEntity>> = repository.customers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appointments: StateFlow<List<AppointmentEntity>> = repository.appointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visits: StateFlow<List<VisitEntity>> = repository.visits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val whatsappMessages: StateFlow<List<WhatsAppMessageEntity>> = repository.whatsappMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automationRules: StateFlow<List<AutomationRuleEntity>> = repository.automationRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentEvents: StateFlow<List<UniversalEventEntity>> = repository.recentEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visitor: StateFlow<VisitorEntity?> = repository.visitor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI state
    private val _currentPersona = MutableStateFlow(AppPersona.PUBLIC)
    val currentPersona: StateFlow<AppPersona> = _currentPersona.asStateFlow()

    private val _currentLanguage = MutableStateFlow("English")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _selectedCustomerId = MutableStateFlow("cust-201")
    val selectedCustomerId: StateFlow<String> = _selectedCustomerId.asStateFlow()

    private val _selectedCustomerProgress = MutableStateFlow<List<ProgressRecordEntity>>(emptyList())
    val selectedCustomerProgress: StateFlow<List<ProgressRecordEntity>> = _selectedCustomerProgress.asStateFlow()

    // Onboarding State
    private val _onboardingStep = MutableStateFlow(1)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    private val _formState = MutableStateFlow(OnboardingFormState())
    val formState: StateFlow<OnboardingFormState> = _formState.asStateFlow()

    private val _createdLead = MutableStateFlow<LeadEntity?>(null)
    val createdLead: StateFlow<LeadEntity?> = _createdLead.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        // Observe progress for selected customer
        viewModelScope.launch {
            _selectedCustomerId.collect { id ->
                repository.getProgressForCustomer(id).collect { records ->
                    _selectedCustomerProgress.value = records
                }
            }
        }
    }

    fun setPersona(persona: AppPersona) {
        _currentPersona.value = persona
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun selectCustomer(id: String) {
        _selectedCustomerId.value = id
        _currentPersona.value = AppPersona.CUSTOMER_PORTAL
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // Onboarding actions
    fun setOnboardingGoal(goal: String) {
        _formState.value = _formState.value.copy(goal = goal)
        _onboardingStep.value = 2
    }

    fun setOnboardingTimeline(timeline: String) {
        _formState.value = _formState.value.copy(timeline = timeline)
        _onboardingStep.value = 3
    }

    fun updateBasicInfo(name: String, phone: String, ageRange: String, locality: String, language: String) {
        _formState.value = _formState.value.copy(
            name = name,
            phone = phone,
            ageRange = ageRange,
            locality = locality,
            language = language
        )
    }

    fun goToStep(step: Int) {
        _onboardingStep.value = step
    }

    fun updateLifestyleInfo(activityLevel: String, dietPreference: String, preferredSlot: String) {
        _formState.value = _formState.value.copy(
            activityLevel = activityLevel,
            dietPreference = dietPreference,
            preferredSlot = preferredSlot
        )
    }

    fun submitOnboarding() {
        val form = _formState.value
        if (form.name.isBlank() || form.phone.isBlank()) {
            _statusMessage.value = "Please provide your Name and Mobile Number"
            return
        }

        viewModelScope.launch {
            val lead = repository.submitOnboardingLead(
                name = form.name,
                phone = form.phone,
                locality = form.locality,
                goal = form.goal,
                timeline = form.timeline,
                dietPreference = form.dietPreference,
                activityLevel = form.activityLevel,
                preferredSlot = form.preferredSlot,
                language = form.language
            )
            _createdLead.value = lead
            _onboardingStep.value = 5
            _statusMessage.value = "Namaste ${form.name}! Your wellness profile is created."
        }
    }

    fun restartOnboarding() {
        _formState.value = OnboardingFormState()
        _createdLead.value = null
        _onboardingStep.value = 1
    }

    // Pipeline & CRM actions
    fun moveLeadStage(leadId: String, nextStage: String) {
        viewModelScope.launch {
            repository.updateLeadStage(leadId, nextStage)
            _statusMessage.value = "Lead moved to $nextStage"
        }
    }

    fun quickCallLead(lead: LeadEntity) {
        viewModelScope.launch {
            repository.updateLeadScore(lead.id, lead.leadScore + 5)
            _statusMessage.value = "Call logged with ${lead.name} (+5 Lead Score)"
        }
    }

    fun triggerWhatsAppForLead(lead: LeadEntity) {
        viewModelScope.launch {
            val message = "Namaste ${lead.name} Ji! 🙏 Welcome to Healthy King LIFE Weight Loss Wellness Center, Lucknow. You selected '${lead.goal}'. Would you like to schedule your complimentary Body Composition Analysis at our Gomti Nagar center?"
            repository.sendWhatsAppMessage(
                phone = lead.phone,
                customerName = lead.name,
                messageText = message,
                templateName = "lead_welcome_lucknow"
            )
            repository.updateLeadStage(lead.id, "WhatsApp Started")
            repository.updateLeadScore(lead.id, lead.leadScore + 20)
            _statusMessage.value = "WhatsApp message dispatched to ${lead.name} (+20 Lead Score)"
        }
    }

    // Reception Actions
    fun fastCheckIn(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.checkInCustomer(
                customerId = customer.id,
                customerName = customer.name,
                coachName = customer.assignedCoach,
                serviceName = "Wellness Session"
            )
            _statusMessage.value = "Checked in ${customer.name} at reception desk"
        }
    }

    fun checkOutVisit(visitId: String) {
        viewModelScope.launch {
            repository.checkOutVisit(visitId)
            _statusMessage.value = "Visit checked out and logged"
        }
    }

    fun updateAppointmentStatus(id: String, status: String) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(id, status)
            _statusMessage.value = "Appointment marked as $status"
        }
    }

    fun bookAppointment(name: String, phone: String, service: String, coach: String, date: String, slot: String, notes: String) {
        viewModelScope.launch {
            repository.bookAppointment(name, phone, service, coach, date, slot, notes)
            _statusMessage.value = "Appointment successfully scheduled for $name!"
        }
    }

    // Task actions
    fun toggleTask(taskId: String, currentStatus: String) {
        viewModelScope.launch {
            repository.toggleTask(taskId, currentStatus)
        }
    }

    // Automation rule toggle
    fun toggleRule(ruleId: String, currentActive: Boolean) {
        viewModelScope.launch {
            repository.toggleAutomationRule(ruleId, currentActive)
        }
    }

    // Progress record
    fun logProgress(weightKg: Double, waistCm: Double, energy: Int, notes: String) {
        val custId = _selectedCustomerId.value
        viewModelScope.launch {
            repository.recordProgress(
                customerId = custId,
                dateLabel = "New Log",
                weightKg = weightKg,
                waistCm = waistCm,
                energyRating = energy,
                activityLevel = "Consistent Active",
                adherencePct = 95,
                notes = notes
            )
            _statusMessage.value = "Wellness progress recorded successfully!"
        }
    }
}
