package com.smartfarm.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartfarm.app.api.NotificationRecord
import com.smartfarm.app.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController, viewModel: WorkerViewModel) {
    // Observe real notifications from the database via ViewModel
    val alerts by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Alerts", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF2E7D32))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF1F8E9)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (alerts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.NotificationsOff, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Spacer(Modifier.height(8.dp))
                        Text("No notifications for today", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Show newest alerts at the top (reversed)
                    items(alerts.reversed()) { alert ->
                        AlertItemCard(alert)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertItemCard(alert: NotificationRecord) {
    // Dynamic icon and color based on notification content
    val (icon, tintColor) = when {
        alert.title.contains("Task", true) -> Icons.Default.Work to Color(0xFF1976D2)
        alert.title.contains("Payment", true) -> Icons.Default.Payments to Color(0xFF2E7D32)
        alert.title.contains("Attendance", true) -> Icons.Default.FactCheck to Color(0xFF388E3C)
        else -> Icons.Default.NotificationsActive to Color(0xFFF57C00)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = tintColor.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = alert.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = alert.message, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
                // Show only the date portion (first 10 characters)
                val dateStr = if (alert.createdAt.length >= 10) alert.createdAt.take(10) else alert.createdAt
                Text(text = dateStr, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}