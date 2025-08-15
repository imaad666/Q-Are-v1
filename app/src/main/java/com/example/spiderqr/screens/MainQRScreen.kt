package com.example.spiderqr.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spiderqr.data.UserPreferences
import com.example.spiderqr.utils.QRCodeGenerator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainQRScreen(onNavigateToSettings: () -> Unit) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var cornerRadius by remember { mutableStateOf(0f) }
    var userInitials by remember { mutableStateOf("") }
    var showInitialsDialog by remember { mutableStateOf(false) }
    
    val rollNumber = userPreferences.getRollNumber() ?: ""
    val studentName = userPreferences.getStudentName() ?: ""
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val qrContent = QRCodeGenerator.generateTodaysQRCode(rollNumber, studentName)

    fun generateQR() {
        qrBitmap = QRCodeGenerator.generateQRBitmap(
            content = qrContent,
            size = 512,
            foregroundColor = selectedColor.toArgb(),
            cornerRadius = cornerRadius,
            userInitials = userInitials.takeIf { it.isNotEmpty() }
        )
    }

    // Load saved preferences
    LaunchedEffect(Unit) {
        selectedColor = Color(userPreferences.getQRColor())
        cornerRadius = userPreferences.getCornerRadius()
        userInitials = userPreferences.getUserInitials() ?: ""
        generateQR()
    }

    fun savePreferences() {
        userPreferences.saveQRColor(selectedColor.toArgb())
        userPreferences.saveCornerRadius(cornerRadius)
        userPreferences.saveUserInitials(userInitials)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Today's QR",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = todayDate,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Row {
                IconButton(onClick = { generateQR() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh QR",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // QR Code Display
        Card(
            modifier = Modifier.size(280.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                qrBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Today's QR Code",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                } ?: CircularProgressIndicator()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Student Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = studentName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Roll: $rollNumber",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Customization Section
        Text(
            text = "Customize Your QR",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Color Picker
        Text(
            text = "QR Color",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(qrColors) { color ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color)
                        .padding(if (selectedColor == color) 4.dp else 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.size(if (selectedColor == color) 40.dp else 48.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = color),
                        onClick = {
                            selectedColor = color
                            savePreferences()
                            generateQR()
                        }
                    ) {}
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Corner Radius Slider
        Text(
            text = "Corner Radius: ${cornerRadius.toInt()}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = cornerRadius,
            onValueChange = { cornerRadius = it },
            onValueChangeFinished = {
                savePreferences()
                generateQR()
            },
            valueRange = 0f..50f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Initials Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Initials: ${userInitials.takeIf { it.isNotEmpty() } ?: "None"}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Button(
                onClick = { showInitialsDialog = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Edit")
            }
        }
    }

    // Initials Dialog
    if (showInitialsDialog) {
        var tempInitials by remember { mutableStateOf(userInitials) }
        
        AlertDialog(
            onDismissRequest = { showInitialsDialog = false },
            title = { Text("Enter Your Initials") },
            text = {
                OutlinedTextField(
                    value = tempInitials,
                    onValueChange = { if (it.length <= 2) tempInitials = it.uppercase() },
                    label = { Text("Initials (max 2 letters)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userInitials = tempInitials
                        savePreferences()
                        generateQR()
                        showInitialsDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInitialsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private val qrColors = listOf(
    Color.Black,
    Color(0xFF1976D2), // Blue
    Color(0xFF388E3C), // Green
    Color(0xFFD32F2F), // Red
    Color(0xFF7B1FA2), // Purple
    Color(0xFFE64A19), // Deep Orange
    Color(0xFF00796B), // Teal
    Color(0xFF5D4037), // Brown
)
