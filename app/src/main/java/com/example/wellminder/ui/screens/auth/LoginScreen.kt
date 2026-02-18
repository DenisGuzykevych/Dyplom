package com.example.wellminder.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.theme.Typography

import com.example.wellminder.ui.theme.OrangePrimary

import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Валідація
    val isEmailValid = remember(email) { android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    val isPasswordValid = remember(password) { password.length >= 8 }
    val isFormValid = isEmailValid && isPasswordValid
    
    val authState by viewModel.authState.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) } // Локальний стан для повідомлення

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                viewModel.resetState()
                onNavigateToHome()
            }
            is AuthState.Error -> {
                errorMessage = state.message
                viewModel.resetState() // Скидаємо помилку
            }
            else -> {}
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
            text = "WellMinder",
            style = Typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = OrangePrimary, // Updated to Orange
                fontSize = 40.sp
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "З поверненням!",
            style = Typography.bodyLarge.copy(color = Color.Gray)
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Електронна пошта") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                focusedLabelColor = OrangePrimary,
                cursorColor = OrangePrimary
            ),
             isError = (errorMessage != null) || (email.isNotEmpty() && !isEmailValid)
        )
        if (email.isNotEmpty() && !isEmailValid) {
            Text(
                text = "Невірний формат пошти",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                focusedLabelColor = OrangePrimary,
                cursorColor = OrangePrimary
            ),
             isError = errorMessage != null
        )
        
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp).align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) OrangePrimary else Color.Gray,
                contentColor = Color.White // Force white text
            ),
            enabled = authState != AuthState.Loading && isFormValid
        ) {
            if (authState == AuthState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Увійти",
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка пропуску
        TextButton(onClick = { viewModel.continueGuest() }) {
            Text(
                text = "Пропустити (Гостьовий режим)",
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Немає акаунту? ", color = Color.Gray)
            Text(
                text = "Зареєструватися",
                color = OrangePrimary, // Updated to Orange
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
