package com.example.wellminder.data.repository

import com.example.wellminder.data.local.dao.UserDao
import com.example.wellminder.data.local.entities.UserEntity
import com.example.wellminder.data.local.entities.UserGoalEntity
import com.example.wellminder.data.local.entities.UserProfileEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val preferenceManager: com.example.wellminder.data.manager.PreferenceManager
) {

    suspend fun registerUser(
        name: String, 
        email: String, 
        passwordHash: String,
        gender: String,
        height: Int,
        weight: Float,
        birthDate: Long
    ): Boolean {
        return try {
            val user = UserEntity(email = email, passwordHash = passwordHash, isGuest = false)
            val profile = UserProfileEntity(
                userId = 0, // Set in Trigger/Transaction
                name = name,
                gender = gender,
                height = height,
                currentWeight = weight,
                dateOfBirth = birthDate
            )
            val goals = UserGoalEntity(
                userId = 0,
                targetWeight = weight, // Default target = current
                targetWaterMl = 2000,
                targetSteps = 6000,
                targetCalories = 2000
            )
            val userId = userDao.registerUser(user, profile, goals)
            preferenceManager.userId = userId
            preferenceManager.userName = name
            preferenceManager.gender = gender
            preferenceManager.weight = weight
            preferenceManager.height = height.toFloat()
            // Approximate age? Or just save DOB? Prefs has AGE (int).
            // Calculate age
           val age = java.time.Period.between(
                java.time.Instant.ofEpochMilli(birthDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                java.time.LocalDate.now()
            ).years
            preferenceManager.age = age
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun continueAsGuest(name: String, gender: String, height: Int, weight: Float, birthDate: Long): Boolean {
        return try {
            val user = UserEntity(isGuest = true)
            val profile = UserProfileEntity(
                userId = 0,
                name = name,
                gender = gender,
                height = height,
                currentWeight = weight,
                dateOfBirth = birthDate
            )
             val goals = UserGoalEntity(
                userId = 0,
                targetWeight = weight, 
                targetWaterMl = 2000,
                targetSteps = 6000,
                targetCalories = 2000
            )
            val userId = userDao.registerUser(user, profile, goals)
            preferenceManager.userId = userId
            preferenceManager.userName = name
             // Calculate age
           val age = java.time.Period.between(
                java.time.Instant.ofEpochMilli(birthDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                java.time.LocalDate.now()
            ).years
            preferenceManager.age = age
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loginUser(email: String, passwordHash: String): UserEntity? {
        val user = userDao.getUserByEmail(email)
        return if (user != null && user.passwordHash == passwordHash) {
            preferenceManager.userId = user.userId
            // Also fetch profile and update prefs? Optional but good for consistency
             val profile = userDao.getUserProfile(user.userId)
             if (profile != null) {
                 preferenceManager.userName = profile.name
                 preferenceManager.gender = profile.gender
                 preferenceManager.weight = profile.currentWeight
                 preferenceManager.height = profile.height.toFloat()
                  val age = java.time.Period.between(
                        java.time.Instant.ofEpochMilli(profile.dateOfBirth).atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                        java.time.LocalDate.now()
                    ).years
                preferenceManager.age = age
                preferenceManager.isOnboardingComplete = true // User has profile, skip onboarding
             }
            user
        } else {
            null
        }
    }
}
