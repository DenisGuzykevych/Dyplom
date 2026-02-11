package com.example.wellminder.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.components.BottomNavigationBar
import com.example.wellminder.ui.components.TopBarSection
import com.example.wellminder.ui.theme.Typography
import com.example.wellminder.ui.screens.stats.dialogs.UpdateWeightDialog
import androidx.compose.runtime.*

@Composable
fun StatsScreen(
    onNavigate: (String) -> Unit,
    viewModel: StatsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val scrollState = rememberScrollState()
    var showWeightDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is StatsViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    if (showWeightDialog) {
        UpdateWeightDialog(
            onDismiss = { showWeightDialog = false },
            onSave = { newWeight ->
                viewModel.saveWeight(newWeight.toFloatOrNull() ?: 0f)
                showWeightDialog = false
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFEFF5FF),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "stats",
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            TopBarSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Weight Update Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .shadow(4.dp, RoundedCornerShape(32.dp))
                    .clickable { showWeightDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Ваша вага",
                            style = Typography.bodyLarge.copy(fontSize = 18.sp),
                            color = Color.Black
                        )
                        Text(
                            text = if (viewModel.currentWeight > 0) "${viewModel.currentWeight} кг" else "Вкажіть вагу",
                            style = Typography.bodyMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "Weight",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(32.dp)
                            .rotate(45f) // Styled like dumbbell
                    )
                    
                    Icon(
                         imageVector = Icons.Default.ChevronRight,
                         contentDescription = "Go",
                         tint = Color(0xFFFF8A00)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Graph Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Динаміка вашої ваги за тиждень",
                        style = Typography.titleMedium.copy(fontSize = 18.sp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Bar Chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val weeksData = viewModel.weeklyWeightData
                        // Find max for scaling
                        val maxWeight = weeksData.map { it.second }.maxOrNull()?.takeIf { it > 0f } ?: 100f
                        val scaleMax = maxWeight * 1.1f
                        
                        weeksData.forEach { (date, weight) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = if (weight > 0) weight.toString() else "0",
                                    style = Typography.labelSmall,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(12.dp)
                                        .height(
                                            if (weight > 0) (weight / scaleMax * 150).dp else (150 / 4).dp
                                        ) 
                                        .background(Color(0xFFFF8A00), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = date,
                                    style = Typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weekly/Daily Stats
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Статистика за сьогодні",
                        style = Typography.titleMedium.copy(fontSize = 20.sp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Вага: ${if (viewModel.currentWeight > 0) "${viewModel.currentWeight}кг" else "--"}",
                        style = Typography.bodyLarge,
                        fontSize = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                         // Water
                         Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                             StatCircle(
                                 label = "Вода",
                                 value = "${viewModel.waterIntake / 1000f}л",
                                 subValue = "з ${viewModel.waterTarget / 1000}л",
                                 progress = if (viewModel.waterTarget > 0) viewModel.waterIntake.toFloat() / viewModel.waterTarget.toFloat() else 0f,
                                 color = Color(0xFF00C2FF) // Blue
                             )
                         }
                         
                         // Calories
                         Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                             StatCircle(
                                 label = "Калорії",
                                 value = "${viewModel.consumedCalories}",
                                 subValue = "з ${viewModel.calorieTarget}",
                                 progress = if (viewModel.calorieTarget > 0) viewModel.consumedCalories.toFloat() / viewModel.calorieTarget.toFloat() else 0f,
                                 color = Color(0xFF4CAF50) // Green
                             )
                         }
                         
                         // Steps
                         Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                             StatCircle(
                                 label = "Кроки",
                                 value = "${viewModel.stepCount}",
                                 subValue = "з ${viewModel.stepTarget}",
                                 progress = if (viewModel.stepTarget > 0) viewModel.stepCount.toFloat() / viewModel.stepTarget.toFloat() else 0f,
                                 color = Color(0xFFFF002E) // Red
                             )
                         }
                    }
                     Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun StatCircle(
    label: String,
    value: String,
    subValue: String,
    progress: Float,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFFEFF5FF), RoundedCornerShape(24.dp)) // Light blue bg for item
            .padding(12.dp)
            .fillMaxWidth() // Adapt to available space in weighted box
    ) {
        Text(text = label, style = Typography.bodyMedium, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
             StatsCircularProgress(
                 progress = progress,
                 color = color,
                 size = 60.dp,
                 strokeWidth = 8.dp
             )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(text = value, style = Typography.bodyMedium, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = subValue, style = Typography.labelSmall, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun StatsCircularProgress(
    progress: Float,
    color: Color,
    size: Dp,
    strokeWidth: Dp
) {
    Canvas(modifier = Modifier.size(size)) {
        // Background track
        drawArc(
            color = Color.White,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
        // Progress
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360 * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}
