package com.example.wellminder.ui.screens.food

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wellminder.ui.theme.Typography

@Composable
fun AddProductOverlay(
    onDismiss: () -> Unit,
    onSave: (String, Int, Float, Float, Float) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var portion by remember { mutableStateOf("100") } // Visual only, as per request
    var calories by remember { mutableStateOf("") }
    var proteins by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    
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
                            text = "Ручне введення",
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
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)), // App Orange
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Зберегти", fontSize = 12.sp, color = Color.White)
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Icon(
                                imageVector = Icons.Default.Close,
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

                    // Portion Field
                    CompactLabeledInput(
                        label = "Порція (г)",
                        placeholder = "100",
                        value = portion,
                        onValueChange = { if (it.all { c -> c.isDigit() }) portion = it },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        // Proteins
                        CompactLabeledInput(
                            label = "Білки",
                            placeholder = "0",
                            value = proteins,
                            onValueChange = { if (it.count { c -> c == '.' } <= 1 && it.replace(".", "").all { c -> c.isDigit() }) proteins = it },
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        // Fats
                        CompactLabeledInput(
                            label = "Жири",
                            placeholder = "0",
                            value = fats,
                            onValueChange = { if (it.count { c -> c == '.' } <= 1 && it.replace(".", "").all { c -> c.isDigit() }) fats = it },
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        // Carbs
                        CompactLabeledInput(
                            label = "Вуглев.",
                            placeholder = "0",
                            value = carbs,
                            onValueChange = { if (it.count { c -> c == '.' } <= 1 && it.replace(".", "").all { c -> c.isDigit() }) carbs = it },
                            keyboardType = KeyboardType.Number,
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
fun CompactLabeledInput(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = Typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = Typography.bodyMedium.copy(color = Color.Black),
            singleLine = true,
            cursorBrush = SolidColor(Color.Black),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5)) // Light Gray
                .padding(horizontal = 12.dp),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = Typography.bodyMedium.copy(color = Color.Gray),
                            maxLines = 1
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun LabeledInput(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = Typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = Typography.bodyLarge.copy(color = Color.Black),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .padding(horizontal = 4.dp), // Inner padding
            decorationBox = { innerTextField ->
                 Box(
                     modifier = Modifier
                         .fillMaxSize()
                         .border(
                             width = 1.dp,
                             color = Color(0xFFE3F2FD), // Light Blue Border
                             shape = RoundedCornerShape(26.dp)
                         )
                         .padding(horizontal = 24.dp),
                     contentAlignment = Alignment.CenterStart
                 ) {
                     if (value.isEmpty()) {
                         Text(
                             text = placeholder,
                             style = Typography.bodyMedium.copy(color = Color(0xFFAAAAAA))
                         )
                     }
                     innerTextField()
                 }
            }
        )
    }
}
