package com.example.wellminder.ui.screens.food

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wellminder.ui.theme.Typography
import com.example.wellminder.ui.components.PremiumCheckButton
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check

@Composable

fun EditProductOverlay(
    initialName: String,
    initialCalories: Int,
    initialProteins: Float,
    initialFats: Float,
    initialCarbs: Float,
    onDismiss: () -> Unit,
    onSave: (String, Int, Float, Float, Float) -> Unit,
    onDelete: () -> Unit = {}
) {
    var name by remember { mutableStateOf(initialName) }
    var calories by remember { mutableStateOf(initialCalories.toString()) }
    var proteins by remember { mutableStateOf(initialProteins.toString()) }
    var carbs by remember { mutableStateOf(initialCarbs.toString()) }
    var fats by remember { mutableStateOf(initialFats.toString()) }
    
    val context = androidx.compose.ui.platform.LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Редагувати продукт",
                            style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black,
                            fontSize = 18.sp
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Save Button
                            Button(
                                onClick = {
                                    val calVal = calories.toIntOrNull()
                                    val protVal = proteins.toFloatOrNull() ?: 0f
                                    val fatsVal = fats.toFloatOrNull() ?: 0f
                                    val carbsVal = carbs.toFloatOrNull() ?: 0f
                                    
                                    if (name.isNotEmpty() && calVal != null) {
                                        onSave(name, calVal, protVal, fatsVal, carbsVal)
                                    } else {
                                         android.widget.Toast.makeText(context, "Вкажіть назву та калорії", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Зберегти", fontSize = 12.sp, color = Color.White)
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            // Delete Icon
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onDelete() }
                            )

                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onDismiss() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Name Field
                    CompactLabeledInput(
                        label = "Назва *",
                        placeholder = "Напр: Куряче філе",
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "На 100 г:",
                        style = Typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Nutrients Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Calories
                        CompactLabeledInput(
                            label = "Ккал *",
                            placeholder = "0",
                            value = calories,
                            onValueChange = { if (it.all { c -> c.isDigit() }) calories = it },
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        // Proteins
                        CompactLabeledInput(
                            label = "Білки",
                            placeholder = "0",
                            value = proteins,
                            onValueChange = { if (it.count { c -> c == '.' } <= 1 && it.replace(".", "").all { c -> c.isDigit() }) proteins = it },
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        // Fats
                        CompactLabeledInput(
                            label = "Жири",
                            placeholder = "0",
                            value = fats,
                            onValueChange = { if (it.count { c -> c == '.' } <= 1 && it.replace(".", "").all { c -> c.isDigit() }) fats = it },
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        // Carbs
                        CompactLabeledInput(
                            label = "Вуглев.",
                            placeholder = "0",
                            value = carbs,
                            onValueChange = { if (it.count { c -> c == '.' } <= 1 && it.replace(".", "").all { c -> c.isDigit() }) carbs = it },
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

