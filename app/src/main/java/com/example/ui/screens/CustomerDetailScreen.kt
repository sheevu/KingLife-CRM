package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ProgressRecordEntity
import com.example.ui.components.KpiCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.WellnessCreamBg
import com.example.ui.theme.WellnessGreen
import com.example.ui.theme.WellnessGreenContainer
import com.example.ui.theme.WellnessGreenDark
import com.example.ui.theme.WellnessLime
import com.example.ui.theme.WellnessOrange
import com.example.ui.theme.WellnessOrangeContainer
import com.example.ui.theme.WellnessYellow
import com.example.ui.theme.WellnessYellowSoft
import com.example.ui.viewmodel.WellnessCrmViewModel

@Composable
fun CustomerDetailScreen(
    viewModel: WellnessCrmViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedCustomerId.collectAsStateWithLifecycle()
    val progressRecords by viewModel.selectedCustomerProgress.collectAsStateWithLifecycle()
    val visits by viewModel.visits.collectAsStateWithLifecycle()

    val customer = customers.find { it.id == selectedId } ?: customers.firstOrNull()

    var showAddProgress by remember { mutableStateOf(false) }
    var newWeight by remember { mutableStateOf("74.2") }
    var newWaist by remember { mutableStateOf("86.5") }
    var newEnergy by remember { mutableStateOf(9f) }
    var newNotes by remember { mutableStateOf("Consistent calorie deficit and brisk walking") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessCreamBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Customer Picker Selector Row
        item {
            Column {
                Text(
                    text = "Select Client (Customer 360)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(customers) { c ->
                        FilterChip(
                            selected = c.id == (customer?.id ?: ""),
                            onClick = { viewModel.selectCustomer(c.id) },
                            label = { Text(c.name, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        if (customer != null) {
            // Customer Header Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth().testTag("customer_360_header")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(WellnessGreenContainer)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = WellnessGreenDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = customer.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                    Text(text = customer.locality, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            StatusBadge(status = customer.stage)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Customer Metadata Details
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(text = "Phone", fontSize = 11.sp, color = Color.Gray)
                                Text(text = customer.phone, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text(text = "Coach", fontSize = 11.sp, color = Color.Gray)
                                Text(text = customer.assignedCoach, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WellnessGreen)
                            }
                            Column {
                                Text(text = "Enrolled", fontSize = 11.sp, color = Color.Gray)
                                Text(text = customer.joinedDate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(WellnessOrangeContainer.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Spa, contentDescription = null, tint = WellnessOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = customer.programEnrolled,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WellnessOrange
                                )
                            }
                        }
                    }
                }
            }

            // Key Metrics: Visits, Streak, Lifetime Value
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KpiCard(
                        title = "Total Visits",
                        value = "${customer.totalVisits}",
                        subtitle = "Target: 24 visits",
                        icon = Icons.Default.DateRange,
                        accentColor = WellnessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Current Streak",
                        value = "${customer.currentStreak} 🔥",
                        subtitle = "Consistent client",
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = WellnessOrange,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Lifetime Value",
                        value = "₹${customer.lifetimeValueInr.toInt()}",
                        subtitle = "Platinum",
                        icon = Icons.Default.Star,
                        accentColor = WellnessYellow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Weight & Wellness Progress Journey Chart
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Weight & Inch Loss Journey",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WellnessGreenDark
                                )
                                Text(
                                    text = "Progressive clinical records at Lucknow center",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { showAddProgress = !showAddProgress },
                                colors = ButtonDefaults.buttonColors(containerColor = WellnessGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Entry", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Summary Comparison
                        val firstRecord = progressRecords.firstOrNull()
                        val lastRecord = progressRecords.lastOrNull()

                        if (firstRecord != null && lastRecord != null) {
                            val weightDelta = lastRecord.weightKg - firstRecord.weightKg
                            val waistDelta = lastRecord.waistCm - firstRecord.waistCm

                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WellnessCreamBg)
                                    .padding(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Starting", fontSize = 11.sp, color = Color.Gray)
                                    Text("${firstRecord.weightKg} kg", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Waist: ${firstRecord.waistCm}cm", fontSize = 11.sp, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Current", fontSize = 11.sp, color = Color.Gray)
                                    Text("${lastRecord.weightKg} kg", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WellnessGreen)
                                    Text("Waist: ${lastRecord.waistCm}cm", fontSize = 11.sp, color = WellnessGreen)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Total Loss", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = "${"%.1f".format(weightDelta)} kg",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = WellnessOrange
                                    )
                                    Text(
                                        text = "${"%.1f".format(waistDelta)} cm",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WellnessOrange
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Custom Trend Line Canvas
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .padding(horizontal = 8.dp)
                            ) {
                                if (progressRecords.size >= 2) {
                                    val maxW = progressRecords.maxOf { it.weightKg }
                                    val minW = progressRecords.minOf { it.weightKg } - 1.0
                                    val range = if (maxW - minW == 0.0) 1.0 else maxW - minW
                                    val stepX = size.width / (progressRecords.size - 1)

                                    val path = Path()
                                    progressRecords.forEachIndexed { index, rec ->
                                        val x = index * stepX
                                        val y = (1.0 - (rec.weightKg - minW) / range).toFloat() * (size.height - 20f) + 10f

                                        if (index == 0) {
                                            path.moveTo(x, y)
                                        } else {
                                            path.lineTo(x, y)
                                        }

                                        // Draw data point circles
                                        drawCircle(
                                            color = WellnessGreen,
                                            radius = 5.dp.toPx(),
                                            center = Offset(x, y)
                                        )
                                    }

                                    drawPath(
                                        path = path,
                                        color = WellnessGreen,
                                        style = Stroke(width = 3.dp.toPx())
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Timeline Labels
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                progressRecords.forEach { rec ->
                                    Text(text = rec.recordDate, fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            // Log New Progress Form Card
            if (showAddProgress) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WellnessGreen),
                        modifier = Modifier.fillMaxWidth().testTag("add_progress_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Log Consultation Progress & Scans",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = WellnessGreenDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newWeight,
                                    onValueChange = { newWeight = it },
                                    label = { Text("Weight (kg)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = newWaist,
                                    onValueChange = { newWaist = it },
                                    label = { Text("Waist (cm)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Client Energy Rating: ${newEnergy.toInt()}/10", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = newEnergy,
                                onValueChange = { newEnergy = it },
                                valueRange = 1f..10f,
                                steps = 8,
                                colors = SliderDefaults.colors(thumbColor = WellnessGreen, activeTrackColor = WellnessGreen)
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newNotes,
                                onValueChange = { newNotes = it },
                                label = { Text("Coach Observations & Nutrition Adjustments") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val w = newWeight.toDoubleOrNull() ?: 74.0
                                    val waist = newWaist.toDoubleOrNull() ?: 86.0
                                    viewModel.logProgress(w, waist, newEnergy.toInt(), newNotes)
                                    showAddProgress = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WellnessGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("save_progress_button")
                            ) {
                                Text("Save Progress Record", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Historical Visit Logs
            item {
                Text(
                    text = "Center Visit Log & Attendance History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WellnessGreenDark
                )
            }

            val customerVisits = visits.filter { it.customerId == customer.id }
            if (customerVisits.isEmpty()) {
                item {
                    Text(text = "No prior visit records logged yet.", fontSize = 12.sp, color = Color.Gray)
                }
            } else {
                items(customerVisits) { v ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Visit #${v.visitNumber} • ${v.visitDate}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                StatusBadge(status = v.attendanceStatus)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Coach: ${v.coachName} • In: ${v.checkInTime} • Out: ${v.checkOutTime ?: "In Progress"}", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "Notes: ${v.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
