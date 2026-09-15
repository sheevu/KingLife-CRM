package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AutomationRuleEntity
import com.example.data.model.WhatsAppMessageEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.WellnessCreamBg
import com.example.ui.theme.WellnessGreen
import com.example.ui.theme.WellnessGreenContainer
import com.example.ui.theme.WellnessGreenDark
import com.example.ui.theme.WellnessOrange
import com.example.ui.theme.WellnessOrangeContainer
import com.example.ui.theme.WhatsAppDark
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.WellnessCrmViewModel

data class ApprovedTemplateInfo(
    val name: String,
    val category: String,
    val language: String,
    val status: String,
    val body: String,
    val buttons: List<String>
)

@Composable
fun WhatsAppAutomationScreen(
    viewModel: WellnessCrmViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.whatsappMessages.collectAsStateWithLifecycle()
    val rules by viewModel.automationRules.collectAsStateWithLifecycle()
    val recentEvents by viewModel.recentEvents.collectAsStateWithLifecycle()
    val visitor by viewModel.visitor.collectAsStateWithLifecycle()

    var testPhone by remember { mutableStateOf("+91 94150 12345") }
    var testClient by remember { mutableStateOf("Rajesh Verma") }
    var testMessage by remember { mutableStateOf("Namaste Rajesh Ji! 🙏 Your Lucknow weight management evaluation slot is reserved for tomorrow 11:00 AM at Gomti Nagar.") }

    val approvedTemplates = remember {
        listOf(
            ApprovedTemplateInfo(
                name = "welcome_wellness_lucknow",
                category = "UTILITY",
                language = "English / Hindi",
                status = "APPROVED",
                body = "Namaste {{1}} Ji! 🙏 Welcome to Healthy King LIFE Lucknow. We have reserved your evaluation slot for {{2}} at Gomti Nagar.",
                buttons = listOf("🔘 Book Evaluation", "🔘 Learn More", "🔘 Call Center")
            ),
            ApprovedTemplateInfo(
                name = "whatsapp_started_welcome",
                category = "MARKETING",
                language = "English",
                status = "APPROVED",
                body = "Namaste {{1}} Ji! Thank you for connecting on WhatsApp. We have special evaluation slots for Gomti Nagar & Hazratganj today.",
                buttons = listOf("🔘 Book Evaluation", "🔘 View Pricing", "🔘 Center Location")
            ),
            ApprovedTemplateInfo(
                name = "retention_streak_nudge",
                category = "UTILITY",
                language = "Hindi / Hinglish",
                status = "APPROVED",
                body = "Namaste {{1}} Ji! 🙏 Humne notice kiya ki pichle 7 dinon se aapka center visit miss hua hai. Kya hum aapka slot reserve karein?",
                buttons = listOf("🔘 Slot Book Karein", "🔘 Coach Se Baat Karein")
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessCreamBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Automation Engine Title
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "WhatsApp Cloud API & Automations",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WellnessGreenDark
                    )
                    Text(
                        text = "Real-time Event-Driven Supabase & Meta Engine",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Edge Functions Live",
                        color = WhatsAppDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Approved WhatsApp Cloud API Templates
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SmartButton, contentDescription = null, tint = WhatsAppGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Approved WhatsApp Templates (Supabase)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "${approvedTemplates.size} Approved",
                            color = WellnessGreenDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    approvedTemplates.forEach { tpl ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WellnessCreamBg)
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = tpl.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WellnessGreenDark
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tpl.status,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WhatsAppDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = tpl.body,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                tpl.buttons.forEach { btn ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White)
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = btn,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WhatsAppDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Anonymous Visitor Tracking & Identity Stitching Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = WellnessOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Anonymous Visitor Tracking & Stitching",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tracks cookie-based unique visitor_id, page views, time-on-site dwell heartbeats, and CTA clicks. When phone number is submitted, PostgreSQL merge_visitor_data stitches past visits into the lead record.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(WellnessCreamBg)
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("Current Visitor ID", fontSize = 9.sp, color = Color.Gray)
                                Text(
                                    text = visitor?.visitorId ?: "vis-8821a",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WellnessGreenDark
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(WellnessCreamBg)
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("Sessions & Dwell", fontSize = 9.sp, color = Color.Gray)
                                Text(
                                    text = "${visitor?.sessionCount ?: 2} visits • 4m 15s",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WellnessOrange
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Automation Rules Engine
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = WellnessOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Automated Retention & Funnel Triggers",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    rules.forEach { rule ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = rule.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "Trigger: ${rule.triggerEvent}", fontSize = 11.sp, color = WellnessOrange)
                                Text(text = rule.actionSummary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = rule.isActive,
                                onCheckedChange = { viewModel.toggleRule(rule.id, rule.isActive) },
                                colors = SwitchDefaults.colors(checkedThumbColor = WellnessGreen, checkedTrackColor = WellnessGreenContainer)
                            )
                        }
                    }
                }
            }
        }

        // Dispatch Custom Test WhatsApp Message
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Simulate WhatsApp Cloud API Dispatch",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = WellnessGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = testClient,
                            onValueChange = { testClient = it },
                            label = { Text("Recipient Name") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = testPhone,
                            onValueChange = { testPhone = it },
                            label = { Text("+91 Phone") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = testMessage,
                        onValueChange = { testMessage = it },
                        label = { Text("Message Body (with Interactive Buttons)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (testPhone.isNotBlank() && testMessage.isNotBlank()) {
                                viewModel.triggerWhatsAppForLead(
                                    com.example.data.model.LeadEntity(
                                        id = "lead-sim",
                                        name = testClient,
                                        phone = testPhone,
                                        goal = "Weight Management"
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simulate_whatsapp_send_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dispatch WhatsApp with Buttons", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live WhatsApp Message Stream with Delivery Status
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "WhatsApp Delivery Status & Message Log",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WellnessGreenDark
                )
                Text(
                    text = "${messages.size} Messages",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        items(messages) { msg ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (msg.direction == "OUTBOUND") Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (msg.direction == "OUTBOUND") "→ To: ${msg.customerName}" else "← From: ${msg.customerName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (msg.direction == "OUTBOUND") WellnessGreenDark else WellnessOrange
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "(${msg.phone})", fontSize = 11.sp, color = Color.Gray)
                        }

                        // Delivery Status Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (msg.status) {
                                        "Read" -> Color(0xFFE0F2FE)
                                        "Delivered" -> Color(0xFFDCFCE7)
                                        "Sent" -> Color(0xFFFEF3C7)
                                        else -> Color(0xFFF3F4F6)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = null,
                                    tint = when (msg.status) {
                                        "Read" -> Color(0xFF0284C7)
                                        "Delivered" -> WhatsAppGreen
                                        else -> Color.Gray
                                    },
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = msg.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (msg.status) {
                                        "Read" -> Color(0xFF0369A1)
                                        "Delivered" -> WhatsAppDark
                                        else -> Color.DarkGray
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = msg.messageText, fontSize = 12.sp, lineHeight = 16.sp)

                    // Interactive Buttons Render in Message Log
                    if (msg.direction == "OUTBOUND") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("🔘 Book Evaluation", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WhatsAppDark)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("🔘 Learn More", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WhatsAppDark)
                            }
                        }
                    }

                    if (msg.trackingToken != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Token: ${msg.trackingToken} • ${msg.timestampFormatted}", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Universal Realtime Events Stream
        item {
            Text(
                text = "Universal Event Tracking Stream",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WellnessGreenDark
            )
        }

        items(recentEvents) { ev ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Icon(Icons.Default.Timeline, contentDescription = null, tint = WellnessGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = ev.eventType, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = WellnessGreenDark)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = ev.eventDataJson, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
