package com.example.wellminder.ui.screens.food

import androidx.compose.foundation.background
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
import com.example.wellminder.data.local.entities.FoodWithNutrients
import com.example.wellminder.ui.theme.Typography

@Composable
fun AddToMealDialog(
    item: FoodWithNutrients,
    mealType: String,
    onDismiss: () -> Unit,
    onAdd: (Int) -> Unit
) {
    var grams by remember { mutableStateOf("100") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Додати до $mealType",
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = item.food.name,
                    style = Typography.bodyLarge,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Grams Input
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = grams,
                        onValueChange = { input ->
                             if (input.all { it.isDigit() }) grams = input 
                        },
                        textStyle = Typography.headlineMedium.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .width(80.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("г", style = Typography.titleMedium, color = Color.Gray)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Calculated Info
                val g = grams.toIntOrNull() ?: 0
                val ratio = g / 100f
                val p = (item.nutrients?.proteins ?: 0f) * ratio
                val f = (item.nutrients?.fats ?: 0f) * ratio
                val c = (item.nutrients?.carbohydrates ?: 0f) * ratio
                val cals = com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(p, f, c)
                
                Text(
                    text = "$cals ккал",
                    style = Typography.titleLarge.copy(color = Color(0xFFFF8A00), fontWeight = FontWeight.Bold)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        val finalGrams = grams.toIntOrNull()
                        if (finalGrams != null && finalGrams > 0) {
                            onAdd(finalGrams)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (grams.toIntOrNull() ?: 0 > 0) Color(0xFFFF8A00) else Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = (grams.toIntOrNull() ?: 0) > 0,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Додати", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
