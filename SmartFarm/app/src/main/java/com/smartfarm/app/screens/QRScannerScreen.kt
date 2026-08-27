package com.smartfarm.app.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(navController: NavController) {
    var isFlashOn by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Simulated Camera Preview (just dark background)
        
        // Custom Overlay for Scanner Frame
        ScannerOverlay()

        // UI Components on top of the scanner
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Scan QR Code",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isFlashOn = !isFlashOn }) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Flashlight",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Instructions Text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Align QR code within the frame",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scanner will start automatically",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Simulation Button (Frontend only requirement)
                Button(
                    onClick = { navController.navigate("attendanceSuccess") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simulate Successful Scan")
                }
            }
        }
    }
}

@Composable
fun ScannerOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val scannerSize = 250.dp.toPx()
        val left = (size.width - scannerSize) / 2
        val top = (size.height - scannerSize) / 2
        val rect = Rect(Offset(left, top), Size(scannerSize, scannerSize))
        val cornerRadius = CornerRadius(24.dp.toPx())

        // Create a path for the full screen with a hole in the middle
        val backgroundPath = Path().apply {
            addRect(Rect(0f, 0f, size.width, size.height))
        }
        val holePath = Path().apply {
            addRoundRect(RoundRect(rect, cornerRadius))
        }
        
        val overlayPath = Path.combine(
            operation = PathOperation.Difference,
            path1 = backgroundPath,
            path2 = holePath
        )

        // Draw the semi-transparent overlay
        drawPath(
            path = overlayPath,
            color = Color.Black.copy(alpha = 0.7f)
        )

        // Draw the green scanner frame corners
        val lineLength = 40.dp.toPx()
        val strokeWidth = 4.dp.toPx()
        val color = Color(0xFF4CAF50)

        // Top Left
        drawLine(color, Offset(left, top + lineLength), Offset(left, top), strokeWidth)
        drawLine(color, Offset(left, top), Offset(left + lineLength, top), strokeWidth)

        // Top Right
        drawLine(color, Offset(left + scannerSize - lineLength, top), Offset(left + scannerSize, top), strokeWidth)
        drawLine(color, Offset(left + scannerSize, top), Offset(left + scannerSize, top + lineLength), strokeWidth)

        // Bottom Left
        drawLine(color, Offset(left, top + scannerSize - lineLength), Offset(left, top + scannerSize), strokeWidth)
        drawLine(color, Offset(left, top + scannerSize), Offset(left + lineLength, top + scannerSize), strokeWidth)

        // Bottom Right
        drawLine(color, Offset(left + scannerSize - lineLength, top + scannerSize), Offset(left + scannerSize, top + scannerSize), strokeWidth)
        drawLine(color, Offset(left + scannerSize, top + scannerSize), Offset(left + scannerSize, top + scannerSize - lineLength), strokeWidth)
    }
}
