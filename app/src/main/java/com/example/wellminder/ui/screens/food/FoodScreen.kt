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
import androidx.compose.material.icons.filled.Search
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
import com.example.wellminder.data.local.entities.FoodWithNutrients
import com.example.wellminder.ui.components.BottomNavigationBar
import com.example.wellminder.ui.components.MealType
import com.example.wellminder.ui.components.ReusableFoodSection
import com.example.wellminder.ui.components.TopBarSection
import com.example.wellminder.ui.theme.Typography
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.HorizontalDivider

@Composable
fun FoodScreen(
    onNavigate: (String) -> Unit,
    viewModel: FoodViewModel = hiltViewModel()
) {
    val foodList by viewModel.filteredFoodList.collectAsState()
    val consumedFoodList by viewModel.consumedFoodList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    // Стан для діалогів
    var showAddProductOverlay by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ConsumedFoodDetail?>(null) }
    var itemToDelete by remember { mutableStateOf<ConsumedFoodDetail?>(null) }
    var editProductItem by remember { mutableStateOf<FoodWithNutrients?>(null) }
    var itemToAdd by remember { mutableStateOf<FoodWithNutrients?>(null) }
    
    val context = LocalContext.current
    
    // Стан вибору прийому їжі
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

            // 1. ВЕРХНЯ СЕКЦІЯ: Бібліотека доступних продуктів
            // Фіксована висота або вага, щоб не займало надто багато місця
            // 1. ВЕРХНЯ СЕКЦІЯ: Бібліотека доступних продуктів
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f) 
            ) {
                Column {
                    // Заголовок списку
                     Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "База продуктів",
                            style = Typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        
                        // Кнопка "Додати свій продукт" у заголовку
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showAddProductOverlay = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Створити",
                                style = Typography.labelMedium,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    HorizontalDivider(color = Color(0xFFF0F0F0))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        items(foodList) { item ->
                            // ... item content ... (keeping same)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { itemToAdd = item }, 
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.food.name, style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    val dbCals = item.nutrients?.calories ?: 0
                                    val calcCals = com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(
                                        item.nutrients?.proteins ?: 0f,
                                        item.nutrients?.fats ?: 0f,
                                        item.nutrients?.carbohydrates ?: 0f
                                    )
                                    val displayCals = if (dbCals > 0) dbCals else calcCals
                                    Text("$displayCals ккал", style = Typography.bodySmall, color = Color.Gray)
                                }
                                
                                 Icon(
                                    Icons.Default.Edit, 
                                    contentDescription = "Edit", 
                                    tint = Color(0xFFFF8A00),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { editProductItem = item }
                                )
                                
                                Spacer(modifier = Modifier.width(16.dp))

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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. СЕРЕДНЯ СЕКЦІЯ: Пошук
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(2.dp, RoundedCornerShape(24.dp)),
                placeholder = { Text("Пошук продукту...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = "Search", 
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    ) 
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFFFF8A00),
                    unfocusedBorderColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. НИЖНЯ СЕКЦІЯ: Вкладки їжі та список спожитого
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.65f) 
                    .shadow(4.dp, RoundedCornerShape(32.dp))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Покращений рядок вкладок
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MealType.entries.forEachIndexed { index, meal ->
                            val isSelected = selectedMeal == meal
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp) // Фіксована висота для однаковості
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) Color(0xFFFF8A00) else Color.Transparent)
                                    .clickable { selectedMeal = meal },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = meal.title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                    Text(
                                        text = meal.timeRange,
                                        fontSize = 9.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray
                                    )
                                }
                            }
                            
                            // Логіка вертикального роздільника
                            if (index < MealType.entries.size - 1) {
                                VerticalDivider(
                                    modifier = Modifier
                                        .height(30.dp)
                                        .width(1.dp)
                                        .align(Alignment.CenterVertically), 
                                    color = Color(0xFFEEEEEE)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF5F5F5))

                    // Список спожитого для обраного прийому їжі
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
                                contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 100.dp)
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

                    // 4. ПІДВАЛ: Аналіз прийому їжі
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val totalCals = filteredLogs.sumOf { item ->
                            val ratio = item.consumed.grams / 100f
                            val p = (item.nutrients?.proteins ?: 0f) * ratio
                            val f = (item.nutrients?.fats ?: 0f) * ratio
                            val c = (item.nutrients?.carbohydrates ?: 0f) * ratio
                            com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(p, f, c)
                        }
                        
                        // Отримуємо ціль з ViewModel (якщо вона додана)
                        val targetCalories = viewModel.targetCalories // Треба переконатися, що це існує
                        
                        val mealTargetPercentage = when(selectedMeal) {
                            MealType.BREAKFAST -> 0.25f
                            MealType.LUNCH -> 0.35f
                            MealType.DINNER -> 0.30f
                            MealType.SNACK -> 0.10f
                        }
                        val mealTarget = (targetCalories * mealTargetPercentage).toInt()
                        
                        // Аналіз результатів
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
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Діалоги
    if (showAddProductOverlay) {
        AddProductOverlay(
            onDismiss = { showAddProductOverlay = false },
            onSave = { name, prot, fats, carbs, cals ->
                viewModel.addFood(name, prot, fats, carbs, cals)
                showAddProductOverlay = false
            }
        )
    }

    if (itemToAdd != null) {
        AddToMealDialog(
            item = itemToAdd!!,
            mealType = selectedMeal.title,
            onDismiss = { itemToAdd = null },
            onAdd = { grams ->
                viewModel.logConsumedFood(itemToAdd!!.food.foodId, grams, selectedMeal.title)
                itemToAdd = null
            }
        )
    }
    
    if (editProductItem != null) {
        val item = editProductItem!!
        val calculated = com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(
            item.nutrients?.proteins ?: 0f,
            item.nutrients?.fats ?: 0f,
            item.nutrients?.carbohydrates ?: 0f
        )
        EditProductOverlay(
            initialName = item.food.name,
            initialProteins = item.nutrients?.proteins ?: 0f,
            initialFats = item.nutrients?.fats ?: 0f,
            initialCarbs = item.nutrients?.carbohydrates ?: 0f,
            initialCalories = if ((item.nutrients?.calories ?: 0) > 0) item.nutrients!!.calories else calculated,
            onDismiss = { editProductItem = null },
            onSave = { name, prot, fats, carbs, cals ->
                viewModel.updateFood(item, name, prot, fats, carbs, cals)
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
    val p = (item.nutrients?.proteins ?: 0f) * (grams / 100f)
    val f = (item.nutrients?.fats ?: 0f) * (grams / 100f)
    val c = (item.nutrients?.carbohydrates ?: 0f) * (grams / 100f)
    val cals = com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(p, f, c)
    
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
