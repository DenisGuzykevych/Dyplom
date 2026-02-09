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

@Composable
fun AddProductOverlay(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var proteins by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }

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
                        text = "Додайте дані про ваш\nпродукт чи страву!",
                        style = Typography.headlineSmall,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Fields
                    LabeledInput(label = "Назва продукту", placeholder = "Введіть назву продукту", value = name, onValueChange = { name = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    LabeledInput(label = "Введіть калорійність на 100г", placeholder = "Введіть у калорійність", value = calories, onValueChange = { calories = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    LabeledInput(label = "Введіть білки продукту на 100г", placeholder = "Введіть білки у грамах", value = proteins, onValueChange = { proteins = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    LabeledInput(label = "Введіть вуглеводи продукту на 100г", placeholder = "Введіть вуглеводи у грамах", value = carbs, onValueChange = { carbs = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    LabeledInput(label = "Введіть жири продукту на 100г", placeholder = "Введіть жири у грамах", value = fats, onValueChange = { fats = it })

                    Spacer(modifier = Modifier.height(32.dp))

                    // Green Checkmark Button
                    PremiumCheckButton(
                        onClick = onSave
                    )


                }
            }
        }
    }
}

@Composable
fun LabeledInput(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
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
