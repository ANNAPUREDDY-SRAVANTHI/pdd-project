package com.smartfarm.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartfarm.app.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceHistoryScreen(navController: NavController, viewModel: WorkerViewModel) {
    // 1. Observe real data from the Django Backend
    val history by viewModel.attendanceHistory.collectAsState()
    val workers by viewModel.allWorkers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // 2. Logic: Group records by date and filter by search query
    val filteredHistory = history.filter { record ->
        val workerName = workers.find { it.id == record.worker }?.name ?: ""
        workerName.contains(searchQuery, ignoreCase = true)
    }

    // Sort by date (newest first) and group
    val groupedHistory = filteredHistory.groupBy { it.date }.toList().sortedByDescending { it.first }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Attendance History", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF2E7D32))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { navController.navigate("adminDashboard") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.FactCheck, null) },
                    label = { Text("Attendance") },
                    selected = true,
                    onClick = { /* Stay here */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, null) },
                    label = { Text("Reports") },
                    selected = false,
                    onClick = { navController.navigate("reports") }
                )
            }
        },
        containerColor = Color(0xFFF1F8E9) // Light agricultural green
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search by worker name...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2E7D32)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            if (history.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No attendance records found in database.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groupedHistory.forEach { (date, records) ->
                        item {
                            Text(
                                text = date,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(records) { record ->
                            val worker = workers.find { it.id == record.worker }
                            HistoryCard(
                                name = worker?.name ?: "Unknown",
                                role = worker?.role ?: "No Role",
                                date = record.date,
                                status = record.status,
                                time = "" // Time can be added to model later if needed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(name: String, role: String, date: String, status: String, time: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Worker Avatar Initial
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFC8E6C9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (name.isNotEmpty()) name.first().toString() else "?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = role, fontSize = 12.sp, color = Color.Gray)
            }

            // Status Indicator
            val statusColor = if (status == "Present") Color(0xFF2E7D32) else Color(0xFFD32F2F)
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
        }
    }
}
