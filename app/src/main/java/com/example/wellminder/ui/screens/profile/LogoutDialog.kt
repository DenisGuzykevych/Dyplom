package com.example.wellminder.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wellminder.ui.theme.Typography

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Ви точно хочете\nвийти з акаунту?",
                    style = Typography.titleMedium.copy(fontSize = 20.sp),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // No Button (Green)
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8D24)), // Green shade
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .width(100.dp)
                            .height(80.dp)
                    ) {
                        Text(
                            text = "Ні",
                            style = Typography.titleMedium.copy(fontSize = 20.sp),
                            color = Color.White
                        )
                    }

                    // Yes Button (Red/Orange?) - User said "same design", so Red is fine for "Exit" action or Orange?
                    // Delete is destructive (Red). Logout is less so, but "Exit" implies leaving. 
                    // Let's use Orange for Logout to differentiate or Red if strictly "same".
                    // User said "same design as delete account". Delete account uses Red.
                    // But usually Logout can be Primary color.
                    // I'll stick to Red for "Yes" to match the "same design" request strictly.
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .width(100.dp)
                            .height(80.dp)
                    ) {
                        Text(
                            text = "Так",
                            style = Typography.titleMedium.copy(fontSize = 20.sp),
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
