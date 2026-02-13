package com.example.wellminder.data.local.dao

import androidx.room.*
import com.example.wellminder.data.local.entities.UserEntity
import com.example.wellminder.data.local.entities.UserGoalEntity
import com.example.wellminder.data.local.entities.UserProfileEntity
import com.example.wellminder.data.local.entities.WeightLogEntity

@Dao
interface UserDao {
    // Basic Inserts
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: UserGoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(log: WeightLogEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateGoals(goals: UserGoalEntity)

    // Queries
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT userId FROM users")
    suspend fun getAllUserIds(): List<Long>

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getUserProfile(userId: Long): UserProfileEntity?

    @Query("SELECT * FROM user_profiles ORDER BY profileId DESC LIMIT 1")
    suspend fun getLastUserProfile(): UserProfileEntity?

    @Query("SELECT * FROM user_goals WHERE userId = :userId LIMIT 1")
    suspend fun getUserGoals(userId: Long): UserGoalEntity?

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun getUserProfileFlow(userId: Long): kotlinx.coroutines.flow.Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_goals WHERE userId = :userId LIMIT 1")
    fun getUserGoalsFlow(userId: Long): kotlinx.coroutines.flow.Flow<UserGoalEntity?>

    @Query("DELETE FROM users WHERE userId = :id")
    suspend fun deleteUserById(id: Long)

    // Compound Transaction for Registration
    @Transaction
    suspend fun registerUser(user: UserEntity, profile: UserProfileEntity, goals: UserGoalEntity): Long {
        val userId = insertUser(user)
        // Must copy userId to other entities as it's auto-generated
        insertProfile(profile.copy(userId = userId))
        insertGoals(goals.copy(userId = userId))
        // Optional: Log initial weight
        insertWeightLog(
            WeightLogEntity(
                userId = userId,
                date = System.currentTimeMillis(),
                weightValue = profile.currentWeight
            )
        )
        return userId
    }

    @Query("SELECT * FROM weight_logs WHERE userId = :userId AND date >= :startDate ORDER BY date ASC")
    suspend fun getWeightLogs(userId: Long, startDate: Long): List<WeightLogEntity>

    @Query("SELECT * FROM weight_logs WHERE userId = :userId AND date < :date ORDER BY date DESC LIMIT 1")
    suspend fun getLastWeightLogBefore(userId: Long, date: Long): WeightLogEntity?
}
