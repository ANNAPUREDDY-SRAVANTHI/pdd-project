package com.smartfarm.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartfarm.app.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Admin") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F8E9))) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF2E7D32))
                }
                Text(text = "Create Account", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2), onClick = { selectedRole = "Admin" }, selected = selectedRole == "Admin", colors = SegmentedButtonDefaults.colors(activeContainerColor = Color(0xFF2E7D32), activeContentColor = Color.White)) { Text("Admin") }
                        SegmentedButton(shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2), onClick = { selectedRole = "Worker" }, selected = selectedRole == "Worker", colors = SegmentedButtonDefaults.colors(activeContainerColor = Color(0xFF2E7D32), activeContentColor = Color.White)) { Text("Worker") }
                    }

                    OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username / Phone Number") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                        }
                    })

                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), visualTransformation = PasswordVisualTransformation())

                    if (message.isNotEmpty()) { Text(text = message, color = Color.Red, fontSize = 12.sp) }

                    if (isLoading) { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color(0xFF2E7D32)) } else {
                        Button(
                            onClick = {
                                if (username.isNotBlank() && password == confirmPassword) {
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val data = mapOf("username" to username, "password" to password, "role" to selectedRole)
                                            val response = RetrofitClient.instance.register(data)
                                            isLoading = false
                                            if (response.isSuccessful) { navController.navigate("login") } else { message = "Registration failed." }
                                        } catch (e: Exception) { isLoading = false; message = "No Connection." }
                                    }
                                } else { message = "Passwords do not match." }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) { Text("Register Now", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}