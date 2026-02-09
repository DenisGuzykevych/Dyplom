package com.example.wellminder.ui.screens.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wellminder.ui.theme.Typography

@Composable
fun ConsumedFoodOverlay(
    onDismiss: () -> Unit
) {
    // Mock Data reusing strict structure
    val consumedItems = listOf(
        SearchFoodItem("Апельсин", "200г, 92ккал"),
        SearchFoodItem("Апельсиновий сік", "200г, 92ккал"),
        SearchFoodItem("Апельсиновий кекс", "200г, 300ккал")
    )

    var editingItem by remember { mutableStateOf<SearchFoodItem?>(null) }

    if (editingItem != null) {
        EditFoodDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onSave = { newGrams ->
                // TODO: Save logic
                editingItem = null
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Спожиті продукти",
                        style = Typography.headlineSmall,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Consumed List Container
                     Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, Color(0xFFE3F2FD)), // Light blue border
                        modifier = Modifier.fillMaxWidth()
                    ) {
                         Column {
                            consumedItems.forEachIndexed { index, item ->
                                ConsumedItemRow(
                                    item = item,
                                    onEdit = { editingItem = item }
                                )
                                if (index < consumedItems.size - 1) {
                                    HorizontalDivider(color = Color(0xFFE3F2FD), thickness = 1.dp)
                                }
                             }
                         }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Green Checkmark Button
                    com.example.wellminder.ui.components.PremiumCheckButton(
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
fun ConsumedItemRow(
    item: SearchFoodItem,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Name
        Text(
            text = item.name,
            style = Typography.bodyMedium,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Edit Icon (Orange)
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
            tint = Color(0xFFFF8A00),
            modifier = Modifier
                .size(20.dp)
                .clickable { onEdit() }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Delete Icon (Red)
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color(0xFFD32F2F), // Red
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(24.dp))
        
        // Details
        Text(
            text = item.details,
            style = Typography.bodySmall,
            fontSize = 12.sp,
            textAlign = TextAlign.End,
            color = Color.Black
        )
    }
}
