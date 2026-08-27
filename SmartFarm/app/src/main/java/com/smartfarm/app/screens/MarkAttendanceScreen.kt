package com.smartfarm.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartfarm.app.viewmodel.WorkerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkAttendanceScreen(navController: NavController, viewModel: WorkerViewModel) {
    // 1. Observe real-time worker list and format today's date
    val workers by viewModel.allWorkers.collectAsState()
    val todayDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

    // tracks choices (WorkerId -> Status) before clicking "Submit"
    val localSelection = remember { mutableStateMapOf<Int, String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mark Attendance", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2E7D32)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF1F8E9) // Agriculture light green
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Screen Header
            Text(
                text = "Today's Farm Duty: $todayDate",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = "Select P for Present or A for Absent for each worker",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // If no workers are added, show a placeholder
            if (workers.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No workers registered in your list.", color = Color.Gray)
                }
            } else {
                // Real-time Worker List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(workers) { worker ->
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = worker.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Role: ${worker.role}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                // P/A Selection Toggle
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AttendancePABtn(
                                        text = "P",
                                        isSelected = localSelection[worker.id] == "Present",
                                        color = Color(0xFF2E7D32) // Green for Present
                                    ) {
                                        localSelection[worker.id] = "Present"
                                    }

                                    AttendancePABtn(
                                        text = "A",
                                        isSelected = localSelection[worker.id] == "Absent",
                                        color = Color(0xFFD32F2F) // Red for Absent
                                    ) {
                                        localSelection[worker.id] = "Absent"
                                    }
                                }
                            }
                        }
                    }
                }

                // Final Submission Button
                Button(
                    onClick = {
                        if (localSelection.isNotEmpty()) {
                            // Logic: Saves history with today's date stamp
                            viewModel.submitAttendance(todayDate, localSelection.toMap())
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    enabled = localSelection.isNotEmpty() // Active only if markings are made
                ) {
                    Text("Submit Today's Attendance", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AttendancePABtn(
    text: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(42.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) color else Color.Transparent,
            contentColor = if (isSelected) Color.White else color
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) color else Color.LightGray
        )
    ) {
        Text(text = text, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
    }
}