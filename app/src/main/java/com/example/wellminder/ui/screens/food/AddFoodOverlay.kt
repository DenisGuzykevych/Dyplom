package com.example.wellminder.ui.screens.food

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wellminder.ui.theme.Typography

data class SearchFoodItem(
    val name: String,
    val details: String, 
    val quantity: String? = null // For consumed items
)

@Composable
fun AddFoodOverlay(
    onDismiss: () -> Unit,
    onAdd: (SearchFoodItem) -> Unit
) {
    // Mock Data
    val searchResults = listOf(
        SearchFoodItem("Апельсин", "Калорійність на 100г\n47ккал"),
        SearchFoodItem("Апельсиновий сік", "Калорійність на 100г\n47ккал"),
        SearchFoodItem("Апельсиновий кекс", "Калорійність на 100г\n150ккал"),
        SearchFoodItem("Апельсиновий пиріг", "Калорійність на 100г\n160 ккал"),
        SearchFoodItem("Апельсинове морозиво", "Калорійність на 100г\n100 ккал")
    )

    val consumedItems = listOf(
        SearchFoodItem("Апельсин", "Калорійність на 100г\n47ккал"),
        SearchFoodItem("Апельсиновий сік", "Калорійність на 100г\n47ккал"),
        SearchFoodItem("Апельсиновий кекс", "Калорійність на 100г\n150ккал")
    )

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
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.9f) // Tall overlay
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Search Bar
                    OutlinedTextField(
                        value = "Апельси|", // Static for now as per design "Apelsi|" cursor
                        onValueChange = {},
                        placeholder = { Text("Пошук") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Results List (Light Blue Border container)
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White), // Transparent/White
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)), // Light border
                        modifier = Modifier.fillMaxWidth()
                    ) {
                         Column {
                             searchResults.forEachIndexed { index, item ->
                                SearchResultItem(item)
                                if (index < searchResults.size - 1) {
                                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                                }
                             }
                         }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Спожиті продукти",
                        style = Typography.headlineSmall,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Consumed Items List (Light Blue Border container)
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE3F2FD)), // Light blue border
                        modifier = Modifier.fillMaxWidth()
                    ) {
                         Column {
                             consumedItems.forEachIndexed { index, item ->
                                SearchResultItem(item) // Reusing same layout for simplicity as per screenshot looks similar
                                if (index < consumedItems.size - 1) {
                                    HorizontalDivider(color = Color(0xFFE3F2FD), thickness = 1.dp)
                                }
                             }
                         }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Green Checkmark Button
                    com.example.wellminder.ui.components.PremiumCheckButton(
                        onClick = onDismiss
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(item: SearchFoodItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            style = Typography.bodyMedium,
            fontSize = 16.sp,
            color = Color.Black
        )
        
        Text(
            text = item.details,
            style = Typography.bodySmall,
            fontSize = 12.sp,
            textAlign = TextAlign.End,
            color = Color.Black
        )
    }
}
