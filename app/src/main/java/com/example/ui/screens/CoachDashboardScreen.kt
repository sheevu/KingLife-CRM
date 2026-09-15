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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.components.StatusBadge
import com.example.ui.theme.WellnessCreamBg
import com.example.ui.theme.WellnessGreen
import com.example.ui.theme.WellnessGreenContainer
import com.example.ui.theme.WellnessGreenDark
import com.example.ui.theme.WellnessOrange
import com.example.ui.theme.WellnessOrangeContainer
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.WellnessCrmViewModel

@Composable
fun CoachDashboardScreen(
    viewModel: WellnessCrmViewModel,
    modifier: Modifier = Modifier
) {
    val appointments by viewModel.appointments.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()

    val atRiskCustomers = customers.filter { it.stage == "At Risk" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessCreamBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Coach Profile Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(WellnessGreenContainer)
                    ) {
                        Text(text = "AV", fontWeight = FontWeight.ExtraBold, color = WellnessGreenDark, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Dr. Ananya Verma", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(text = "Chief Wellness Coach • Lucknow Center", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(WellnessGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Active Shift", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WellnessGreenDark)
                    }
                }
            }
        }

        // At Risk Retention Alerts
        if (atRiskCustomers.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Retention Alert: Inactive Lucknow Clients (${atRiskCustomers.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF9F1239)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Clients with 3+ visits absent for 7+ days. Rule recommends WhatsApp or direct call.",
                            fontSize = 11.sp,
                            color = Color(0xFFBE123C)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        atRiskCustomers.forEach { cust ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column {
                                    Text(text = cust.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "Last visit: ${cust.lastVisitDate}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Button(
                                    onClick = {
                                        viewModel.triggerWhatsAppForLead(
                                            com.example.data.model.LeadEntity(
                                                id = cust.id,
                                                name = cust.name,
                                                phone = cust.phone,
                                                goal = "Win-back Re-engagement",
                                                stage = "At Risk"
                                            )
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Nudge", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Today's Client Consultations
        item {
            Column {
                Text(
                    text = "Today's Consultations & Appointments",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WellnessGreenDark
                )
                Text(
                    text = "Track attendance and log direct consultation progress",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(appointments) { apt ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = apt.customerOrLeadName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        StatusBadge(status = apt.status)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = apt.serviceName, fontSize = 13.sp, color = WellnessOrange, fontWeight = FontWeight.SemiBold)
                    Text(text = "${apt.appointmentDate} • ${apt.timeSlot}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Notes: ${apt.notes}", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { viewModel.updateAppointmentStatus(apt.id, "Attended") },
                            colors = ButtonDefaults.buttonColors(containerColor = WellnessGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Attended", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.updateAppointmentStatus(apt.id, "Missed") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Missed", fontSize = 12.sp, color = Color.Red)
                        }
                    }
                }
            }
        }

        // Coach Tasks & Checklist
        item {
            Text(
                text = "Clinical & Follow-up Tasks",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WellnessGreenDark
            )
        }

        items(tasks) { task ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Checkbox(
                        checked = task.status == "Done",
                        onCheckedChange = { viewModel.toggleTask(task.id, task.status) },
                        colors = CheckboxDefaults.colors(checkedColor = WellnessGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (task.status == "Done") Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                        Text(text = "Client: ${task.relatedEntityName} • Due: ${task.dueDate}", fontSize = 11.sp, color = Color.Gray)
                    }
                    StatusBadge(status = task.priority)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
