package com.example.wellminder.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.theme.OrangePrimary
import com.example.wellminder.ui.theme.Typography
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun GoalScreen(
    onNavigateToUserInfo: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    // Local state for immediate selection
    var selectedGoal by remember { mutableStateOf("MAINTAIN") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Яка ваша ціль?",
            style = Typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = OrangePrimary
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Ми адаптуємо додаток під ваші потреби",
            style = Typography.bodyLarge.copy(color = Color.Gray),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Options
        GoalOption(
            text = "Схуднути",
            isSelected = selectedGoal == "LOSE",
            onClick = { selectedGoal = "LOSE" }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        GoalOption(
            text = "Підтримувати форму",
            isSelected = selectedGoal == "MAINTAIN",
            onClick = { selectedGoal = "MAINTAIN" }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        GoalOption(
            text = "Набрати масу",
            isSelected = selectedGoal == "GAIN",
            onClick = { selectedGoal = "GAIN" }
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { 
                viewModel.setGoal(selectedGoal)
                onNavigateToUserInfo() 
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OrangePrimary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Далі",
                style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun GoalOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                color = if (isSelected) OrangePrimary.copy(alpha = 0.1f) else Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) OrangePrimary else Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = if (isSelected) OrangePrimary else Color.Transparent,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (isSelected) OrangePrimary else Color.Gray,
                    shape = CircleShape
                )
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = text,
            style = Typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) OrangePrimary else Color.Black
            )
        )
    }
}
