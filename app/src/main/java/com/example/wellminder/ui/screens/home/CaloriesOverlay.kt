package com.example.wellminder.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wellminder.ui.theme.Typography

@Composable
fun CaloriesOverlay(
    currentProteins: Float,
    targetProteins: Float,
    currentFats: Float,
    targetFats: Float,
    currentCarbs: Float,
    targetCarbs: Float,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
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
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ваш баланс БЖВ",
                        style = Typography.headlineSmall.copy(fontWeight = FontWeight.Normal),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Proteins
                        NutrientItem(
                            title = "Білки",
                            progress = if (targetProteins > 0) currentProteins / targetProteins else 0f,
                            color = Color(0xFF4285F4), // Blue
                            current = "${currentProteins.toInt()}г",
                            target = "${targetProteins.toInt()}г",
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Fats
                        NutrientItem(
                            title = "Жири",
                            progress = if (targetFats > 0) currentFats / targetFats else 0f,
                            color = Color(0xFFFBBC05), // Yellow/Orange
                            current = "${currentFats.toInt()}г",
                            target = "${targetFats.toInt()}г",
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Carbs
                        NutrientItem(
                            title = "Вуглеводи",
                            progress = if (targetCarbs > 0) currentCarbs / targetCarbs else 0f,
                            color = Color(0xFF66BB6A), // Green
                            current = "${currentCarbs.toInt()}г",
                            target = "${targetCarbs.toInt()}г",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun NutrientItem(
    title: String,
    progress: Float,
    color: Color,
    current: String,
    target: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Container for partial background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(Color(0xFFF0F4F8), RoundedCornerShape(24.dp)) // Light blueish bg like screenshot
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = Typography.bodyMedium,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(contentAlignment = Alignment.Center) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.size(60.dp)) {
                        // Background Track
                        drawArc(
                            color = Color(0xFFE0E0E0),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Progress
                        drawArc(
                            color = color,
                            startAngle = -90f,
                            sweepAngle = 360 * progress,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    // Knob helper calculation would be complex here for exact positioning on arc,
                    // For static UI without complex math, we can skip the knob or try a simple overlay if needed.
                    // The screenshot shows a white knob at the end of the progress.
                    // Let's implement a simple knob at the top (start) for now or omit if too complex without math, 
                    // but users like details.
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "$current з $target",
                    style = Typography.bodySmall,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }
    }
}
