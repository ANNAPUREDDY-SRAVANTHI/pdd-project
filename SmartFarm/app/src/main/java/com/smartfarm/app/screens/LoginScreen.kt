package com.smartfarm.app.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartfarm.app.api.RetrofitClient
import com.smartfarm.app.api.TokenManager
import com.smartfarm.app.viewmodel.WorkerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: WorkerViewModel) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Admin") }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F8E9))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Your Original Header Section
            Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "Smart Farm",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Admin/Worker Switch
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            onClick = { selectedRole = "Admin" },
                            selected = selectedRole == "Admin",
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = Color(0xFF2E7D32),
                                activeContentColor = Color.White
                            )
                        ) { Text("Admin") }
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            onClick = { selectedRole = "Worker" },
                            selected = selectedRole == "Worker",
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = Color(0xFF2E7D32),
                                activeContentColor = Color.White
                            )
                        ) { Text("Worker") }
                    }

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        }
                    )

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = Color(0xFF2E7D32)
                        )
                    } else {
                        Button(
                            onClick = {
                                if (username.isNotBlank() && password.isNotBlank()) {
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val res = RetrofitClient.instance.login(mapOf("username" to username, "password" to password))
                                            if (res.isSuccessful) {
                                                val token = res.body()?.get("token") ?: ""
                                                tokenManager.saveToken(token)
                                                viewModel.setLoggedInUser(username)
                                                viewModel.refreshAll()
                                                if (selectedRole == "Admin") {
                                                    navController.navigate("adminDashboard") { popUpTo("login") { inclusive = true } }
                                                } else {
                                                    navController.navigate("workerDashboard") { popUpTo("login") { inclusive = true } }
                                                }
                                            } else {
                                                Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show()
                                        } finally { isLoading = false }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("Login", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Reset Password Option inside the card
                    TextButton(
                        onClick = { navController.navigate("forgotPassword") },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Forgot Password?", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- THE ORIGINAL REGISTER OPTION ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = Color.Gray)
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Register Now", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}