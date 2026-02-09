package com.example.wellminder.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.theme.Typography

@Composable
fun AccountInfoScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    name: String,
    email: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = Color(0xFFFF8A00),
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onBack() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp) // Approximate height
                .shadow(4.dp, RoundedCornerShape(32.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Дані акаунту",
                    style = Typography.titleMedium.copy(fontSize = 20.sp),
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "І'мя: $name",
                    style = Typography.bodyLarge,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Email: $email",
                    style = Typography.bodyLarge,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Change Data Button
        Button(
            onClick = onEdit,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
             Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Змінити дані акаунту",
                    style = Typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}
