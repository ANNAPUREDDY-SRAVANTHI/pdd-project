package com.smartfarm.app.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceReportScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Attendance Report",
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
                actions = {
                    IconButton(onClick = { /* Export PDF */ }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export", tint = Color(0xFF2E7D32))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
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
            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnalyticsCard(
                    modifier = Modifier.weight(1f),
                    label = "Avg. Attendance",
                    value = "88%",
                    color = Color(0xFF2E7D32)
                )
                AnalyticsCard(
                    modifier = Modifier.weight(1f),
                    label = "Total Present",
                    value = "2,450",
                    color = Color(0xFF1976D2)
                )
            }

            // Pie Chart Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Attendance Distribution", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                        AttendancePieChart()
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total", fontSize = 12.sp, color = Color.Gray)
                            Text("100%", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ChartLegend(label = "Present", color = Color(0xFF2E7D32))
                        ChartLegend(label = "Absent", color = Color(0xFFD32F2F))
                        ChartLegend(label = "Late", color = Color(0xFFF57C00))
                    }
                }
            }

            // Bar Graph Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Daily Trends (Last 7 Days)", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(24.dp))
                    AttendanceBarGraph()
                }
            }

            // Attendance Table
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Recent Worker Attendance", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    AttendanceTable()
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AnalyticsCard(modifier: Modifier = Modifier, label: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AttendancePieChart() {
    Canvas(modifier = Modifier.size(160.dp)) {
        // Simple mock pie chart using arcs
        drawArc(
            color = Color(0xFF2E7D32),
            startAngle = -90f,
            sweepAngle = 270f, // 75% Present
            useCenter = false,
            style = Stroke(width = 40f, cap = StrokeCap.Round)
        )
        drawArc(
            color = Color(0xFFD32F2F),
            startAngle = 180f,
            sweepAngle = 45f, // 12.5% Absent
            useCenter = false,
            style = Stroke(width = 40f, cap = StrokeCap.Round)
        )
        drawArc(
            color = Color(0xFFF57C00),
            startAngle = 225f,
            sweepAngle = 45f, // 12.5% Late
            useCenter = false,
            style = Stroke(width = 40f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ChartLegend(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun AttendanceBarGraph() {
    val barData = listOf(0.8f, 0.9f, 0.7f, 0.95f, 0.85f, 0.6f, 0.88f)
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    
    Row(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        barData.forEachIndexed { index, value ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight(value)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(Color(0xFFC8E6C9))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f) // Solid part
                            .align(Alignment.BottomCenter)
                            .background(Color(0xFF2E7D32))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = days[index], fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun AttendanceTable() {
    val tableData = listOf(
        Triple("Ramesh K.", "24 Oct", "Present"),
        Triple("Suresh R.", "24 Oct", "Late"),
        Triple("Anita D.", "24 Oct", "Present"),
        Triple("Mahesh B.", "23 Oct", "Absent"),
        Triple("Priya S.", "23 Oct", "Present")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Header
        Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F8E9)).padding(8.dp)) {
            Text("Worker", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Status", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
        }
        // Rows
        tableData.forEach { (name, date, status) ->
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(name, modifier = Modifier.weight(1f), fontSize = 12.sp)
                Text(date, modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
                Text(
                    status,
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    color = when(status) {
                        "Present" -> Color(0xFF2E7D32)
                        "Late" -> Color(0xFFF57C00)
                        else -> Color(0xFFD32F2F)
                    },
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE8F5E9))
        }
    }
}
