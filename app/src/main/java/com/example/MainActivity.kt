package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PersonaSelectorTabs
import com.example.ui.screens.CoachDashboardScreen
import com.example.ui.screens.CustomerDetailScreen
import com.example.ui.screens.KanbanPipelineScreen
import com.example.ui.screens.OwnerDashboardScreen
import com.example.ui.screens.PublicOnboardingScreen
import com.example.ui.screens.ReceptionDashboardScreen
import com.example.ui.screens.WhatsAppAutomationScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WellnessCreamBg
import com.example.ui.theme.WellnessGreen
import com.example.ui.theme.WellnessGreenContainer
import com.example.ui.theme.WellnessGreenDark
import com.example.ui.theme.WellnessOrange
import com.example.ui.theme.WellnessOrangeContainer
import com.example.ui.theme.WellnessYellow
import com.example.ui.viewmodel.AppPersona
import com.example.ui.viewmodel.WellnessCrmViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WellnessAppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessAppRoot(
    viewModel: WellnessCrmViewModel = viewModel()
) {
    val currentPersona by viewModel.currentPersona.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val statusMsg by viewModel.statusMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showLangMenu by remember { mutableStateOf(false) }

    LaunchedEffect(statusMsg) {
        statusMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(WellnessGreen)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = "Brand Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Healthy King LIFE",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = WellnessGreenDark
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = WellnessOrange,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Lucknow Wellness Center",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = WellnessOrange
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        // Language Selector
                        Box {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WellnessGreenContainer)
                                    .clickable { showLangMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Language",
                                        tint = WellnessGreenDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentLang,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WellnessGreenDark
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showLangMenu,
                                onDismissRequest = { showLangMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("English") },
                                    onClick = {
                                        viewModel.setLanguage("English")
                                        showLangMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("हिंदी (Hindi)") },
                                    onClick = {
                                        viewModel.setLanguage("Hindi")
                                        showLangMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Hinglish") },
                                    onClick = {
                                        viewModel.setLanguage("Hinglish")
                                        showLangMenu = false
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Persona Selector Bar
                PersonaSelectorTabs(
                    selectedPersona = currentPersona,
                    onSelectPersona = { viewModel.setPersona(it) }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentPersona == AppPersona.PUBLIC,
                    onClick = { viewModel.setPersona(AppPersona.PUBLIC) },
                    icon = { Icon(Icons.Default.Spa, contentDescription = "Public") },
                    label = { Text("Public", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WellnessGreen,
                        selectedTextColor = WellnessGreen,
                        indicatorColor = WellnessGreenContainer
                    )
                )
                NavigationBarItem(
                    selected = currentPersona == AppPersona.OWNER,
                    onClick = { viewModel.setPersona(AppPersona.OWNER) },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Owner") },
                    label = { Text("Owner", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WellnessGreen,
                        selectedTextColor = WellnessGreen,
                        indicatorColor = WellnessGreenContainer
                    )
                )
                NavigationBarItem(
                    selected = currentPersona == AppPersona.KANBAN,
                    onClick = { viewModel.setPersona(AppPersona.KANBAN) },
                    icon = { Icon(Icons.Default.ViewKanban, contentDescription = "Pipeline") },
                    label = { Text("Pipeline", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WellnessGreen,
                        selectedTextColor = WellnessGreen,
                        indicatorColor = WellnessGreenContainer
                    )
                )
                NavigationBarItem(
                    selected = currentPersona == AppPersona.RECEPTION,
                    onClick = { viewModel.setPersona(AppPersona.RECEPTION) },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Reception") },
                    label = { Text("Check-In", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WellnessGreen,
                        selectedTextColor = WellnessGreen,
                        indicatorColor = WellnessGreenContainer
                    )
                )
                NavigationBarItem(
                    selected = currentPersona == AppPersona.CUSTOMER_PORTAL,
                    onClick = { viewModel.setPersona(AppPersona.CUSTOMER_PORTAL) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Client 360") },
                    label = { Text("Client 360", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WellnessGreen,
                        selectedTextColor = WellnessGreen,
                        indicatorColor = WellnessGreenContainer
                    )
                )
                NavigationBarItem(
                    selected = currentPersona == AppPersona.WHATSAPP_ENGINE,
                    onClick = { viewModel.setPersona(AppPersona.WHATSAPP_ENGINE) },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "WhatsApp") },
                    label = { Text("WhatsApp", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WellnessGreen,
                        selectedTextColor = WellnessGreen,
                        indicatorColor = WellnessGreenContainer
                    )
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentPersona) {
                AppPersona.PUBLIC -> PublicOnboardingScreen(viewModel = viewModel)
                AppPersona.OWNER -> OwnerDashboardScreen(viewModel = viewModel)
                AppPersona.COACH -> CoachDashboardScreen(viewModel = viewModel)
                AppPersona.RECEPTION -> ReceptionDashboardScreen(viewModel = viewModel)
                AppPersona.KANBAN -> KanbanPipelineScreen(viewModel = viewModel)
                AppPersona.CUSTOMER_PORTAL -> CustomerDetailScreen(viewModel = viewModel)
                AppPersona.WHATSAPP_ENGINE -> WhatsAppAutomationScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

