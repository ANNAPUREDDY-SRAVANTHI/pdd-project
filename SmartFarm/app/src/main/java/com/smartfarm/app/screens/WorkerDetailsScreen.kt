package com.smartfarm.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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
fun WorkerDetailsScreen(navController: NavController, workerId: Int, viewModel: WorkerViewModel) {
    val workers by viewModel.allWorkers.collectAsState(initial = emptyList())
    val worker = workers.find { it.id == workerId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Worker Details", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20)) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFF2E7D32)) } },
                actions = {
                    IconButton(onClick = { navController.navigate("editWorker/$workerId") }) { Icon(Icons.Default.Edit, null, tint = Color(0xFF2E7D32)) }
                    IconButton(onClick = { navController.navigate("deleteConfirmation/$workerId") }) { Icon(Icons.Default.Delete, null, tint = Color(0xFFD32F2F)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF1F8E9)
    ) { padding ->
        if (worker == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Worker not found") }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFC8E6C9)), contentAlignment = Alignment.Center) {
                    Text(worker.name.first().toString(), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                }
                Spacer(Modifier.height(24.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailItem("Name", worker.name)
                        DetailItem("Phone", worker.phone)
                        DetailItem("Address", worker.address)
                        DetailItem("Role", worker.role)
                        DetailItem("Wage", "₹${worker.dailyWage}")
                        DetailItem("Joined", worker.joinDate)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}