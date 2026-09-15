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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.viewmodel.WellnessCrmViewModel

@Composable
fun ReceptionDashboardScreen(
    viewModel: WellnessCrmViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val visits by viewModel.visits.collectAsStateWithLifecycle()
    val appointments by viewModel.appointments.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var walkInName by remember { mutableStateOf("") }
    var walkInPhone by remember { mutableStateOf("") }
    var walkInGoal by remember { mutableStateOf("Weight Management") }
    var walkInLocality by remember { mutableStateOf("Gomti Nagar") }

    val activeCheckedInVisits = visits.filter { it.attendanceStatus == "Checked-In" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WellnessCreamBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Reception Terminal Title
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Reception & Check-in Terminal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WellnessGreenDark
                    )
                    Text(
                        text = "Front Desk Terminal • Gomti Nagar Center, Lucknow",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(WellnessGreenContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Front Desk",
                        color = WellnessGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Fast Client Check-in Search
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Fast One-Tap Check-In",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = WellnessGreenDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search client name or +91 phone...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = WellnessGreen) },
                        modifier = Modifier.fillMaxWidth().testTag("reception_search_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val filteredCustomers = if (searchQuery.isBlank()) {
                        customers
                    } else {
                        customers.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.phone.contains(searchQuery)
                        }
                    }

                    filteredCustomers.forEach { cust ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = cust.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = "${cust.phone} • Streak: ${cust.currentStreak} 🔥",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { viewModel.fastCheckIn(cust) },
                                colors = ButtonDefaults.buttonColors(containerColor = WellnessGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("check_in_button_${cust.id}")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Check In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Active Visited Clients at Center
        item {
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
                        Text(
                            text = "Currently in Center (${activeCheckedInVisits.size} Active)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = WellnessOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (activeCheckedInVisits.isEmpty()) {
                        Text(
                            text = "No active clients in center right now. Check in above.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    } else {
                        activeCheckedInVisits.forEach { visit ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = visit.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Coach: ${visit.coachName} • In: ${visit.checkInTime}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Button(
                                    onClick = { viewModel.checkOutVisit(visit.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = WellnessOrange),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Check Out", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Walk-In Lead Registration
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Register Offline Walk-In Lead",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = WellnessGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = walkInName,
                        onValueChange = { walkInName = it },
                        label = { Text("Walk-In Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = walkInPhone,
                        onValueChange = { walkInPhone = it },
                        label = { Text("+91 Mobile Number") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (walkInName.isNotBlank() && walkInPhone.isNotBlank()) {
                                viewModel.updateBasicInfo(walkInName, walkInPhone, "26-35", walkInLocality, "Hinglish")
                                viewModel.submitOnboarding()
                                walkInName = ""
                                walkInPhone = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WellnessGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("register_walkin_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Register Walk-In (+15 Lead Score)", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
