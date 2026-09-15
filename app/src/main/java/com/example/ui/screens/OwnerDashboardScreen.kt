package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.KpiCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.WellnessCreamBg
import com.example.ui.theme.WellnessGreen
import com.example.ui.theme.WellnessGreenContainer
import com.example.ui.theme.WellnessGreenDark
import com.example.ui.theme.WellnessLime
import com.example.ui.theme.WellnessOrange
import com.example.ui.theme.WellnessOrangeContainer
import com.example.ui.theme.WellnessPurpleAccent
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.WellnessCrmViewModel

@Composable
fun OwnerDashboardScreen(
    viewModel: WellnessCrmViewModel,
    modifier: Modifier = Modifier
) {
    val leads by viewModel.leads.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val appointments by viewModel.appointments.collectAsStateWithLifecycle()
    val visits by viewModel.visits.collectAsStateWithLifecycle()
    val waMessages by viewModel.whatsappMessages.collectAsStateWithLifecycle()

    val activeCount = customers.count { it.stage == "Active Customer" }
    val atRiskCount = customers.count { it.stage == "At Risk" }
    val scheduledToday = appointments.count { it.status == "Scheduled" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessCreamBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Dashboard Title
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Owner Executive Dashboard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = WellnessGreenDark
                    )
                    Text(
                        text = "Lucknow Center Performance • Realtime Supabase Sync",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(WellnessGreenContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "92% Retention",
                        color = WellnessGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Primary KPI Grid 2x2
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                KpiCard(
                    title = "Revenue MTD",
                    value = "₹1,48,500",
                    subtitle = "+24% vs last month",
                    icon = Icons.Default.AttachMoney,
                    accentColor = WellnessGreen,
                    modifier = Modifier.weight(1f).testTag("kpi_revenue")
                )
                KpiCard(
                    title = "Total Active Clients",
                    value = "$activeCount",
                    subtitle = "142 Lifetime Total",
                    icon = Icons.Default.Group,
                    accentColor = WellnessOrange,
                    modifier = Modifier.weight(1f).testTag("kpi_active_clients")
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                KpiCard(
                    title = "Today's Appointments",
                    value = "$scheduledToday",
                    subtitle = "${appointments.size} Total scheduled",
                    icon = Icons.Default.DateRange,
                    accentColor = WellnessPurpleAccent,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "WhatsApp Chats",
                    value = "${waMessages.size}",
                    subtitle = "42 Inbound/Outbound",
                    icon = Icons.Default.Chat,
                    accentColor = WhatsAppGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Customer Acquisition & Conversion Funnel
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Customer Lifecycle Conversion Funnel",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = WellnessGreenDark
                        )
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = WellnessGreen)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    FunnelRow(label = "Anonymous Visitor → Lead Captured", pct = 38, count = "124 leads")
                    FunnelRow(label = "Lead → WhatsApp Started", pct = 65, count = "81 chats")
                    FunnelRow(label = "WhatsApp → Appointment Booked", pct = 45, count = "36 booked")
                    FunnelRow(label = "Appointment → In-Person Visit", pct = 85, count = "31 attended")
                    FunnelRow(label = "Visit → Programme Enrolled", pct = 70, count = "22 clients")
                    FunnelRow(label = "Programme → Renewal / Maintenance", pct = 62, count = "14 renewed")
                }
            }
        }

        // At-Risk Retention Warning
        if (atRiskCount > 0) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE11D48))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Retention Alert: $atRiskCount Clients At Risk",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF9F1239)
                            )
                            Text(
                                text = "3+ prior visits logged, but no attendance in 7+ days. Automated WhatsApp nudge queued.",
                                fontSize = 11.sp,
                                color = Color(0xFFBE123C)
                            )
                        }
                    }
                }
            }
        }

        // Coach Performance Leaderboard
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Lucknow Coach Performance Ranking",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = WellnessGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    CoachRankItem(name = "Dr. Ananya Verma", specialty = "Metabolic Reset & Fat Loss", clients = 28, retention = "94%", rating = "4.9")
                    CoachRankItem(name = "Coach Priya Sharma", specialty = "Nutrition & Lifestyle Coaching", clients = 22, retention = "91%", rating = "4.8")
                    CoachRankItem(name = "Trainer Rahul Dixit", specialty = "Functional Mobility & Training", clients = 16, retention = "89%", rating = "4.8")
                }
            }
        }

        // Top Lucknow Localities
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Acquisition by Lucknow Locality",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = WellnessGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LocalityBar(name = "Gomti Nagar", pct = 42, count = "52 Leads")
                    LocalityBar(name = "Hazratganj", pct = 28, count = "35 Leads")
                    LocalityBar(name = "Aliganj", pct = 18, count = "22 Leads")
                    LocalityBar(name = "Indira Nagar & Mahanagar", pct = 12, count = "15 Leads")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FunnelRow(label: String, pct: Int, count: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "$pct% ($count)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WellnessGreen)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { pct / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = WellnessGreen,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

@Composable
private fun CoachRankItem(name: String, specialty: String, clients: Int, retention: String, rating: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(WellnessGreenContainer)
        ) {
            Text(text = name.take(1), fontWeight = FontWeight.Bold, color = WellnessGreenDark)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = specialty, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "$clients Clients", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = WellnessOrange)
            Text(text = "$retention Ret | ★ $rating", fontSize = 11.sp, color = WellnessGreen)
        }
    }
}

@Composable
private fun LocalityBar(name: String, pct: Int, count: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = count, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WellnessOrange)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { pct / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp)),
            color = WellnessOrange,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
