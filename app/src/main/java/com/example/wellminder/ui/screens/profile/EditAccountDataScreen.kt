package com.example.wellminder.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.theme.Typography
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import android.util.Patterns

@Composable
fun EditAccountDataScreen(
    onSave: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // Load initial Data
    val currentProfile = viewModel.userProfile
    val currentEmail = viewModel.userEmail
    
    var name by remember { mutableStateOf(currentProfile?.name ?: "") }
    var email by remember { mutableStateOf(currentEmail ?: "") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Оновлюємо стейт, коли завантажуються дані
    LaunchedEffect(currentProfile?.name, currentEmail) {
        if (name.isEmpty() && currentProfile?.name != null) name = currentProfile.name
        if (email.isEmpty() && currentEmail != null) email = currentEmail
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF5FF))
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Зміна даних",
            style = Typography.bodyMedium.copy(fontSize = 18.sp, color = Color.Gray),
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Змініть Ваші дані",
            style = Typography.headlineSmall,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Editable Email
        CustomInputInternal(
            label = "Email", 
            value = email, 
            onValueChange = { email = it }, 
            readOnly = false,
            placeholder = "Ваша пошта"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Editable Name
        CustomInputInternal(
            label = "Як до Вас звертатись?", 
            value = name, 
            onValueChange = { name = it },
            placeholder = "Ваше ім'я"
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        Column(modifier = Modifier.fillMaxWidth(0.9f)) {
            Text(
                text = "Новий пароль (залиште пустим, якщо не міняєте)",
                style = Typography.bodyLarge,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Пароль", color = Color.Gray) },
                textStyle = Typography.bodyLarge.copy(color = Color.Black),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Сховати пароль" else "Показати пароль"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description, tint = Color.Gray)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFFF8A00),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                errorMessage = null
                if (name.isBlank() || email.isBlank()) {
                    errorMessage = "Заповніть обов'язкові поля (Ім'я та Email)"
                    return@Button
                }
                
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = "Введіть коректний Email"
                    return@Button
                }

                viewModel.updateAccountData(
                    newName = name.trim(), 
                    newEmail = email.trim(), 
                    newPassword = password.takeIf { it.isNotBlank() },
                    onSuccess = {
                        onSave()
                    },
                    onError = { error ->
                        errorMessage = error
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(80.dp)
        ) {
            Text(
                text = "Зберегти дані",
                style = Typography.titleMedium,
                fontSize = 20.sp,
                color = Color.White
            )
        }
        
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage!!, color = Color.Red, style = Typography.bodyMedium)
        }
    }
}

@Composable
private fun CustomInputInternal(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    readOnly: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = Typography.bodyLarge,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            placeholder = { Text(placeholder, color = Color.Gray) },
            textStyle = Typography.bodyLarge.copy(color = if (readOnly) Color.Gray else Color.Black),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(25.dp))
                .background(if (readOnly) Color(0xFFE0E0E0) else Color.White),
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = if(readOnly) Color.Transparent else Color(0xFFFF8A00),
                focusedContainerColor = if (readOnly) Color(0xFFE0E0E0) else Color.White,
                unfocusedContainerColor = if (readOnly) Color(0xFFE0E0E0) else Color.White
            )
        )
    }
}
