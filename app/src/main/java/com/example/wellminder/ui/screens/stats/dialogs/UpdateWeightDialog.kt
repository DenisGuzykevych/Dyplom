package com.example.wellminder.ui.screens.stats.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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

@Composable
fun UpdateWeightDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var weight by remember { mutableStateOf("") }

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
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Введіть вашу актуальну вагу!",
                        style = Typography.titleMedium.copy(fontSize = 18.sp),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Input Field
                    BasicTextField(
                        value = weight,
                        onValueChange = { input ->
                             if (input.count { it == '.' } <= 1 && input.replace(".", "").all { it.isDigit() }) {
                                 weight = input
                             }
                        },
                        textStyle = Typography.bodyLarge.copy(color = Color.Black),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(32.dp)) // Fully rounded
                            .background(Color.White)
                            .padding(horizontal = 4.dp), // Inner padding
                            // We need a border. BasicTextField doesn't have it natively easily without decorationBox
                        decorationBox = { innerTextField ->
                             Box(
                                 modifier = Modifier
                                     .fillMaxSize()
                                     .border(
                                         width = 1.dp,
                                         color = Color(0xFFE3F2FD),
                                         shape = RoundedCornerShape(32.dp)
                                     )
                                     .padding(horizontal = 24.dp),
                                 contentAlignment = Alignment.CenterStart
                             ) {
                                 if (weight.isEmpty()) {
                                     Text(
                                         text = "У кілограмах",
                                         style = Typography.bodyMedium.copy(color = Color(0xFFAAAAAA))
                                     )
                                 }
                                 innerTextField()
                             }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Orange Save Button
                    Button(
                        onClick = { onSave(weight) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (weight.toFloatOrNull() != null) Color(0xFFFF8A00) else Color.Gray
                        ),
                        enabled = weight.toFloatOrNull() != null,
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp)
                    ) {
                        Text(
                            text = "Зберегти",
                            style = Typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
