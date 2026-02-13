package com.example.wellminder.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
fun GenderScreen(
    onNavigateToUserInfo: (String) -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var selectedGender by remember { mutableStateOf<String?>(null) }
    
    // Check if gender is already selected in prefs? Or just let user select.
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Розкажіть про себе",
            style = Typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = OrangePrimary
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Для кращого досвіду нам потрібно знати вашу стать",
            style = Typography.bodyLarge.copy(color = Color.Gray),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GenderCard(
                text = "Чоловік",
                isSelected = selectedGender == "Male",
                onClick = { selectedGender = "Male" }
            )
            GenderCard(
                text = "Жінка",
                isSelected = selectedGender == "Female",
                onClick = { selectedGender = "Female" }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { 
                selectedGender?.let { gender ->
                     viewModel.setGender(gender)
                     onNavigateToUserInfo(gender)
                }
            },
            enabled = selectedGender != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OrangePrimary,
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray
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
fun GenderCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(140.dp)
            .background(
                color = if (isSelected) OrangePrimary else Color.Transparent,
                shape = RoundedCornerShape(100.dp) // Circle
            )
            .border(
                width = 2.dp,
                color = if (isSelected) OrangePrimary else Color.LightGray,
                shape = RoundedCornerShape(100.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = Typography.titleLarge.copy(
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
