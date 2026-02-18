package com.example.wellminder.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.example.wellminder.ui.components.BottomNavigationBar
import com.example.wellminder.ui.components.TopBarSection
import com.example.wellminder.ui.theme.Typography

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    var showHealthConnect by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showChangeGoals by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showAccountInfo by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showEditAccountData by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showDeleteDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showLogoutDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { route ->
            if (route == "login") {
                onNavigate("login")
            }
        }
    }

    if (showHealthConnect) {
        BackHandler { showHealthConnect = false }
        HealthConnectScreen(onBack = { showHealthConnect = false })
        return
    }

    if (showChangeGoals) {
        BackHandler { showChangeGoals = false }
        ChangeGoalsScreen(
            onFinish = {
                showChangeGoals = false
                android.widget.Toast.makeText(context, "Ваші цілі були змінені", android.widget.Toast.LENGTH_SHORT).show()
            },
            viewModel = viewModel
        )
        return
    }

    if (showEditAccountData) {
        BackHandler { showEditAccountData = false }
        EditAccountDataScreen(
            onSave = {
                showEditAccountData = false
                showAccountInfo = false // Повернення до основного профілю
                android.widget.Toast.makeText(context, "Ваші дані змінені", android.widget.Toast.LENGTH_SHORT).show()
            },
            viewModel = viewModel
        )
        return
    }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = { 
                showDeleteDialog = false
                viewModel.deleteAccount()
            }
        )
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFEFF5FF),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "profile",
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        if (showAccountInfo) {
            BackHandler { showAccountInfo = false }
            Box(modifier = Modifier.padding(paddingValues)) {
                AccountInfoScreen(
                    onBack = { showAccountInfo = false },
                    onEdit = { showEditAccountData = true },
                    name = viewModel.userProfile?.name ?: "Гість",
                    email = viewModel.userEmail ?: "не вказано"
                )
            }
        } else {
             Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(modifier = Modifier.height(16.dp))

            TopBarSection()

            Spacer(modifier = Modifier.height(24.dp))

            // User Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Про Вас",
                        style = Typography.titleMedium.copy(fontSize = 20.sp),
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    UserInfoRow("Ім'я:", viewModel.userProfile?.name ?: "Гість")
                    val genderDisplay = when(viewModel.userProfile?.gender?.lowercase()) {
                        "male", "чоловік" -> "Чоловік"
                        "female", "жінка" -> "Жінка"
                        else -> viewModel.userProfile?.gender ?: "-"
                    }
                    UserInfoRow("Стать:", genderDisplay)
                    UserInfoRow("Поточна вага:", "${viewModel.userProfile?.currentWeight?.toInt() ?: 0}кг")
                    val goalText = when(viewModel.userGoals?.goalType) {
                        "LOSE" -> "Схуднути"
                        "GAIN" -> "Набрати вагу"
                        else -> "Підтримувати форму"
                    }
                    UserInfoRow("Мета:", goalText)
                    // Обчислюємо вік за датою народження
                    val age = if (viewModel.userProfile?.dateOfBirth != null && viewModel.userProfile!!.dateOfBirth > 0) {
                         val birthDate = java.time.Instant.ofEpochMilli(viewModel.userProfile!!.dateOfBirth).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                         val now = java.time.LocalDate.now()
                         java.time.Period.between(birthDate, now).years.toString()
                    } else "0"
                    
                    UserInfoRow("Вік:", age)
                    UserInfoRow("Зріст:", "${viewModel.userProfile?.height ?: 0}")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопки дій
            ProfileActionButton(
                text = "Дані про акаунт",
                icon = Icons.Default.Email,
                onClick = { showAccountInfo = true }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileActionButton(
                text = "Змінити персональні цілі",
                icon = Icons.Default.Refresh,
                onClick = { showChangeGoals = true }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { showHealthConnect = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(4.dp, RoundedCornerShape(32.dp)),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp) // Розмір контейнера
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.wellminder.R.drawable.hc),
                            contentDescription = "Health Connect",
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                            modifier = Modifier.size(28.dp) // Розмір логотипа всередині кола
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "З’єднання з Health Connect",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
            
            
            Spacer(modifier = Modifier.height(16.dp))

            
            // Кнопка виходу
            ProfileActionButton(
                text = "Вийти з акаунту",
                icon = Icons.Filled.Close,
                onClick = { showLogoutDialog = true },
                containerColor = Color(0xFFFF8A00)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileActionButton(
                text = "Видалити акаунт",
                icon = Icons.Default.Delete,
                onClick = { showDeleteDialog = true },
                containerColor = Color(0xFFD32F2F) // Червоний для видалення
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    }
}

@Composable
fun UserInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label ",
            style = Typography.bodyMedium,
            color = Color.Black
        )
        Text(
            text = value,
            style = Typography.bodyMedium,
            color = Color.Black // Або трохи світліший колір за потреби
        )
    }
}

@Composable
fun ProfileActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    containerColor: Color = Color(0xFFFF8A00)
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(32.dp)),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black, // Колір іконки чорний, згідно з дизайном
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = text,
                style = Typography.bodyMedium.copy(fontSize = 16.sp),
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
