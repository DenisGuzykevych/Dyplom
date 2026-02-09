package com.example.wellminder.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.theme.Typography

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext // Added for open settings if needed

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun HealthConnectScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        viewModel.checkPermissions()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF5FF))
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = Color(0xFFFF8A00),
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onBack() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Approximate height based on screenshot
                .shadow(4.dp, RoundedCornerShape(32.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "З’єднання Health Connect",
                    style = Typography.headlineSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Статус SDK: ${
                            when (viewModel.sdkStatus) {
                                androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE -> "Доступно"
                                androidx.health.connect.client.HealthConnectClient.SDK_UNAVAILABLE -> "Недоступно"
                                androidx.health.connect.client.HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> "Потрібне оновлення"
                                else -> "Невідомо"
                            }
                        }",
                        style = Typography.bodyLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Дозволи: ${if (viewModel.permissionsGranted) "Надано" else "Не надано"}",
                        style = Typography.bodyLarge,
                        color = if (viewModel.permissionsGranted) Color(0xFF4CAF50) else Color.Red
                    )
                    
                    if (viewModel.permissionsGranted) {
                         Spacer(modifier = Modifier.height(16.dp))
                         Text(
                            text = "Прочитано кроків за сьогодні: ${viewModel.steps}",
                            style = Typography.headlineMedium,
                            color = Color(0xFFFF8A00)
                        )
                        
                        if (viewModel.stepsBreakdown.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Джерела (Сума):", style = Typography.titleSmall, color = Color.Gray)
                            viewModel.stepsBreakdown.forEach { (pkg, count) ->
                                Text(
                                    text = "$pkg: $count",
                                    style = Typography.bodySmall,
                                    color = Color.Black
                                )
                            }
                        }
                        
                        if (viewModel.rawRecords.isNotEmpty()) {
                             Spacer(modifier = Modifier.height(16.dp))
                             Text(text = "Детальний журнал:", style = Typography.titleSmall, color = Color.Gray)
                             // Limit to last 5-10 records or show all in a scrollable column? 
                             // Card has specific height. Should make it scrollable or just show first few.
                             // Making the whole column scrollable.
                             viewModel.rawRecords.take(10).forEach { record ->
                                 Text(
                                    text = record,
                                    style = Typography.labelSmall,
                                    color = Color.DarkGray
                                )
                             }
                             if (viewModel.rawRecords.size > 10) {
                                 Text("... ще ${viewModel.rawRecords.size - 10} записів", style = Typography.labelSmall)
                             }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Toggle Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Використовувати Health Connect",
                style = Typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = viewModel.userProfile?.isHealthConnectEnabled == true,
                onCheckedChange = { viewModel.toggleHealthConnect(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFFF8A00)
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Check Connection Button / Fetch Steps
        Button(
            onClick = { viewModel.fetchSteps() },
            enabled = viewModel.userProfile?.isHealthConnectEnabled == true && viewModel.permissionsGranted,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Перевірити з’єднання",
                    style = Typography.titleMedium,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Check Permissions Button / Request Permissions
        Button(
            onClick = { permissionLauncher.launch(viewModel.permissions) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
             Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Build, contentDescription = null, tint = Color.White) // Using Build as placeholder for Plug
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Перевірити дозволи",
                    style = Typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}
