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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.theme.Typography

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun HealthConnectScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        viewModel.checkPermissions()
    }

    // Onboarding Explanation Dialog
    if (viewModel.showHealthConnectInfo) {
        AlertDialog(
            onDismissRequest = { viewModel.onHealthConnectInfoDismissed() },
            title = { Text("Синхронізація здоров'я", color = Color(0xFF1C1B1F)) },
            text = {
                Text(
                    "WellMinder може автоматично отримувати дані про вашу активність (кроки) через Health Connect. " +
                    "Це дозволяє бачити прогрес з вашого телефону або годинника в одному місці.\n\n" +
                    "На наступному кроці система запитає дозволи на читання даних кроків.",
                    color = Color(0xFF1C1B1F)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onHealthConnectInfoDismissed()
                        permissionLauncher.launch(viewModel.permissions)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00))
                ) {
                    Text("Зрозуміло", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(28.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF5FF))
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Back Button & Title Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
            Text(
                text = "Health Connect",
                style = Typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Color(0xFFFF8A00),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFFFF8A00)
                )
            },
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Налаштування", style = Typography.bodyMedium) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Джерела та Журнал", style = Typography.bodyMedium) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            if (selectedTab == 0) {
                ConnectionStatusTab(viewModel, permissionLauncher)
            } else {
                SourcesAndJournalTab(viewModel)
            }
        }
    }
}

@Composable
fun ConnectionStatusTab(
    viewModel: ProfileViewModel,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<Set<String>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(32.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Статус з'єднання",
                    style = Typography.headlineSmall,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                StatusRow("Сервіси SDK", when (viewModel.sdkStatus) {
                    androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE -> "Доступно"
                    androidx.health.connect.client.HealthConnectClient.SDK_UNAVAILABLE -> "Недоступно"
                    androidx.health.connect.client.HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> "Потрібне оновлення"
                    else -> "Невідомо"
                })
                
                StatusRow("Дозволи", if (viewModel.permissionsGranted) "Надано" else "Не надано", 
                    if (viewModel.permissionsGranted) Color(0xFF4CAF50) else Color.Red)
                
                if (viewModel.permissionsGranted) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Сьогодні отримано:",
                        style = Typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "${viewModel.steps} кроків",
                        style = Typography.headlineMedium,
                        color = Color(0xFFFF8A00)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Actions
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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

        Spacer(modifier = Modifier.height(16.dp))

        if (!viewModel.permissionsGranted) {
            Button(
                onClick = { permissionLauncher.launch(viewModel.permissions) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Build, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Надати дозволи", style = Typography.titleMedium, color = Color.White)
            }
        }

        Button(
            onClick = { viewModel.fetchSteps() },
            enabled = viewModel.userProfile?.isHealthConnectEnabled == true && viewModel.permissionsGranted,
            colors = ButtonDefaults.buttonColors(containerColor = if (viewModel.permissionsGranted) Color(0xFFFF8A00) else Color.LightGray),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(56.dp)
        ) {
            Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Оновити дані", style = Typography.titleMedium, color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SourcesAndJournalTab(viewModel: ProfileViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (viewModel.permissionsGranted) {
            Text(text = "Пріоритетне джерело:", style = Typography.titleSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SourceItem(
                        name = "Усі джерела (Авто)",
                        isSelected = viewModel.preferredSourceKey == "all" || viewModel.preferredSourceKey == null,
                        onClick = { viewModel.selectStepSource("all") }
                    )

                    viewModel.stepsBreakdown.forEach { (pkg, count) ->
                        SourceItem(
                            name = pkg,
                            isSelected = viewModel.preferredSourceKey == pkg,
                            onClick = { viewModel.selectStepSource(pkg) },
                            count = count
                        )
                    }
                }
            }
            
            if (viewModel.rawRecords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Журнал останніх записів:", style = Typography.titleSmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        viewModel.rawRecords.take(15).forEach { record ->
                            Text(
                                text = record,
                                style = Typography.labelSmall,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Divider(color = Color.LightGray.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Надайте дозволи у вкладці Налаштування", textAlign = TextAlign.Center, color = Color.Gray)
            }
        }
    }
}

@Composable
fun StatusRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = Typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = Typography.bodyMedium, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SourceItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    count: Long? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF8A00))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name.substringAfterLast("."), // Clean up package name for UI
                style = Typography.bodyMedium,
                color = if (isSelected) Color(0xFFFF8A00) else Color.Black,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (count != null) {
                Text(
                    text = "$count кроків",
                    style = Typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
