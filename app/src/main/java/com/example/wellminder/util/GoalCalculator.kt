package com.example.wellminder.util

import kotlin.math.roundToInt

object GoalCalculator {

    enum class GoalType {
        LOSE, MAINTAIN, GAIN
    }

    // Mifflin-St Jeor Equation
    fun calculateBMR(weightKg: Float, heightCm: Int, age: Int, isMale: Boolean): Float {
        val s = if (isMale) 5 else -161
        return (10 * weightKg) + (6.25f * heightCm) - (5 * age) + s
    }

    // TDEE with default Sedentary activity multiplier (1.2)
    // We can expand this later if we add Activity Level selection
    fun calculateTDEE(bmr: Float, activityMultiplier: Float = 1.2f): Float {
        return bmr * activityMultiplier
    }

    fun calculateTargetCalories(tdee: Float, goalType: String): Int {
        val adjusted = when (goalType.uppercase()) {
            "LOSE" -> tdee - 500
            "GAIN" -> tdee + 500
            else -> tdee
        }
        // Round to nearest 50
        return (adjusted / 50).roundToInt() * 50
    }

    fun calculateWaterTarget(weightKg: Float): Int {
        val raw = weightKg * 35 // 35ml per kg
        // Round to nearest 50
        return (raw / 50).roundToInt() * 50
    }

    fun calculateStepTarget(goalType: String): Int {
        val raw = when (goalType.uppercase()) {
            "LOSE" -> 10000
            "GAIN" -> 8000
            else -> 8000
        }
        // Round to nearest 1000 (already clean, but good for safety)
        return (raw / 1000) * 1000
    }

    // Returns Triple(Proteins, Fats, Carbs) in grams
    fun calculateMacros(targetCalories: Int, goalType: String): Triple<Float, Float, Float> {
        // Ratios based on Goal
        val (pRatio, fRatio, cRatio) = when (goalType.uppercase()) {
            "LOSE" -> Triple(0.40f, 0.30f, 0.30f) // High protein for retention
            "GAIN" -> Triple(0.30f, 0.20f, 0.50f) // High carb for fuel
            else -> Triple(0.30f, 0.30f, 0.40f) // Balanced (MAINTAIN)
        }

        val pCals = targetCalories * pRatio
        val fCals = targetCalories * fRatio
        val cCals = targetCalories * cRatio

        // 1g Protein = 4kcal
        // 1g Fat = 9kcal
        // 1g Carb = 4kcal
        
        val pGrams = (pCals / 4f).roundToInt().toFloat()
        val fGrams = (fCals / 9f).roundToInt().toFloat()
        val cGrams = (cCals / 4f).roundToInt().toFloat()

        return Triple(pGrams, fGrams, cGrams)
    }
}
