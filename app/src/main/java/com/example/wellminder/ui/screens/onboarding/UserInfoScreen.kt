package com.example.wellminder.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.theme.OrangePrimary
import com.example.wellminder.ui.theme.Typography

import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun UserInfoScreen(
    onNavigateToHome: (Int, Float, Float) -> Unit, // Keeping signature for minimal disruption, though we could just use () -> Unit
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    
    val onboardingState by viewModel.onboardingState.collectAsState()
    
    LaunchedEffect(onboardingState) {
        if (onboardingState is OnboardingState.Success) {
            // Data is saved to DB, now navigate
             val ageInt = age.toIntOrNull() ?: 0
             val weightFloat = weight.toFloatOrNull() ?: 0f
             val heightFloat = height.toFloatOrNull() ?: 0f
            onNavigateToHome(ageInt, weightFloat, heightFloat)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Розкажіть більше",
            style = Typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = OrangePrimary
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Зріст та вага допомагають нам точніше розрахувати калорії та кроки",
            style = Typography.bodyLarge.copy(color = Color.Gray),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = age,
            onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
            label = { Text("Вік") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                focusedLabelColor = OrangePrimary,
                cursorColor = OrangePrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) weight = it },
            label = { Text("Вага (кг)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                focusedLabelColor = OrangePrimary,
                cursorColor = OrangePrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = height,
            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) height = it },
            label = { Text("Зріст (см)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                focusedLabelColor = OrangePrimary,
                cursorColor = OrangePrimary
            )
        )
        
         if (onboardingState is OnboardingState.Error) {
            Text(
                text = (onboardingState as OnboardingState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { 
                val ageInt = age.toIntOrNull()
                val weightFloat = weight.toFloatOrNull()
                val heightFloat = height.toFloatOrNull()
                
                if (ageInt != null && weightFloat != null && heightFloat != null) {
                    viewModel.completeOnboarding(ageInt, weightFloat, heightFloat)
                }
            },
            enabled = age.isNotEmpty() && weight.isNotEmpty() && height.isNotEmpty() && onboardingState != OnboardingState.Loading,
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
             if (onboardingState == OnboardingState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Завершити",
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
