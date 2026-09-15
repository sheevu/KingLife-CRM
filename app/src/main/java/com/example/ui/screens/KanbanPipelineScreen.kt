package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LeadEntity
import com.example.ui.components.LeadScoreChip
import com.example.ui.theme.WellnessCreamBg
import com.example.ui.theme.WellnessGreen
import com.example.ui.theme.WellnessGreenContainer
import com.example.ui.theme.WellnessGreenDark
import com.example.ui.theme.WellnessOrange
import com.example.ui.theme.WellnessOrangeContainer
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.WellnessCrmViewModel

enum class KanbanViewMode {
    NATIVE_COMPOSE,
    REACT_TAILWIND_WEB
}

@Composable
fun KanbanPipelineScreen(
    viewModel: WellnessCrmViewModel,
    modifier: Modifier = Modifier
) {
    val leads by viewModel.leads.collectAsStateWithLifecycle()
    var viewMode by remember { mutableStateOf(KanbanViewMode.NATIVE_COMPOSE) }

    // Full 9-Stage Funnel including Anonymous Visitor
    val stages = listOf(
        "All Stages",
        "Anonymous Visitor",
        "Lead Captured",
        "Wellness Goal Selected",
        "WhatsApp Started",
        "Appointment Scheduled",
        "Consultation Completed",
        "Active Customer",
        "At Risk",
        "Renewal Due"
    )

    var selectedStageIndex by remember { mutableStateOf(0) }
    val selectedStage = stages[selectedStageIndex]

    // Include simulated anonymous visitor leads if in stage
    val combinedLeads = remember(leads) {
        val anonLead = LeadEntity(
            id = "lead-anon-1",
            visitorId = "vis-8821a",
            name = "Gomti Nagar Visitor",
            phone = "+91 94150 99881 (Anonymous)",
            locality = "Gomti Nagar",
            goal = "Weight Loss - 12kg",
            timeline = "Exploring",
            dietPreference = "Vegetarian",
            stage = "Anonymous Visitor",
            leadScore = 25,
            assignedCoach = "Unassigned",
            utmCampaign = "Viewed Weight Loss Consultation page (3m on site, 2 CTA clicks)"
        )
        listOf(anonLead) + leads
    }

    val filteredLeads = if (selectedStage == "All Stages") {
        combinedLeads
    } else {
        combinedLeads.filter { it.stage.equals(selectedStage, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessCreamBg)
    ) {
        // Pipeline Header & Mode Switcher
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Lead Funnel Kanban Board",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WellnessGreenDark
                        )
                        Text(
                            text = "Lucknow Center • Supabase & WhatsApp Synced",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Toggle View Mode: Native Compose vs React + Tailwind
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(WellnessCreamBg)
                            .padding(2.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (viewMode == KanbanViewMode.NATIVE_COMPOSE) WellnessGreen else Color.Transparent)
                                .clickable { viewMode = KanbanViewMode.NATIVE_COMPOSE }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Native",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (viewMode == KanbanViewMode.NATIVE_COMPOSE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (viewMode == KanbanViewMode.REACT_TAILWIND_WEB) WellnessGreen else Color.Transparent)
                                .clickable { viewMode = KanbanViewMode.REACT_TAILWIND_WEB }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = if (viewMode == KanbanViewMode.REACT_TAILWIND_WEB) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "React + Tailwind",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewMode == KanbanViewMode.REACT_TAILWIND_WEB) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        if (viewMode == KanbanViewMode.REACT_TAILWIND_WEB) {
            // Render the React & Tailwind Drag-and-Drop Kanban view via WebView
            ReactTailwindKanbanWebView(modifier = Modifier.fillMaxSize())
        } else {
            // Native Compose Kanban View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Horizontal Stages Scrollable Tabs
                item {
                    ScrollableTabRow(
                        selectedTabIndex = selectedStageIndex,
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        contentColor = WellnessGreen
                    ) {
                        stages.forEachIndexed { index, stage ->
                            Tab(
                                selected = selectedStageIndex == index,
                                onClick = { selectedStageIndex = index },
                                text = {
                                    val count = if (stage == "All Stages") combinedLeads.size else combinedLeads.count { it.stage.equals(stage, ignoreCase = true) }
                                    Text(
                                        text = "$stage ($count)",
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedStageIndex == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }

                // Stage Info Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(WellnessGreenContainer)
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, tint = WellnessGreenDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedStage == "All Stages") "Viewing all stages. Tap 'Move' or 'Next' on any card to update funnel stage." else "Stage '$selectedStage' contains ${filteredLeads.size} leads.",
                                fontSize = 11.sp,
                                color = WellnessGreenDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Leads List
                if (filteredLeads.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No leads in '$selectedStage'. Use 'Move' on leads to transfer to this stage.",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    items(filteredLeads) { lead ->
                        LeadPipelineCard(
                            lead = lead,
                            stages = stages.filter { it != "All Stages" },
                            onMoveStage = { nextStage ->
                                viewModel.moveLeadStage(lead.id, nextStage)
                            },
                            onWhatsAppClick = {
                                viewModel.triggerWhatsAppForLead(lead)
                            },
                            onCallClick = {
                                viewModel.quickCallLead(lead)
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ReactTailwindKanbanWebView(modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false

                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {}

                // Load the local React & Tailwind CSS Kanban asset
                loadUrl("file:///android_asset/kanban.html")
            }
        },
        modifier = modifier
    )
}

@Composable
private fun LeadPipelineCard(
    lead: LeadEntity,
    stages: List<String>,
    onMoveStage: (String) -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    var showStageMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lead_card_${lead.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Name & Score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = lead.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = WellnessOrange, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = lead.locality, fontSize = 11.sp, color = WellnessOrange, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = lead.phone, fontSize = 11.sp, color = Color.Gray)
                    }
                }
                LeadScoreChip(score = lead.leadScore)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Goal & Details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WellnessCreamBg)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Goal: ${lead.goal} • Diet: ${lead.dietPreference} • Slot: ${lead.preferredSlot}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Last Activity Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Last Activity",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "Active recently",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = if (lead.utmCampaign.isNotBlank()) lead.utmCampaign else "Engagement event recorded via Lucknow funnel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stage badge & Move dropdown
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stage: ",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = lead.stage,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (lead.stage == "WhatsApp Started") WhatsAppGreen else WellnessGreenDark
                    )
                }

                // Stage selector dropdown
                Box {
                    OutlinedButton(
                        onClick = { showStageMenu = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Move Stage ▾", fontSize = 10.sp)
                    }

                    DropdownMenu(
                        expanded = showStageMenu,
                        onDismissRequest = { showStageMenu = false }
                    ) {
                        stages.forEach { stageName ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (lead.stage == stageName) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp), tint = WellnessGreen)
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(stageName, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    onMoveStage(stageName)
                                    showStageMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: WhatsApp & Call & Next Stage
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onWhatsAppClick,
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp (Buttons)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onCallClick,
                    colors = ButtonDefaults.buttonColors(containerColor = WellnessGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call (+5)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                val nextStage = when (lead.stage) {
                    "Anonymous Visitor" -> "Lead Captured"
                    "Lead Captured" -> "Wellness Goal Selected"
                    "Wellness Goal Selected" -> "WhatsApp Started"
                    "WhatsApp Started" -> "Appointment Scheduled"
                    "Appointment Scheduled" -> "Consultation Completed"
                    "Consultation Completed" -> "Active Customer"
                    "Active Customer" -> "Renewal Due"
                    else -> "Active Customer"
                }

                OutlinedButton(
                    onClick = { onMoveStage(nextStage) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(0.9f)
                ) {
                    Text("Next →", fontSize = 10.sp, color = WellnessGreenDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
