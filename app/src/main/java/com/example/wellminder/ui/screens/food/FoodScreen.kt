package com.example.wellminder.ui.screens.food

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.components.BottomNavigationBar
import com.example.wellminder.ui.components.TopBarSection
import com.example.wellminder.ui.theme.Typography

data class FoodItem(
    val name: String,
    val weight: String,
    val calories: String
)

@Composable
fun FoodScreen(
    onNavigate: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    // Static data for the list
    val foodList = listOf(
        FoodItem("Апельсин", "200г,", "92ккал"),
        FoodItem("Апельсиновий сік", "200г,", "92ккал"),
        FoodItem("Апельсиновий кекс", "200г,", "300ккал")
    )
    
    
    // States for Overlays
    var showConsumedFoodOverlay by remember { mutableStateOf(false) }
    var showAddProductOverlay by remember { mutableStateOf(false) }

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
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            TopBarSection()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Food List Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    foodList.forEachIndexed { index, item ->
                        FoodListItem(item)
                        if (index < foodList.size) {
                            HorizontalDivider(
                                color = Color(0xFFEEEEEE),
                                thickness = 1.dp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp)) // Min height so list area isn't too small
                    
                    Text(
                        text = "Страви чи продукту немає у додатку?\nСтворіть свій їх самі!",
                        style = Typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showAddProductOverlay = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Додати!",
                            style = Typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Meal Selection (Detailed View Mode) with integrated Advice
            FoodSelectionView(
                onShowOverlay = { showConsumedFoodOverlay = true },
                adviceContent = {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ви поснідали на 650 ккал. Цього\nдостатньо для сніданку!",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = Typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Не забудьте про денну норму води!",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = Typography.bodyMedium.copy(color = Color.Black)
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(84.dp))
        }



        if (showAddProductOverlay) {
            AddProductOverlay(
                onDismiss = { showAddProductOverlay = false },
                onSave = { 
                    // TODO: Save logic
                    showAddProductOverlay = false 
                }
            )
        }

        if (showConsumedFoodOverlay) {
            com.example.wellminder.ui.screens.food.ConsumedFoodOverlay(
                onDismiss = { showConsumedFoodOverlay = false }
            )
        }
    }
}

@Composable
fun FoodListItem(item: FoodItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Name
        Text(
            text = item.name,
            style = Typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Actions
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
            tint = Color(0xFFFF8A00),
            modifier = Modifier.size(20.dp).clickable(enabled = false) {}
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color(0xFFD32F2F),
            modifier = Modifier.size(20.dp).clickable(enabled = false) {}
        )
        
        Spacer(modifier = Modifier.width(24.dp))
        
        // Info
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = item.weight,
                style = Typography.bodySmall,
                fontSize = 12.sp
            )
            Text(
                text = item.calories,
                style = Typography.bodySmall,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun FoodSelectionView(
    onShowOverlay: () -> Unit,
    adviceContent: @Composable () -> Unit
) {
    var selectedMeal by remember { mutableStateOf(com.example.wellminder.ui.components.MealType.BREAKFAST) }
    
    com.example.wellminder.ui.components.ReusableFoodSection(
        selectedMeal = selectedMeal,
        onMealSelect = { 
            if (selectedMeal == it) {
                onShowOverlay() 
            } else {
                selectedMeal = it 
            }
        },
        activeTabContent = { meal ->
            // Specific content for Food Screen (Arrow Icon)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "View",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                        // .clickable { onShowOverlay() } // Removed: Handled by parent container
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Перегляньте\nваш ${meal.title.lowercase()}", 
                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp,
                    fontSize = 12.sp
                )
            }
        },
        footerContent = adviceContent
    )
}
