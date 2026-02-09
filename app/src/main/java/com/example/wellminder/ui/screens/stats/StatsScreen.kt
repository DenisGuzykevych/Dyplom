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
    onNavigate: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var showWeightDialog by remember { mutableStateOf(false) }

    if (showWeightDialog) {
        UpdateWeightDialog(
            onDismiss = { showWeightDialog = false },
            onSave = { newWeight ->
                // TODO: Save weight logic
                showWeightDialog = false
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFEFF5FF),
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
                            text = "Змінилась вага?",
                            style = Typography.bodyLarge.copy(fontSize = 18.sp),
                            color = Color.Black
                        )
                        Text(
                            text = "Вкажіть актуальну",
                            style = Typography.bodyMedium,
                            color = Color.Black
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
                    
                    // Bar Chart Placeholder
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val weeksData = listOf(
                            "22.01" to 67.8f,
                            "23.01" to 68.5f,
                            "24.01" to 67.5f,
                            "25.01" to 67.5f,
                            "26.01" to 68.0f,
                            "27.01" to 68.5f,
                            "28.01" to 68.0f
                        )
                        val maxWeight = 70f
                        
                        weeksData.forEach { (date, weight) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = weight.toString(),
                                    style = Typography.labelSmall,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(12.dp)
                                        .height((weight / maxWeight * 150).dp) // simplistic scaling
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
                        text = "Статистика за\n28.01.26",
                        style = Typography.titleMedium.copy(fontSize = 20.sp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Вага: 68кг",
                        style = Typography.bodyLarge,
                        fontSize = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                         StatCircle(
                             label = "Вода",
                             value = "1.5л", // Mock
                             subValue = "з 2л",
                             progress = 0.75f,
                             color = Color(0xFF00C2FF) // Blue
                         )
                         StatCircle(
                             label = "Калорії",
                             value = "1400", // Mock
                             subValue = "з 2500",
                             progress = 0.56f,
                             color = Color(0xFF4CAF50) // Green
                         )
                         StatCircle(
                             label = "Кроки",
                             value = "4.7k", // Mock
                             subValue = "з 8k",
                             progress = 0.6f,
                             color = Color(0xFFFF002E) // Red
                         )
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
            .width(90.dp) // Fixed width for uniformity
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
            color = Color.White, // White track inside the light blue card
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
        
        // Dot at end of progress (calculating position)
        val angleInDegrees = -90f + (360 * progress)
        val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
        val radius = (size.toPx() - strokeWidth.toPx()) / 2
        val center = size.toPx() / 2
        val cx = center + radius * Math.cos(angleInRadians).toFloat()
        val cy = center + radius * Math.sin(angleInRadians).toFloat()
        
        drawCircle(
            color = Color.White,
            radius = strokeWidth.toPx() / 2, // Small dot inside? No, probably the knob.
            center = Offset(cx, cy)
        )
    }
}
