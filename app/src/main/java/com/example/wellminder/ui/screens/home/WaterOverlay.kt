package com.example.wellminder.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WaterDrop
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

@Composable
fun WaterOverlay(
    currentIntake: Int, // in ml
    targetIntake: Int, // in ml
    onDismiss: () -> Unit,
    onAdd: (Int) -> Unit,
    onRemove: (Int) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false // KEY: Allows full control over size/position
        )
    ) {
        // Transparent container to manage positioning
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss), // Dismiss when clicking outside if desired, though Dialog does this
            contentAlignment = Alignment.Center // Start centered, then offset or alignment
        ) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.85f) // Bigger width
                    .clickable(enabled = false) {} // Prevent click-through to dismiss
                    .offset(y = (-40).dp) // Position it "not exactly center", slightly higher 
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Ви випили:",
                        style = Typography.headlineSmall.copy(fontWeight = FontWeight.Normal),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "${(currentIntake / 1000f)} літри",
                        style = Typography.headlineMedium.copy(fontWeight = FontWeight.Normal),
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp
                    )
    
                    Spacer(modifier = Modifier.height(32.dp))
    
                    // Progress Bar
                    WaterProgressBar(
                        current = currentIntake,
                        target = targetIntake
                    )
    
                    Spacer(modifier = Modifier.height(32.dp))
    
                    // Control Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WaterButton(amount = 100, isAdd = true, onClick = { onAdd(100) })
                        WaterButton(amount = 300, isAdd = true, onClick = { onAdd(300) })
                        WaterButton(amount = 500, isAdd = true, onClick = { onAdd(500) })
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WaterButton(amount = 100, isAdd = false, onClick = { onRemove(100) })
                        WaterButton(amount = 300, isAdd = false, onClick = { onRemove(300) })
                        WaterButton(amount = 500, isAdd = false, onClick = { onRemove(500) })
                    }
    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Target Text
                    Text(
                        text = "Норма: ${targetIntake / 1000}л",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WaterProgressBar(current: Int, target: Int) {
    val progress = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp), // Height to accommodate the knob
        contentAlignment = Alignment.CenterStart
    ) {
        // Track Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE0E0E0)) // Light gray background
        )
        
        // Active Track
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF03A9F4)) // Bright Blue
        )
        
        // Knob (Thumb)
        Row(modifier = Modifier.fillMaxWidth()) {
             if (progress > 0f) {
                 Spacer(modifier = Modifier.weight(progress))
             }
             // The Knob
             Box(
                 modifier = Modifier
                     .size(24.dp)
                     .shadow(4.dp, CircleShape)
                     .background(Color.White, CircleShape)
                     .padding(4.dp) // Optional internal styling
             )
             if (progress < 1f) {
                 Spacer(modifier = Modifier.weight(1f - progress))
             }
        }
    }
}

@Composable
fun WaterButton(amount: Int, isAdd: Boolean, onClick: () -> Unit) {
    // Colors based on the screenshot (Cyan for +, Red/Pink for -)
    val containerColor = if (isAdd) Color(0xFFB2EBF2) else Color(0xFFFFCDD2) // Light Cyan vs Light Red
    val contentColor = Color.Black
    val iconTint = if (isAdd) Color(0xFF00BCD4) else Color(0xFF2196F3) // Cyan vs Blue drops

    val buttonShape = RoundedCornerShape(12.dp) // Screenshot shows less rounded than fully circular
    
    // Custom button layout to match exact look
    Box(
        modifier = Modifier
            .width(85.dp) // Approximate width from screenshot
            .height(36.dp)
            .shadow(0.dp, buttonShape) // Flat look
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
                text = "${if (isAdd) "+" else "-"} $amount мл",
                style = Typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                fontSize = 12.sp,
                color = contentColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Rounded.WaterDrop,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
