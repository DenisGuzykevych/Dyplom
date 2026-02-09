package com.example.wellminder.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun


import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.theme.Typography
import com.example.wellminder.ui.components.TopBarSection
import com.example.wellminder.ui.components.BottomNavigationBar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    var showWaterOverlay by remember { mutableStateOf(false) }
    var showCaloriesOverlay by remember { mutableStateOf(false) }
    var showStepsOverlay by remember { mutableStateOf(false) }
    var showAddFoodOverlay by remember { mutableStateOf(false) }
    var stepCount by remember { mutableIntStateOf(0) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(viewModel.permissions)) {
            viewModel.fetchSteps()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchSteps()
    }

    LaunchedEffect(viewModel.steps) {
        stepCount = viewModel.steps
    }
    val waterTarget = 2000
    val stepTarget = 8000

    
    Scaffold(
        containerColor = Color(0xFFEFF5FF),
        bottomBar = { 
            BottomNavigationBar(
                currentRoute = "home",
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
            
            // Top Bar with Logo and Profile
            TopBarSection()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Norm Cards
            NormCard(
                title = "Норма води",
                icon = Icons.Rounded.WaterDrop,
                iconColor = Color(0xFF00C2FF),
                progress = viewModel.waterIntake.toFloat() / waterTarget.toFloat(),
                progressColor = Color(0xFF00C2FF),
                subtitle = "Випито ${viewModel.waterIntake / 1000f}л\nз ${waterTarget / 1000}л",
                onClick = { showWaterOverlay = true }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            NormCard(
                title = "Норма калорій",
                icon = Icons.Rounded.Bolt,
                iconColor = Color(0xFFFFC107),
                progress = 0f,
                progressColor = Color(0xFF4CAF50), // Green for calories
                subtitle = "Спожито 0ккал\nз 2500ккал",
                onClick = { showCaloriesOverlay = true }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            NormCard(
                title = "Норма кроків",
                icon = Icons.AutoMirrored.Rounded.DirectionsRun,
                iconColor = Color(0xFF757575),
                progress = stepCount.toFloat() / stepTarget.toFloat(),
                progressColor = Color(0xFFFF002E),
                subtitle = "Пройдено $stepCount кроків\nз $stepTarget",
                onClick = { showStepsOverlay = true }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Food Section
            FoodSection(
                onAddFood = { showAddFoodOverlay = true }
            )
            

            

            
            Spacer(modifier = Modifier.height(84.dp))
        }
        }
        
        if (showWaterOverlay) {
            WaterOverlay(
                currentIntake = viewModel.waterIntake,
                targetIntake = waterTarget,
                onDismiss = { showWaterOverlay = false },
                onAdd = { amount -> viewModel.addWater(amount) },
                onRemove = { amount -> viewModel.addWater(-amount) }
            )
        }

        if (showCaloriesOverlay) {
            CaloriesOverlay(
                onDismiss = { showCaloriesOverlay = false }
            )
        }

        if (showStepsOverlay) {
            StepsOverlay(
                currentSteps = stepCount,
                targetSteps = stepTarget,
                onDismiss = { showStepsOverlay = false },
                onSave = { added -> viewModel.addSteps(added) },
                stepsPerKm = viewModel.stepsPerKm,
                distanceKm = viewModel.distanceKm,
                onRequestHealthConnect = { permissionLauncher.launch(viewModel.permissions) },
                onConvertKm = { km -> viewModel.convertKmToSteps(km) }
            )
        }

        if (showAddFoodOverlay) {
            com.example.wellminder.ui.screens.food.AddFoodOverlay(
                onDismiss = { showAddFoodOverlay = false },
                onAdd = { /* TODO: Add logic */ showAddFoodOverlay = false }
            )
        }
    }



@Composable
fun NormCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    progress: Float,
    progressColor: Color,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(4.dp, RoundedCornerShape(32.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Circular Progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(70.dp)
            ) {
                CircularProgress(
                    progress = progress,
                    color = progressColor,
                    size = 70.dp,
                    strokeWidth = 10.dp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, style = Typography.titleMedium, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Text(
                    text = subtitle, 
                    style = Typography.bodySmall, 
                    color = Color.Gray,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Details",
                tint = Color(0xFFFF8A00)
            )
        }
    }
}

@Composable
fun CircularProgress(
    progress: Float,
    color: Color,
    size: Dp,
    strokeWidth: Dp
) {
    Canvas(modifier = Modifier.size(size)) {
        // Background track
        drawArc(
            color = Color(0xFFE0E0E0),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
        // Progress
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360 * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun FoodSection(
    onAddFood: () -> Unit
) {
    var selectedMeal by remember { mutableStateOf(com.example.wellminder.ui.components.MealType.BREAKFAST) }
    
    com.example.wellminder.ui.components.ReusableFoodSection(
        selectedMeal = selectedMeal,
        onMealSelect = { 
            if (selectedMeal == it) {
                onAddFood()
            } else {
                selectedMeal = it 
            }
        },
        activeTabContent = { meal ->
            // Specific content for Home Screen (Plus Button)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(48.dp)
                        // .clickable { onAddFood() } // Removed: Handled by parent container
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color(0xFFFF8A00),
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Додати\n" + meal.title.lowercase(),
                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        },
        footerContent = {
             Text(
                 text = "Слідкуйте за своїм харчуванням\nдля кращого самопочуття!",
                 modifier = Modifier.padding(16.dp),
                 textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                 style = Typography.bodyMedium
             )
        }
    )
}


