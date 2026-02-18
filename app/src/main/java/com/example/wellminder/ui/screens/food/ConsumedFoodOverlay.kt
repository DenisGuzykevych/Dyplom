package com.example.wellminder.ui.screens.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wellminder.data.local.entities.FoodWithNutrients
import com.example.wellminder.ui.theme.Typography

@Composable
fun ConsumedFoodOverlay(
    mealType: String,
    onDismiss: () -> Unit,
    viewModel: FoodViewModel = hiltViewModel()
) {
    val filteredList by viewModel.filteredFoodList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    var selectedFood by remember { mutableStateOf<FoodWithNutrients?>(null) }
    var gramsInput by remember { mutableStateOf("100") }

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
                    .fillMaxHeight(0.85f)
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Додати у $mealType",
                        style = Typography.headlineSmall,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Bar
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        textStyle = Typography.bodyLarge.copy(color = Color.Black),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))
                            .padding(horizontal = 16.dp),
                        decorationBox = { innerTextField ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (searchQuery.isEmpty()) {
                                        Text("Пошук...", color = Color.Gray)
                                    }
                                    innerTextField()
                                }
                                if (searchQuery.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { viewModel.onSearchQueryChange("") }
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Food List
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE3F2FD)),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (filteredList.isEmpty()) {
                             Box(
                                 modifier = Modifier.fillMaxSize(),
                                 contentAlignment = Alignment.Center
                             ) {
                                 Text(
                                     text = if (searchQuery.isEmpty()) "Список порожній" else "Нічого не знайдено",
                                     style = Typography.bodyMedium,
                                     color = Color.Gray
                                 )
                             }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(filteredList) { item ->
                                    val isSelected = selectedFood?.food?.foodId == item.food.foodId
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) Color(0xFFE3F2FD) else Color.Transparent)
                                            .clickable { 
                                                selectedFood = item 
                                                gramsInput = "100" 
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.food.name,
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            val cals = com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(
                                                item.nutrients?.proteins ?: 0f,
                                                item.nutrients?.fats ?: 0f,
                                                item.nutrients?.carbohydrates ?: 0f
                                            )
                                            Text(
                                                text = "$cals ккал / 100г",
                                                style = Typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color(0xFF1E8D24),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    if (filteredList.last() != item) {
                                        HorizontalDivider(color = Color(0xFFF0F0F0))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom Edit Panel
                    if (selectedFood != null) {
                         val sf = selectedFood!!
                         Column(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .background(Color(0xFFE3F2FD), RoundedCornerShape(24.dp))
                                 .padding(16.dp),
                             horizontalAlignment = Alignment.CenterHorizontally
                         ) {
                             Text(
                                 text = sf.food.name,
                                 style = Typography.titleMedium,
                                 fontWeight = FontWeight.Bold
                             )
                             Spacer(modifier = Modifier.height(8.dp))
                             
                             Row(
                                 verticalAlignment = Alignment.CenterVertically,
                                 horizontalArrangement = Arrangement.Center,
                                 modifier = Modifier.fillMaxWidth()
                             ) {
                                 Text("Вага (г):", style = Typography.bodyMedium)
                                 Spacer(modifier = Modifier.width(16.dp))
                                 BasicTextField(
                                     value = gramsInput,
                                     onValueChange = { if (it.all { char -> char.isDigit() }) gramsInput = it },
                                     textStyle = Typography.titleMedium.copy(textAlign = TextAlign.Center),
                                     singleLine = true,
                                     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                     modifier = Modifier
                                         .width(80.dp)
                                         .background(Color.White, RoundedCornerShape(8.dp))
                                         .padding(8.dp)
                                 )
                             }
                             
                             Spacer(modifier = Modifier.height(8.dp))
                             
                             val grams = gramsInput.toIntOrNull() ?: 0
                             val factor = grams / 100f
                             val p = (sf.nutrients?.proteins ?: 0f) * factor
                             val f = (sf.nutrients?.fats ?: 0f) * factor
                             val c = (sf.nutrients?.carbohydrates ?: 0f) * factor
                             val cals = com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(p, f, c)
                             
                             Text(
                                 text = "Разом: $cals ккал",
                                 style = Typography.bodyMedium,
                                 fontWeight = FontWeight.Bold,
                                 color = Color(0xFF1E8D24)
                             )
                             
                             Spacer(modifier = Modifier.height(16.dp))
                             
                             Button(
                                 onClick = {
                                     val gramsVal = gramsInput.toIntOrNull()
                                     if (gramsVal != null && gramsVal > 0) {
                                         viewModel.logConsumedFood(sf.food.foodId, gramsVal, mealType)
                                         onDismiss()
                                     }
                                 },
                                 colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                                 shape = RoundedCornerShape(24.dp),
                                 modifier = Modifier.fillMaxWidth()
                             ) {
                                 Text("Додати", color = Color.White)
                             }
                         }
                    } else {
                         // Placeholder or Close button if nothing selected
                         Button(
                             onClick = onDismiss,
                             colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                             shape = RoundedCornerShape(24.dp),
                              modifier = Modifier.fillMaxWidth()
                         ) {
                             Text("Закрити", color = Color.White)
                         }
                    }
                }
            }
        }
    }
}
