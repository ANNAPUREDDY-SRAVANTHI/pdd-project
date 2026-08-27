package com.smartfarm.app.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartfarm.app.viewmodel.WorkerViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsDashboard(navController: NavController, viewModel: WorkerViewModel) {
    val context = LocalContext.current
    val workers by viewModel.allWorkers.collectAsState()

    var selectedItem by remember { mutableIntStateOf(3) } // Default to Reports tab
    val items = listOf("Home", "Attendance", "Wages", "Reports")
    val navIcons = listOf(
        Icons.Default.Home,
        Icons.AutoMirrored.Filled.FactCheck,
        Icons.Default.Payments,
        Icons.Default.BarChart
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reports & Analytics",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
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
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(navIcons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            when (index) {
                                0 -> navController.navigate("adminDashboard")
                                1 -> navController.navigate("attendanceDashboard")
                                2 -> navController.navigate("financeSummary")
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color(0xFF2E7D32),
                            indicatorColor = Color(0xFF2E7D32),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFFF1F8E9)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Farm Performance",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )

            // Attendance Analytics Card (Live Data)
            ReportCategoryCard(
                title = "Attendance Summary",
                description = "Total registered workers: ${workers.size}\nTrack daily presence and trends.",
                icon = Icons.AutoMirrored.Filled.FactCheck,
                color = Color(0xFFE8F5E9),
                iconColor = Color(0xFF2E7D32),
                onClick = { navController.navigate("attendanceDashboard") }
            )

            // Wage Analytics Card (Live Data)
            val totalDue = workers.sumOf { viewModel.getBalanceDue(it.id) }
            ReportCategoryCard(
                title = "Wage Analytics",
                description = "Total farm debt: ₹${totalDue.toInt()}\nView pending salary and payment history.",
                icon = Icons.Default.Payments,
                color = Color(0xFFE3F2FD),
                iconColor = Color(0xFF1976D2),
                onClick = { navController.navigate("financeSummary") }
            )

            // --- EXPORT SECTION (FUNCTIONAL) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Export to WhatsApp/Excel", fontWeight = FontWeight.Bold)
                        Text(text = "Share full wage & labor summary", fontSize = 12.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = {
                            // Logic: Create a text-based CSV summary
                            try {
                                val header = "SMART FARM REPORT - ${Calendar.getInstance().time}\n\n"
                                val tableHeader = "Name | Phone | Role | Earned | Paid | Due\n"
                                val tableDivider = "-------------------------------------------\n"

                                val reportData = workers.joinToString("\n") { worker ->
                                    val earned = viewModel.getTotalEarned(worker.id).toInt()
                                    val paid = viewModel.getTotalPaid(worker.id).toInt()
                                    val due = viewModel.getBalanceDue(worker.id).toInt()
                                    "${worker.name} | ${worker.phone} | ${worker.role} | ₹$earned | ₹$paid | ₹$due"
                                }

                                val fullReport = header + tableHeader + tableDivider + reportData

                                // Create Share Intent
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, fullReport)
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, "Send Farm Report via")
                                context.startActivity(shareIntent)

                            } catch (e: Exception) {
                                Toast.makeText(context, "Error generating report", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Share", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCategoryCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
