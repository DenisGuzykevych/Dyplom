package com.example.wellminder.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun EditAccountDataScreen(
    onSave: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // Load initial Data
    val currentProfile = viewModel.userProfile
    val currentEmail = viewModel.userEmail
    
    var name by remember { mutableStateOf(currentProfile?.name ?: "") }
    
    // Email is usually immutable in simple apps or requires re-auth. Let's make it read-only for now.
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF5FF))
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
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

        // Read-only Email
        CustomInputInternal(
            label = "Email", 
            value = currentEmail ?: "", 
            onValueChange = {}, 
            readOnly = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Editable Name
        CustomInputInternal(
            label = "Як до Вас звертатись?", 
            value = name, 
            onValueChange = { name = it },
            placeholder = "Ваше ім'я"
        )
        
        // Passwords - omitting for now as logic is complex for "Modernization" of UI without backend.
        // If user really wants it, we can add later.

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    viewModel.updateAccountData(name)
                    onSave()
                }
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
