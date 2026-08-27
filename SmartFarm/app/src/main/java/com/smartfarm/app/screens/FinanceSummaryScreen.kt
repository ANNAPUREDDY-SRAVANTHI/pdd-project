package com.smartfarm.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.smartfarm.app.api.Worker
import com.smartfarm.app.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceSummaryScreen(navController: NavController, viewModel: WorkerViewModel) {
    val workers by viewModel.allWorkers.collectAsState()
    val totalFarmDebt = workers.sumOf { viewModel.getBalanceDue(it.id) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Finance", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = Color(0xFF1B5E20))
            )
        },
        containerColor = Color(0xFFF1F8E9)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Labor Debt", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Text("₹ ${"%.2f".format(totalFarmDebt)}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("Amount you still owe to all workers", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            }

            Text("Worker Balance Sheet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(workers) { worker ->
                    WorkerFinanceCard(worker, viewModel)
                }
            }
        }
    }
}

@Composable
fun WorkerFinanceCard(worker: Worker, viewModel: WorkerViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var payAmount by remember { mutableStateOf("") }
    val earned = viewModel.getTotalEarned(worker.id)
    val paid = viewModel.getTotalPaid(worker.id)
    val balance = viewModel.getBalanceDue(worker.id)

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(worker.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(worker.role, color = Color.Gray, fontSize = 12.sp)
                }
                Text(text = "Due: ₹${balance.toInt()}", color = if(balance > 0) Color(0xFFD32F2F) else Color(0xFF2E7D32), fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Total Earned: ₹${earned.toInt()}", fontSize = 12.sp)
                    Text("Total Paid: ₹${paid.toInt()}", fontSize = 12.sp, color = Color.Gray)
                }
                Button(onClick = { showDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), shape = RoundedCornerShape(8.dp)) {
                    Text("Record Pay", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Record Payment for ${worker.name}") },
            text = {
                OutlinedTextField(value = payAmount, onValueChange = { payAmount = it }, label = { Text("Amount paid?") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            },
            confirmButton = {
                Button(onClick = {
                    val amount = payAmount.toDoubleOrNull() ?: 0.0
                    if (amount > 0) { viewModel.recordPayment(worker.id, amount); showDialog = false; payAmount = "" }
                }) { Text("Confirm") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}