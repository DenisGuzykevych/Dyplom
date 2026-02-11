package com.example.wellminder.ui.screens.food

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wellminder.data.local.dao.ConsumedFoodDetail
import com.example.wellminder.data.local.entities.FoodWithNutrientsAndCategory
import com.example.wellminder.ui.components.BottomNavigationBar
import com.example.wellminder.ui.components.MealType
import com.example.wellminder.ui.components.ReusableFoodSection
import com.example.wellminder.ui.components.TopBarSection
import com.example.wellminder.ui.theme.Typography
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.HorizontalDivider

@Composable
fun FoodScreen(
    onNavigate: (String) -> Unit,
    viewModel: FoodViewModel = hiltViewModel()
) {
    val foodList by viewModel.foodList.collectAsState(initial = emptyList())
    val consumedFoodList by viewModel.consumedFoodList.collectAsState()
    
    // State for dialogs
    var showAddProductOverlay by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ConsumedFoodDetail?>(null) }
    var itemToDelete by remember { mutableStateOf<ConsumedFoodDetail?>(null) }
    var editProductItem by remember { mutableStateOf<FoodWithNutrientsAndCategory?>(null) }
    
    // Meal Selection State
    var selectedMeal by remember { mutableStateOf(MealType.BREAKFAST) }

    Scaffold(
        containerColor = Color(0xFFEFF5FF),
        bottomBar = { 
            BottomNavigationBar(
                currentRoute = "food", 
                onNavigate = onNavigate
            ) 
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TopBarSection()
            Spacer(modifier = Modifier.height(16.dp))

            // 1. TOP SECTION: Available Food Library
            // Fixed height or weight to ensure it doesn't take up too much space
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f) // Takes up about 35% of the vertical space
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(foodList) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { editProductItem = item }, // Edit available food
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.food.name, style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("${item.nutrients?.calories} ккал", style = Typography.bodySmall, color = Color.Gray)
                            }
                            
                            // Edit Icon
                             Icon(
                                Icons.Default.Edit, 
                                contentDescription = "Edit", 
                                tint = Color(0xFFFF8A00),
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))

                            // Delete Icon (Optional, keeping it consistent with request if needed, or just Edit)
                             Icon(
                                Icons.Default.Delete, 
                                contentDescription = "Delete", 
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { viewModel.deleteFood(item) }
                            )
                        }
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. MIDDLE SECTION: Create Custom Food Banner
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Страви чи продукту немає у додатку?\nСтворіть свій їх самі!",
                    textAlign = TextAlign.Center,
                    style = Typography.bodyMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showAddProductOverlay = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(0.6f)
                ) {
                    Text("Додати!", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. BOTTOM SECTION: Meal Tabs & Consumed List
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.65f) // Takes remaining space
                    .shadow(4.dp, RoundedCornerShape(32.dp))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Custom Tab Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MealType.entries.forEach { meal ->
                            val isSelected = selectedMeal == meal
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedMeal = meal }
                                    .background(if (isSelected) Color(0xFFFF8A00) else Color.Transparent)
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = meal.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                                Text(
                                    text = meal.timeRange,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray
                                )
                            }
                            if (meal != MealType.SNACK) {
                                VerticalDivider(
                                    modifier = Modifier.height(30.dp).width(1.dp),
                                    color = Color.LightGray
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    // Consumed List for Selected Meal
                    val filteredLogs = consumedFoodList.filter { it.consumed.mealType == selectedMeal.title }
                    
                    Box(modifier = Modifier.weight(1f)) {
                         if (filteredLogs.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Нічого не додано",
                                    style = Typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                items(filteredLogs) { item ->
                                    ConsumedFoodItem(
                                        item = item,
                                        onEditClick = { itemToEdit = item },
                                        onDeleteClick = { itemToDelete = item }
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    // 4. FOOTER: Meal Analysis
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val totalCals = filteredLogs.sumOf { 
                            ((it.nutrients?.calories ?: 0) * (it.consumed.grams / 100f)).toInt()
                        }
                        
                        // Get target from ViewModel (assuming it's added)
                        val targetCalories = viewModel.targetCalories // Need to ensure this exists
                        
                        val mealTargetPercentage = when(selectedMeal) {
                            MealType.BREAKFAST -> 0.25f
                            MealType.LUNCH -> 0.35f
                            MealType.DINNER -> 0.30f
                            MealType.SNACK -> 0.10f
                        }
                        val mealTarget = (targetCalories * mealTargetPercentage).toInt()
                        
                        // Analysis
                        val (statusText, statusColor) = when {
                             totalCals < mealTarget * 0.8 -> "Мало" to Color(0xFFFFB74D) // Orange
                             totalCals > mealTarget * 1.2 -> "Перебір" to Color(0xFFEF5350) // Red
                             else -> "Добре" to Color(0xFF66BB6A) // Green
                        }
                        
                        Text(
                            text = "Спожито: $totalCals / $mealTarget ккал ($statusText)",
                            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = statusColor
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = { (totalCals.toFloat() / mealTarget.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth(0.6f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = statusColor,
                            trackColor = Color(0xFFEEEEEE),
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddProductOverlay) {
        AddProductOverlay(
            onDismiss = { showAddProductOverlay = false },
            onSave = { name, cals, prot, fats, carbs ->
                viewModel.addFood(name, cals, prot, fats, carbs)
                showAddProductOverlay = false
            }
        )
    }
    
    if (editProductItem != null) {
        val item = editProductItem!!
        EditProductOverlay(
            initialName = item.food.name,
            initialCalories = item.nutrients?.calories ?: 0,
            initialProteins = item.nutrients?.proteins ?: 0f,
            initialFats = item.nutrients?.fats ?: 0f,
            initialCarbs = item.nutrients?.carbohydrates ?: 0f,
            onDismiss = { editProductItem = null },
            onSave = { name, cals, prot, fats, carbs ->
                viewModel.updateFood(item, name, cals, prot, fats, carbs)
                editProductItem = null
            },
            onDelete = {
                 viewModel.deleteFood(item)
                 editProductItem = null
            }
        )
    }

    if (itemToDelete != null) {
        DeleteConfirmationDialog(
            onDismiss = { itemToDelete = null },
            onConfirm = {
                viewModel.deleteConsumedFood(itemToDelete!!.consumed.id)
                itemToDelete = null
            }
        )
    }
    
    if (itemToEdit != null) {
        EditConsumedDialog(
            item = itemToEdit!!,
            onDismiss = { itemToEdit = null },
            onSave = { newGrams ->
                viewModel.updateConsumedFood(
                    id = itemToEdit!!.consumed.id,
                    foodId = itemToEdit!!.food.foodId,
                    grams = newGrams,
                    mealType = itemToEdit!!.consumed.mealType,
                    timestamp = itemToEdit!!.consumed.timestamp
                )
                itemToEdit = null
            }
        )
    }
}

@Composable
fun ConsumedFoodItem(
    item: ConsumedFoodDetail,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val grams = item.consumed.grams
    val cals = ((item.nutrients?.calories ?: 0) * (grams / 100f)).toInt()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.food.name,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "${grams}г • $cals ккал",
                style = Typography.bodyMedium,
                color = Color.Gray
            )
        }
        
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
            tint = Color(0xFFFF8A00),
            modifier = Modifier
                .size(24.dp)
                .clickable { onEditClick() }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color(0xFFD32F2F),
            modifier = Modifier
                .size(24.dp)
                .clickable { onDeleteClick() }
        )
    }
}

@Composable
fun EditConsumedDialog(
    item: ConsumedFoodDetail,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var grams by remember { mutableStateOf(item.consumed.grams.toString()) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
             modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Редагувати вагу", style = Typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.foundation.text.BasicTextField(
                     value = grams,
                     onValueChange = { if (it.all { char -> char.isDigit() }) grams = it },
                     textStyle = Typography.bodyLarge.copy(textAlign = TextAlign.Center),
                     keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                     modifier = Modifier
                         .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                         .padding(16.dp)
                         .width(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val g = grams.toIntOrNull()
                        if (g != null && g > 0) onSave(g)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Зберегти")
                }
            }
        }
    }
}
