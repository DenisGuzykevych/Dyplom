package com.example.wellminder.ui.screens.food

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wellminder.ui.theme.Typography
import androidx.compose.foundation.text.BasicTextField

@Composable
fun EditFoodDialog(
    item: SearchFoodItem,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var grams by remember { mutableStateOf("200") } // Default/Initial value

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss), // Click outside to dismiss
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable(enabled = false) {} // Prevent click through
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Text(
                        text = item.name,
                        style = Typography.headlineMedium, // Larger font
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Light Blue Card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), // Light Blue
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Row: Name and Calories info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.name,
                                    style = Typography.bodyMedium,
                                    color = Color.Black
                                )
                                Text(
                                    text = "47ккал на 100 г", // Mock data or extract from item
                                    style = Typography.bodySmall,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Введіть грами продукту",
                                style = Typography.titleMedium,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // White Input Field
                            BasicTextField(
                                value = grams,
                                onValueChange = { grams = it },
                                textStyle = Typography.titleLarge.copy(color = Color.Black),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 16.dp),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (grams.isEmpty()) {
                                            Text(
                                                text = "Введіть",
                                                style = Typography.titleLarge.copy(color = Color.Gray)
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Premium Check Button
                    com.example.wellminder.ui.components.PremiumCheckButton(
                        onClick = { onSave(grams) }
                    )
                }
            }
        }
    }
}
