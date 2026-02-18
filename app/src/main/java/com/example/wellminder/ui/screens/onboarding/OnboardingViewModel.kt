package com.example.wellminder.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellminder.data.local.dao.UserDao
import com.example.wellminder.data.local.entities.UserProfileEntity
import com.example.wellminder.data.manager.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userDao: UserDao,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _onboardingState = MutableStateFlow<OnboardingState>(OnboardingState.Idle)
    val onboardingState = _onboardingState.asStateFlow()

    fun setGender(gender: String) {
        preferenceManager.gender = gender
    }

    fun setGoal(goal: String) {
        preferenceManager.userGoal = goal
    }

    fun completeOnboarding(age: Int, weight: Float, height: Float) {
        viewModelScope.launch {
            _onboardingState.value = OnboardingState.Loading
            try {
                // Update Prefs
                preferenceManager.age = age
                preferenceManager.weight = weight
                preferenceManager.height = height
                
                // Update Database
                val userId = preferenceManager.userId
                // getLastUserProfile is unsafe in multi-user. Use getUserProfile(userId)
                val currentProfile = if (userId != -1L) userDao.getUserProfile(userId) else userDao.getLastUserProfile() 
                
                if (currentProfile != null) {
                    // Calculate approximate DOB
                    val now = LocalDate.now()
                    val birthDate = now.minusYears(age.toLong())
                    val dobTimestamp = birthDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                    val updatedProfile = currentProfile.copy(
                        userId = userId.takeIf { it != -1L } ?: currentProfile.userId,
                        gender = preferenceManager.gender ?: "Male",
                        currentWeight = weight,
                        height = height.toInt(),
                        dateOfBirth = dobTimestamp
                    )
                    userDao.updateProfile(updatedProfile)
                    
                    // Insert Weight Log for the initial weight
                    userDao.insertWeightLog(
                        com.example.wellminder.data.local.entities.WeightLogEntity(
                            userId = updatedProfile.userId,
                            date = System.currentTimeMillis(),
                            weightValue = weight
                        )
                    )

                    // Calculate and Save Initial Goals
                    val isMale = updatedProfile.gender.equals("Male", ignoreCase = true) || 
                                 updatedProfile.gender.equals("Чоловік", ignoreCase = true) ||
                                 updatedProfile.gender.equals("Чоловіча", ignoreCase = true)

                    val bmr = com.example.wellminder.util.GoalCalculator.calculateBMR(weight, height.toInt(), age, isMale)
                    val tdee = com.example.wellminder.util.GoalCalculator.calculateTDEE(bmr)
                    
                    val goal = preferenceManager.userGoal ?: "MAINTAIN"
                    
                    val (targetProteins, targetFats, targetCarbs) = com.example.wellminder.util.GoalCalculator.calculateMacros(weight, goal)
                    val targetCalories = com.example.wellminder.util.GoalCalculator.calculateCaloriesFromMacros(targetProteins, targetFats, targetCarbs)
                    val targetWater = com.example.wellminder.util.GoalCalculator.calculateWaterTarget(weight)
                    val targetSteps = com.example.wellminder.util.GoalCalculator.calculateStepTarget(goal)

                    val userGoal = com.example.wellminder.data.local.entities.UserGoalEntity(
                        userId = updatedProfile.userId,
                        targetWeight = weight,
                        targetWaterMl = targetWater,
                        targetSteps = targetSteps,
                        targetProteins = targetProteins,
                        targetFats = targetFats,
                        targetCarbs = targetCarbs,
                        goalType = goal
                    )
                    
                    // Check if goals exist, if so update, else insert.
                    val existingGoals = userDao.getUserGoals(updatedProfile.userId)
                    if (existingGoals != null) {
                         userDao.updateGoals(userGoal.copy(goalId = existingGoals.goalId))
                    } else {
                         userDao.insertGoals(userGoal)
                    }
                }
                
                preferenceManager.isOnboardingComplete = true
                _onboardingState.value = OnboardingState.Success
            } catch (e: Exception) {
                _onboardingState.value = OnboardingState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class OnboardingState {
    object Idle : OnboardingState()
    object Loading : OnboardingState()
    object Success : OnboardingState()
    data class Error(val message: String) : OnboardingState()
}
