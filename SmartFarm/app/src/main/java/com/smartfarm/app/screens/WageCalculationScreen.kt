package com.smartfarm.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WageCalculationScreen(navController: NavController) {
    // Input states for calculation
    var dailyWage by remember { mutableStateOf("") }
    var overtimeHours by remember { mutableStateOf("") }
    var foodExpense by remember { mutableStateOf("") }

    // Real-time calculation logic
    val dailyWageVal = dailyWage.toDoubleOrNull() ?: 0.0
    val overtimeHoursVal = overtimeHours.toDoubleOrNull() ?: 0.0
    val foodExpenseVal = foodExpense.toDoubleOrNull() ?: 0.0

    // Assuming overtime rate is dailyWage / 8 per hour
    val overtimePay = if (dailyWageVal > 0) (dailyWageVal / 8) * overtimeHoursVal else 0.0
    val totalSalary = dailyWageVal + overtimePay - foodExpenseVal

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Wage Calculation",
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
        containerColor = Color(0xFFF1F8E9) // Agriculture Theme Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Calculation Factors",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    OutlinedTextField(
                        value = dailyWage,
                        onValueChange = { dailyWage = it },
                        label = { Text("Daily Wage (₹)") },
                        leadingIcon = {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFF2E7D32))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = overtimeHours,
                        onValueChange = { overtimeHours = it },
                        label = { Text("Overtime Hours") },
                        leadingIcon = {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF2E7D32))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = foodExpense,
                        onValueChange = { foodExpense = it },
                        label = { Text("Food Expense Deduction (₹)") },
                        leadingIcon = {
                            Icon(Icons.Default.Fastfood, contentDescription = null, tint = Color(0xFF2E7D32))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            // Results Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Estimated Daily Salary",
                        fontSize = 16.sp,
                        color = Color(0xFF388E3C)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹ ${"%.2f".format(totalSalary)}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Base Pay:", color = Color.Gray, fontSize = 14.sp)
                        Text("₹ $dailyWageVal", fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Overtime:", color = Color.Gray, fontSize = 14.sp)
                        Text("+ ₹ ${"%.2f".format(overtimePay)}", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Food Deduction:", color = Color.Gray, fontSize = 14.sp)
                        Text("- ₹ $foodExpenseVal", color = Color.Red, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation to the shared summary screen
            Button(
                onClick = { navController.navigate("financeSummary") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                enabled = dailyWage.isNotBlank() // Only active if data is entered
            ) {
                Text(
                    text = "Confirm & View Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}