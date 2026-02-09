package com.example.wellminder.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wellminder.ui.theme.Typography
import kotlinx.coroutines.launch

private enum class StepsScreen {
    QUESTION,
    MANUAL,
    WATCH
}

@Composable
fun StepsOverlay(
    currentSteps: Int,
    targetSteps: Int,
    stepsPerKm: Int,
    distanceKm: Float,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    onRequestHealthConnect: () -> Unit, // Kept if needed for "Detailed Stats" checking permissions? 
    // Actually detailed stats can just show what we know.
    onConvertKm: suspend (Double) -> Int 
) {
    // Local state for navigation within the overlay
    var currentScreen by remember { mutableStateOf(StepsScreen.QUESTION) } // Reusing enum name but logically it's MENU now
    var sessionSteps by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val handleDismiss = {
        android.util.Log.d("StepsOverlay", "Dismissing. Session steps: $sessionSteps")
        if (sessionSteps != 0) {
            onSave(sessionSteps)
        }
        onDismiss()
    }

    Dialog(
        onDismissRequest = handleDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = handleDismiss),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable(enabled = false) {}
                    .offset(y = (-40).dp)
            ) {
                // Content changes based on currentScreen
                when (currentScreen) {
                    StepsScreen.QUESTION -> StepsMenuContent(
                        currentSteps = currentSteps,
                        distanceKm = distanceKm,
                        onDetailedStats = { currentScreen = StepsScreen.WATCH },
                        onAddManual = { currentScreen = StepsScreen.MANUAL }
                    )
                    StepsScreen.MANUAL -> StepsManualContentForKm(
                        currentSteps = currentSteps,
                        sessionSteps = sessionSteps,
                        targetSteps = targetSteps,
                        stepsPerKm = stepsPerKm,
                        onUpdateSessionKm = { km ->
                             scope.launch {
                                 val steps = onConvertKm(km)
                                 sessionSteps += steps
                             }
                        }
                    )
                    StepsScreen.WATCH -> StepsDetailedContent(
                        currentSteps = currentSteps,
                        distanceKm = distanceKm,
                        targetSteps = targetSteps
                    )
                }
            }
        }
    }
}

@Composable
fun StepsMenuContent(
    currentSteps: Int,
    distanceKm: Float, // Optional to show simplified stats here too
    onDetailedStats: () -> Unit,
    onAddManual: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Кроки сьогодні",
            style = Typography.headlineSmall.copy(fontWeight = FontWeight.Normal),
            textAlign = TextAlign.Center,
            fontSize = 22.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Detailed Stats Button
        Button(
            onClick = onDetailedStats,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F7FA)), // Light Cyan
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().height(80.dp)
        ) {
             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                 Text(
                     text = "Детальніше",
                     style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF006064))
                 )
                 Text(
                     text = "$currentSteps кроків • ${String.format("%.2f", distanceKm)} км",
                     style = Typography.bodySmall.copy(color = Color(0xFF00838F))
                 )
             }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Manual Add Button
        Button(
            onClick = onAddManual,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
             Text(
                 text = "Додати активність вручну",
                 textAlign = TextAlign.Center,
                 style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
             )
        }
    }
}

@Composable
fun StepsManualContentForKm(
    currentSteps: Int,
    sessionSteps: Int,
    targetSteps: Int,
    stepsPerKm: Int,
    onUpdateSessionKm: (Double) -> Unit
) {
    val displayedSteps = (currentSteps + sessionSteps).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ви пройшли:",
            style = Typography.headlineSmall.copy(fontWeight = FontWeight.Normal),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Text(
            text = "$displayedSteps кроків",
            style = Typography.headlineMedium.copy(fontWeight = FontWeight.Normal),
            textAlign = TextAlign.Center,
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Progress Slider (Red)
        StepsProgressBar(current = displayedSteps, target = targetSteps)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Додати відстань (км):",
            style = Typography.bodyMedium,
            fontSize = 16.sp,
            color = Color.Gray
        )
        Text(
            text = "(1 км ≈ $stepsPerKm кроків)",
            style = Typography.bodySmall,
            fontSize = 12.sp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Buttons (+ Teal, - Red) for KM
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StepsButton(label = "+ 0.5 км", isAdd = true, onClick = { onUpdateSessionKm(0.5) })
            StepsButton(label = "+ 1.0 км", isAdd = true, onClick = { onUpdateSessionKm(1.0) })
            StepsButton(label = "+ 2.0 км", isAdd = true, onClick = { onUpdateSessionKm(2.0) })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StepsButton(label = "- 0.5 км", isAdd = false, onClick = { onUpdateSessionKm(-0.5) })
            StepsButton(label = "- 1.0 км", isAdd = false, onClick = { onUpdateSessionKm(-1.0) })
            StepsButton(label = "- 2.0 км", isAdd = false, onClick = { onUpdateSessionKm(-2.0) })
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Норма: $targetSteps кроків",
            style = Typography.titleMedium.copy(fontWeight = FontWeight.Normal),
            color = Color.Black,
            fontSize = 20.sp
        )
    }
}

@Composable
fun StepsDetailedContent(
    currentSteps: Int,
    distanceKm: Float,
    targetSteps: Int
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Progress
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(150.dp)
        ) {
            CircularProgress(
                progress = (currentSteps.toFloat() / targetSteps.toFloat()).coerceIn(0f, 1f),
                color = Color(0xFFFF002E), 
                size = 150.dp,
                strokeWidth = 15.dp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Статистика за сьогодні",
            style = Typography.headlineSmall.copy(fontWeight = FontWeight.Normal),
            textAlign = TextAlign.Center,
            fontSize = 22.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
             Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                 Text(text = "Кроки", style = Typography.bodyMedium, color = Color.Gray)
                 Text(text = "$currentSteps", style = Typography.headlineMedium, fontSize = 24.sp)
             }
             Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                 Text(text = "Дистанція", style = Typography.bodyMedium, color = Color.Gray)
                 Text(text = String.format("%.2f км", distanceKm), style = Typography.headlineMedium, fontSize = 24.sp)
             }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Норма: $targetSteps кроків",
            style = Typography.titleMedium.copy(fontWeight = FontWeight.Normal),
            color = Color.Black,
            fontSize = 20.sp
        )
    }
}

@Composable
fun StepsProgressBar(current: Int, target: Int) {
    val progress = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE0E0E0))
        )
        
        // Active Track
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFFF002E)) // Red
        )
        
        // Knob
        Row(modifier = Modifier.fillMaxWidth()) {
             if (progress > 0f) Spacer(modifier = Modifier.weight(progress))
             Box(
                 modifier = Modifier
                     .size(24.dp)
                     .shadow(4.dp, CircleShape)
                     .background(Color.White, CircleShape)
                     .padding(4.dp)
             )
             if (progress < 1f) Spacer(modifier = Modifier.weight(1f - progress))
        }
    }
}

@Composable
fun StepsButton(label: String, isAdd: Boolean, onClick: () -> Unit) {
    val containerColor = if (isAdd) Color(0xFFB2EBF2) else Color(0xFFFFCDD2)
    val contentColor = Color.Black
    
    val buttonShape = RoundedCornerShape(12.dp)
    
    Box(
        modifier = Modifier
            .width(95.dp) // Maintain width
            .height(36.dp)
            .shadow(0.dp, buttonShape)
            .clip(buttonShape)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = Typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                fontSize = 12.sp,
                color = contentColor
            )
            // Icon removed for cleaner text fit with "km" or adjust layout
        }
    }
}
