package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ServiceEntity
import com.example.ui.theme.WellnessCreamBg
import com.example.ui.theme.WellnessGreen
import com.example.ui.theme.WellnessGreenContainer
import com.example.ui.theme.WellnessGreenDark
import com.example.ui.theme.WellnessLime
import com.example.ui.theme.WellnessLimeContainer
import com.example.ui.theme.WellnessOrange
import com.example.ui.theme.WellnessOrangeContainer
import com.example.ui.theme.WellnessYellowSoft
import com.example.ui.theme.WhatsAppDark
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.WellnessCrmViewModel

@Composable
fun PublicOnboardingScreen(
    viewModel: WellnessCrmViewModel,
    modifier: Modifier = Modifier
) {
    val services by viewModel.services.collectAsStateWithLifecycle()
    val onboardingStep by viewModel.onboardingStep.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val createdLead by viewModel.createdLead.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    var selectedCategory by remember { mutableStateOf("All") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessCreamBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HeroBanner(currentLang = currentLang)
        }

        // Trust Badges
        item {
            TrustHighlightsRow()
        }

        // 5-Step Progressive Wellness Onboarding Card
        item {
            WellnessOnboardingCard(
                step = onboardingStep,
                formState = formState,
                createdLead = createdLead,
                onSelectGoal = { viewModel.setOnboardingGoal(it) },
                onSelectTimeline = { viewModel.setOnboardingTimeline(it) },
                onUpdateBasicInfo = { name, phone, age, locality, lang ->
                    viewModel.updateBasicInfo(name, phone, age, locality, lang)
                },
                onUpdateLifestyle = { activity, diet, slot ->
                    viewModel.updateLifestyleInfo(activity, diet, slot)
                },
                onSubmit = { viewModel.submitOnboarding() },
                onRestart = { viewModel.restartOnboarding() },
                onGoToStep = { viewModel.goToStep(it) },
                onWhatsAppClick = { lead ->
                    viewModel.triggerWhatsAppForLead(lead)
                }
            )
        }

        // Services Catalog Header
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = if (currentLang == "Hindi") "हमारी वेलनेस सेवाएँ" else "Wellness Services Catalog",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = WellnessGreenDark
                        )
                        Text(
                            text = "Database-driven clinical & lifestyle solutions in Lucknow",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Service Category Filter Chips
                val categories = listOf("All", "Weight Management", "Diet & Nutrition Counselling", "Personal Training", "Lifestyle Coaching")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WellnessGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Services List
        val filteredServices = if (selectedCategory == "All") {
            services
        } else {
            services.filter { it.category.contains(selectedCategory, ignoreCase = true) }
        }

        items(filteredServices) { service ->
            ServiceCardItem(
                service = service,
                onBookClick = {
                    viewModel.bookAppointment(
                        name = if (formState.name.isNotBlank()) formState.name else "Lucknow Guest",
                        phone = if (formState.phone.isNotBlank()) formState.phone else "+91 94150 00000",
                        service = service.name,
                        coach = "Dr. Ananya Verma",
                        date = "Tomorrow",
                        slot = "11:00 AM",
                        notes = "In-person consultation requested via Lucknow portal"
                    )
                }
            )
        }

        // Direct Appointment Booking Section
        item {
            AppointmentBookingCard(viewModel = viewModel)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HeroBanner(currentLang: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(WellnessGreenDark, WellnessGreen, Color(0xFF0D9488))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WellnessOrange)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "LUCKNOW, UP",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gomti Nagar • Hazratganj • Aliganj",
                        color = WellnessYellowSoft,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (currentLang == "Hindi")
                        "हेल्दी किंग LIFE वेट लॉस वेलनेस सेंटर"
                    else
                        "Healthy King LIFE\nWeight Loss Wellness Center",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Sustainable weight management, clinical nutrition & lifestyle coaching. No starvation, real metabolic transformation.",
                    fontSize = 13.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = WellnessLimeContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Customized Indian Diet (Veg / Egg / Non-Veg)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun TrustHighlightsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TrustItem(number = "1,500+", label = "Lucknow Clients", modifier = Modifier.weight(1f))
        TrustItem(number = "92%", label = "Retention Rate", modifier = Modifier.weight(1f))
        TrustItem(number = "₹0", label = "Crash Diet Cost", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TrustItem(number: String, label: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(text = number, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WellnessOrange)
            Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun WellnessOnboardingCard(
    step: Int,
    formState: com.example.ui.viewmodel.OnboardingFormState,
    createdLead: com.example.data.model.LeadEntity?,
    onSelectGoal: (String) -> Unit,
    onSelectTimeline: (String) -> Unit,
    onUpdateBasicInfo: (String, String, String, String, String) -> Unit,
    onUpdateLifestyle: (String, String, String) -> Unit,
    onSubmit: () -> Unit,
    onRestart: () -> Unit,
    onGoToStep: (Int) -> Unit,
    onWhatsAppClick: (com.example.data.model.LeadEntity) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WellnessGreenContainer, RoundedCornerShape(20.dp))
            .testTag("onboarding_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Step header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(WellnessGreen)
                    ) {
                        Text(text = "$step", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Step $step of 5: Personalized Onboarding",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = WellnessGreenDark
                    )
                }

                if (step in 2..4) {
                    Text(
                        text = "Back",
                        fontSize = 12.sp,
                        color = WellnessOrange,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onGoToStep(step - 1) }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (step) {
                1 -> Step1GoalSelection(onSelectGoal)
                2 -> Step2TimelineSelection(onSelectTimeline)
                3 -> Step3BasicInfo(formState, onUpdateBasicInfo, onNext = { onGoToStep(4) })
                4 -> Step4LifestyleInfo(formState, onUpdateLifestyle, onSubmit)
                5 -> Step5ProfileReady(createdLead, onRestart, onWhatsAppClick)
            }
        }
    }
}

@Composable
private fun Step1GoalSelection(onSelectGoal: (String) -> Unit) {
    Column {
        Text(
            text = "What is your primary wellness goal?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Select one to tailor your Lucknow consultation & meal plan",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        val goals = listOf(
            Pair("Weight Management", "Targeted fat loss, inch loss & metabolic reset"),
            Pair("Nutrition & Diet", "Awadhi & North Indian balanced diet plans"),
            Pair("Fitness", "Functional fitness, posture & stamina building"),
            Pair("Energy & Active Lifestyle", "Combat fatigue, brain fog and sluggishness"),
            Pair("General Wellness", "Overall health, hormonal balance & detox"),
            Pair("Maintenance", "Long-term weight maintenance after weight loss")
        )

        goals.forEach { (title, desc) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WellnessCreamBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectGoal(title) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = WellnessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = WellnessGreenDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Step2TimelineSelection(onSelectTimeline: (String) -> Unit) {
    Column {
        Text(
            text = "When would you like to begin?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Helps us schedule your Lucknow coach availability",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        val timelines = listOf(
            Pair("Immediately", "Ready today / tomorrow at Gomti Nagar or Hazratganj"),
            Pair("This Week", "Looking for an appointment in the next 3-5 days"),
            Pair("This Month", "Planning my fitness calendar for this month"),
            Pair("Just Exploring", "Want guidance and info on plans first")
        )

        timelines.forEach { (time, desc) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WellnessCreamBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectTimeline(time) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = WellnessOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = time, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = WellnessOrange,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Step3BasicInfo(
    formState: com.example.ui.viewmodel.OnboardingFormState,
    onUpdate: (String, String, String, String, String) -> Unit,
    onNext: () -> Unit
) {
    var name by remember { mutableStateOf(formState.name) }
    var phone by remember { mutableStateOf(formState.phone) }
    var ageRange by remember { mutableStateOf(formState.ageRange) }
    var locality by remember { mutableStateOf(formState.locality) }
    var language by remember { mutableStateOf(formState.language) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "Your Contact & Lucknow Locality", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                onUpdate(name, phone, ageRange, locality, language)
            },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = WellnessGreen) },
            modifier = Modifier.fillMaxWidth().testTag("onboarding_name_input")
        )

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                onUpdate(name, phone, ageRange, locality, language)
            },
            label = { Text("Mobile Number (+91 WhatsApp)") },
            placeholder = { Text("e.g. 98390 12345") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = WellnessGreen) },
            modifier = Modifier.fillMaxWidth().testTag("onboarding_phone_input")
        )

        // Lucknow Locality Selector
        Text(text = "Select Lucknow Locality", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        val localities = listOf("Gomti Nagar", "Hazratganj", "Aliganj", "Indira Nagar", "Mahanagar", "Ashiyana", "Rajajipuram")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(localities) { loc ->
                FilterChip(
                    selected = locality == loc,
                    onClick = {
                        locality = loc
                        onUpdate(name, phone, ageRange, locality, language)
                    },
                    label = { Text(loc, fontSize = 11.sp) }
                )
            }
        }

        // Language & Age
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            val languages = listOf("Hinglish", "Hindi", "English")
            languages.forEach { lang ->
                FilterChip(
                    selected = language == lang,
                    onClick = {
                        language = lang
                        onUpdate(name, phone, ageRange, locality, language)
                    },
                    label = { Text(lang, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Button(
            onClick = { onNext() },
            colors = ButtonDefaults.buttonColors(containerColor = WellnessGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .testTag("onboarding_next_step4")
        ) {
            Text("Continue to Lifestyle Info", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Step4LifestyleInfo(
    formState: com.example.ui.viewmodel.OnboardingFormState,
    onUpdateLifestyle: (String, String, String) -> Unit,
    onSubmit: () -> Unit
) {
    var activity by remember { mutableStateOf(formState.activityLevel) }
    var diet by remember { mutableStateOf(formState.dietPreference) }
    var slot by remember { mutableStateOf(formState.preferredSlot) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "Diet & Daily Activity (Optional)", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        // Diet Preference
        Text(text = "Dietary Habit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        val diets = listOf("Vegetarian", "Eggetarian", "Non-Vegetarian")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            diets.forEach { d ->
                FilterChip(
                    selected = diet == d,
                    onClick = {
                        diet = d
                        onUpdateLifestyle(activity, diet, slot)
                    },
                    label = { Text(d, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Activity Level
        Text(text = "Current Activity Level", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        val activities = listOf("Sedentary (Desk Job)", "Moderate Walking", "Active / Workout")
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            activities.forEach { act ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (activity == act) WellnessGreenContainer else WellnessCreamBg
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            activity = act
                            onUpdateLifestyle(activity, diet, slot)
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = if (activity == act) WellnessGreenDark else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = act,
                            fontSize = 12.sp,
                            fontWeight = if (activity == act) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Consultation Slot
        Text(text = "Preferred Consultation Time", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        val slots = listOf("Morning (9am - 12pm)", "Afternoon (1pm - 4pm)", "Evening (5pm - 8pm)")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(slots) { s ->
                FilterChip(
                    selected = slot == s,
                    onClick = {
                        slot = s
                        onUpdateLifestyle(activity, diet, slot)
                    },
                    label = { Text(s, fontSize = 11.sp) }
                )
            }
        }

        Button(
            onClick = { onSubmit() },
            colors = ButtonDefaults.buttonColors(containerColor = WellnessGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .testTag("onboarding_submit_button")
        ) {
            Text("Generate My Wellness Profile", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Step5ProfileReady(
    createdLead: com.example.data.model.LeadEntity?,
    onRestart: () -> Unit,
    onWhatsAppClick: (com.example.data.model.LeadEntity) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(WellnessGreenContainer)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = WellnessGreen,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your basic wellness profile is ready.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = WellnessGreenDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Welcome to Healthy King LIFE, Lucknow! Our wellness coach has mapped your primary goal.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (createdLead != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WellnessCreamBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Name: ${createdLead.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = createdLead.locality, color = WellnessOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Goal: ${createdLead.goal}", fontSize = 12.sp)
                    Text(text = "Assigned Coach: ${createdLead.assignedCoach}", fontSize = 12.sp, color = WellnessGreen)
                    Text(text = "Initial Lead Score: ${createdLead.leadScore} pts", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PRIMARY CTA: Continue on WhatsApp
        Button(
            onClick = {
                createdLead?.let { onWhatsAppClick(it) }
            },
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_continue_whatsapp_cta")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue on WhatsApp", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onRestart,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start New Consultation", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun ServiceCardItem(
    service: ServiceEntity,
    onBookClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WellnessGreenContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = service.category,
                        color = WellnessGreenDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "₹${service.priceInr.toInt()}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = WellnessOrange
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = service.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = service.shortDescription, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${service.durationMins} mins", fontSize = 11.sp, color = Color.Gray)
                }

                Button(
                    onClick = onBookClick,
                    colors = ButtonDefaults.buttonColors(containerColor = WellnessGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Book Session", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun AppointmentBookingCard(viewModel: WellnessCrmViewModel) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("Weight Management Consultation") }
    var date by remember { mutableStateOf("Tomorrow") }
    var slot by remember { mutableStateOf("11:00 AM") }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Book In-Person Lucknow Evaluation",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = WellnessGreenDark
            )
            Text(
                text = "Visit our Gomti Nagar or Hazratganj centers for full body scanning",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("WhatsApp Phone (+91)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        viewModel.bookAppointment(
                            name = name,
                            phone = phone,
                            service = service,
                            coach = "Dr. Ananya Verma",
                            date = date,
                            slot = slot,
                            notes = "Booked directly via Lucknow Landing screen"
                        )
                        name = ""
                        phone = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WellnessOrange),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("direct_book_button")
            ) {
                Text("Confirm Lucknow Appointment", fontWeight = FontWeight.Bold)
            }
        }
    }
}
