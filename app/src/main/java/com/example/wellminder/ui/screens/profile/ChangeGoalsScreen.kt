package com.example.wellminder.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellminder.ui.theme.Typography
import androidx.hilt.navigation.compose.hiltViewModel

// Step Enum
enum class ChangeGoalStep {
    GOAL_SELECTION,
    USER_DETAILS
}

@Composable
fun ChangeGoalsScreen(
    onFinish: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var currentStep by remember { mutableStateOf(ChangeGoalStep.GOAL_SELECTION) }
    
    // Load initial data
    val currentProfile = viewModel.userProfile
    val currentGoals = viewModel.userGoals
    
    // State for Goal
    // Assuming "MAINTAIN" is default if null
    var selectedGoal by remember { 
        mutableStateOf(currentGoals?.goalType ?: "MAINTAIN") 
    }
    
    // State for Stats
    // Convert currentWeight (Float) to String. Drop decimal if .0
    var weight by remember { 
        mutableStateOf(currentProfile?.currentWeight?.let { if(it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") 
    }
    var height by remember { 
        mutableStateOf(currentProfile?.height?.toString() ?: "") 
    }
    // Calculate Age if not available, or use Prefs age? 
    // ProfileViewModel logic uses estimatedDOB. Let's try to reverse it or just use a derived value.
    // For now, let's keep it empty if we can't easily calc, or calc from DOB.
    val calculatedAge = remember(currentProfile?.dateOfBirth) {
        if (currentProfile?.dateOfBirth != null && currentProfile.dateOfBirth > 0) {
            val birthDate = java.time.Instant.ofEpochMilli(currentProfile.dateOfBirth).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            val now = java.time.LocalDate.now()
            java.time.Period.between(birthDate, now).years.toString()
        } else ""
    }
    var age by remember { mutableStateOf(calculatedAge) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF5FF))
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header (Step Counter)
        Text(
            text = "Крок ${if (currentStep == ChangeGoalStep.GOAL_SELECTION) "1" else "2"} з 2",
            style = Typography.bodyLarge,
            fontSize = 20.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Content based on step
        when (currentStep) {
            ChangeGoalStep.GOAL_SELECTION -> {
                GoalSelectionStep(
                    selectedGoal = selectedGoal,
                    onGoalSelected = { selectedGoal = it },
                    onNext = { currentStep = ChangeGoalStep.USER_DETAILS }
                )
            }
            ChangeGoalStep.USER_DETAILS -> {
                UserDetailsStep(
                    weight = weight,
                    onWeightChange = { weight = it },
                    height = height,
                    onHeightChange = { height = it },
                    age = age,
                    onAgeChange = { age = it },
                    onFinish = {
                        // Validate and Save
                        if (weight.isNotEmpty() && height.isNotEmpty() && age.isNotEmpty()) {
                            viewModel.saveGoalsAndStats(
                                goal = selectedGoal,
                                weight = weight.toFloatOrNull() ?: 0f,
                                height = height.toIntOrNull() ?: 0,
                                age = age.toIntOrNull() ?: 0
                            )
                            onFinish()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GoalSelectionStep(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Для чого вам цей додаток?",
            style = Typography.headlineSmall,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Radio Buttons
        GoalRadioButton(
            text = "Схуднути",
            selected = selectedGoal == "LOSE",
            onClick = { onGoalSelected("LOSE") }
        )
        GoalRadioButton(
            text = "Підтримувати форму",
            selected = selectedGoal == "MAINTAIN",
            onClick = { onGoalSelected("MAINTAIN") }
        )
        GoalRadioButton(
            text = "Набрати масу",
            selected = selectedGoal == "GAIN",
            onClick = { onGoalSelected("GAIN") }
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(80.dp)
        ) {
            Text(
                text = "Перейти далі",
                style = Typography.titleMedium,
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun UserDetailsStep(
    weight: String,
    onWeightChange: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Введіть трохи даних\nпро себе",
            style = Typography.headlineSmall,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Inputs
        CustomInput(label = "Вага", placeholder = "кг", value = weight, onValueChange = onWeightChange)
        Spacer(modifier = Modifier.height(16.dp))
        CustomInput(label = "Зріст", placeholder = "см", value = height, onValueChange = onHeightChange)
        Spacer(modifier = Modifier.height(16.dp))
        CustomInput(label = "Вік", placeholder = "років", value = age, onValueChange = onAgeChange)

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(80.dp)
        ) {
            Text(
                text = "Все готово!",
                style = Typography.titleMedium,
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun GoalRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 32.dp)
            .clickable(onClick = onClick)
    ) {
        // Custom Radio Circle
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = if (selected) Color(0xFFFF8A00) else Color(0xFFEEEEEE),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .run {
                     if (!selected) {
                         this.border(
                             width = 2.dp,
                             color = Color(0xFFE0E0E0),
                             shape = androidx.compose.foundation.shape.CircleShape
                         )
                     } else this
                }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = Typography.bodyLarge,
            fontSize = 20.sp,
            color = Color.Black
        )
    }
}

@Composable
fun CustomInput(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
        Text(
            text = label,
            style = Typography.bodyLarge,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            textStyle = Typography.bodyLarge.copy(color = Color.Black),
            singleLine = true,
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
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}
