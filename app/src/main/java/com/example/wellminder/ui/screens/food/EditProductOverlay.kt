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
                    .fillMaxWidth(0.95f)
                    .wrapContentHeight()
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Редагувати продукт",
                        style = Typography.headlineSmall,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Delete Button (Optional)
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCDD2)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(androidx.compose.material.icons.Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Видалити", color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Spacer(modifier = Modifier.height(32.dp))

                    // Fields
                    LabeledInput(
                        label = "Назва продукту", 
                        placeholder = "Введіть назву продукту", 
                        value = name, 
                        onValueChange = { name = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LabeledInput(
                        label = "Калорійність на 100г", 
                        placeholder = "Введіть у калорійність", 
                        value = calories, 
                        onValueChange = { if (it.all { char -> char.isDigit() }) calories = it },
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LabeledInput(
                        label = "Білки на 100г", 
                        placeholder = "Введіть білки у грамах", 
                        value = proteins, 
                        onValueChange = { if (it.count { char -> char == '.' } <= 1 && it.replace(".", "").all { char -> char.isDigit() }) proteins = it },
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LabeledInput(
                        label = "Вуглеводи на 100г", 
                        placeholder = "Введіть вуглеводи у грамах", 
                        value = carbs, 
                        onValueChange = { if (it.count { char -> char == '.' } <= 1 && it.replace(".", "").all { char -> char.isDigit() }) carbs = it },
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LabeledInput(
                        label = "Жири на 100г", 
                        placeholder = "Введіть жири у грамах", 
                        value = fats, 
                        onValueChange = { if (it.count { char -> char == '.' } <= 1 && it.replace(".", "").all { char -> char.isDigit() }) fats = it },
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Green Checkmark Button
                    PremiumCheckButton(
                        onClick = {
                            val calVal = calories.toIntOrNull()
                            val protVal = proteins.toFloatOrNull()
                            val fatsVal = fats.toFloatOrNull()
                            val carbsVal = carbs.toFloatOrNull()
                            
                            if (name.isNotEmpty() && calVal != null && protVal != null && fatsVal != null && carbsVal != null) {
                                onSave(name, calVal, protVal, fatsVal, carbsVal)
                            } else {
                                // You might need context here too if available, or just rely on visual feedback if toast is hard to access (but we can get context)
                            }
                        }
                    )


                }
            }
        }
    }
}

