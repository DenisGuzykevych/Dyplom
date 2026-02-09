package com.example.wellminder.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBarSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo Placeholder
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.White, CircleShape)
                .shadow(4.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Bolt, 
                contentDescription = "Logo",
                tint = Color(0xFFFF5722),
                modifier = Modifier.size(32.dp)
            )
        }
        
        // Profile or Camera cutout area
        // Assuming just a simple profile icon for now


    }
}

enum class MealType(val title: String, val timeRange: String) {
    BREAKFAST("Сніданок", "06:00 - 12:00"),
    LUNCH("Обід", "12:00 - 16:00"),
    DINNER("Вечеря", "16:00 - 20:00"),
    SNACK("Перекус", "20:00 - 00:00")
}

@Composable
fun ReusableFoodSection(
    selectedMeal: MealType,
    onMealSelect: (MealType) -> Unit,
    // Content for the ACTIVE tab (the one that is selected)
    activeTabContent: @Composable (MealType) -> Unit,
    footerContent: (@Composable () -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
    ) {
        Column {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                MealType.entries.forEach { meal ->
                    ReusableMealTabItem(
                        meal = meal,
                        isSelected = selectedMeal == meal,
                        onSelect = { onMealSelect(meal) },
                        activeContent = { activeTabContent(meal) },
                        modifier = Modifier.weight(1f)
                    )
                    if (meal != MealType.SNACK) {
                        VerticalDivider(
                            color = Color(0xFFEEEEEE),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            }
            // Footer
            if (footerContent != null) {
                HorizontalDivider(
                     color = Color(0xFFEEEEEE),
                     thickness = 1.dp
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    footerContent()
                }
            }
        }
    }
}

@Composable
fun ReusableMealTabItem(
    meal: MealType,
    isSelected: Boolean,
    onSelect: () -> Unit,
    activeContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color(0xFFFF8A00) else Color.White
    val contentColor = if (isSelected) Color.White else Color.Black
    
    // We strictly use the size/padding from the "FoodScreen" analysis to ensure identical size
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onSelect)
            .background(backgroundColor)
            .padding(vertical = 16.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Title
        Text(
            text = meal.title,
            style = com.example.wellminder.ui.theme.Typography.bodyMedium.copy(
                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
            ),
            color = contentColor,
            maxLines = 1,
            softWrap = false,
            fontSize = 13.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Time
        Text(
            text = meal.timeRange,
            style = com.example.wellminder.ui.theme.Typography.labelSmall,
            color = contentColor.copy(alpha = 0.9f),
            fontSize = 10.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isSelected) {
            activeContent()
        } else {
             // Fixed height spacer to maintain height when inactive, matching the active state rough height
             Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
   NavigationBar(
       containerColor = Color.White,
       contentColor = Color.Black,
       modifier = Modifier.height(68.dp)
   ) {
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Rounded.DirectionsRun, contentDescription = "Home") },
            label = { Text("Головний", fontSize = 10.sp) },
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF8A00),
                selectedTextColor = Color(0xFFFF8A00),
                indicatorColor = Color.White,
                unselectedIconColor = Color.Black
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Fastfood, contentDescription = "Food") },
            label = { Text("Їжа", fontSize = 10.sp) },
            selected = currentRoute == "food",
            onClick = { onNavigate("food") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF8A00),
                selectedTextColor = Color(0xFFFF8A00), // Added highlighted color for consistency
                indicatorColor = Color.White,
                unselectedIconColor = Color.Black
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Bolt, contentDescription = "Stats") },
            label = { Text("Активність", fontSize = 10.sp) },
            selected = currentRoute == "stats",
            onClick = { onNavigate("stats") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF8A00),
                selectedTextColor = Color(0xFFFF8A00),
                indicatorColor = Color.White,
                unselectedIconColor = Color.Black
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Акаунт", fontSize = 10.sp) },
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF8A00),
                selectedTextColor = Color(0xFFFF8A00),
                indicatorColor = Color.White,
                unselectedIconColor = Color.Black
            )
        )
   }

}

@Composable
fun PremiumCheckButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .shadow(4.dp, CircleShape)
            .background(Color(0xFF2E8B15), CircleShape) // Darker Green Base
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Inner Polygon (Lighter Green)
        Canvas(modifier = Modifier.size(40.dp)) {
            val path = androidx.compose.ui.graphics.Path().apply {
                val w = size.width
                val h = size.height
                val cx = w / 2
                val cy = h / 2
                val r = w / 2
                
                // Octagon points
                moveTo(cx + r, cy) // 0
                lineTo(cx + r * 0.7f, cy + r * 0.7f) // 45
                lineTo(cx, cy + r) // 90
                lineTo(cx - r * 0.7f, cy + r * 0.7f) // 135
                lineTo(cx - r, cy) // 180
                lineTo(cx - r * 0.7f, cy - r * 0.7f) // 225
                lineTo(cx, cy - r) // 270
                lineTo(cx + r * 0.7f, cy - r * 0.7f) // 315
                close()
            }
            drawPath(path, Color(0xFF43A047)) // Lighter Green
        }
        
        // Icon
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Done",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}
