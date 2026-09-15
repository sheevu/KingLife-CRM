package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WellnessCreamBg
import com.example.ui.theme.WellnessGreen
import com.example.ui.theme.WellnessGreenContainer
import com.example.ui.theme.WellnessGreenDark
import com.example.ui.theme.WellnessLime
import com.example.ui.theme.WellnessOrange
import com.example.ui.theme.WellnessOrangeContainer
import com.example.ui.theme.WellnessPurpleAccent
import com.example.ui.theme.WellnessPurpleContainer
import com.example.ui.theme.WellnessYellow
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.AppPersona

@Composable
fun PersonaSelectorTabs(
    selectedPersona: AppPersona,
    onSelectPersona: (AppPersona) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedPersona.ordinal,
        edgePadding = 12.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = WellnessGreen,
        modifier = modifier.testTag("persona_tabs")
    ) {
        AppPersona.values().forEach { persona ->
            Tab(
                selected = selectedPersona == persona,
                onClick = { onSelectPersona(persona) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = persona.label,
                            fontWeight = if (selectedPersona == persona) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selectedPersona == persona) WellnessGreenContainer else Color(0xFFF1F5F9)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = persona.badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedPersona == persona) WellnessGreenDark else Color(0xFF64748B)
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    accentColor: Color = WellnessGreen,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = accentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "active customer", "attended", "checked-in", "renewed", "completed", "done" ->
            Pair(WellnessGreenContainer, WellnessGreenDark)
        "at risk", "missed", "cancelled", "failed" ->
            Pair(Color(0xFFFFE4E6), Color(0xFFBE123C))
        "renewal due", "hot", "urgent", "high" ->
            Pair(WellnessOrangeContainer, WellnessOrange)
        "appointment scheduled", "scheduled", "warm" ->
            Pair(WellnessPurpleContainer, WellnessPurpleAccent)
        "whatsapp started" ->
            Pair(Color(0xFFDCFCE7), WhatsAppGreen)
        else ->
            Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LeadScoreChip(score: Int, modifier: Modifier = Modifier) {
    val (label, bg, fg) = when {
        score >= 76 -> Triple("Priority $score", WellnessOrangeContainer, WellnessOrange)
        score >= 51 -> Triple("Hot $score", Color(0xFFFEF3C7), Color(0xFFB45309))
        score >= 26 -> Triple("Warm $score", Color(0xFFE0E7FF), Color(0xFF4338CA))
        else -> Triple("Cold $score", Color(0xFFF1F5F9), Color(0xFF64748B))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = label, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
