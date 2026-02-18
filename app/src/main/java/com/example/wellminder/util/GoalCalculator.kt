package com.example.wellminder.util

import kotlin.math.roundToInt

object GoalCalculator {

    enum class GoalType {
        LOSE, MAINTAIN, GAIN
    }

    // Формула Міффліна-Сент-Жеора
    fun calculateBMR(weightKg: Float, heightCm: Int, age: Int, isMale: Boolean): Float {
        val s = if (isMale) 5 else -161
        return (10 * weightKg) + (6.25f * heightCm) - (5 * age) + s
    }

    // TDEE з коефіцієнтом активності за замовчуванням (1.2 - сидячий спосіб життя)
    // Можна розширити пізніше, якщо додамо вибір рівня активності
    fun calculateTDEE(bmr: Float, activityMultiplier: Float = 1.2f): Float {
        return bmr * activityMultiplier
    }

    fun calculateTargetCalories(tdee: Float, goalType: String): Int {
        val adjusted = when (goalType.uppercase()) {
            "LOSE" -> tdee - 500
            "GAIN" -> tdee + 500
            else -> tdee
        }
        // Округлити до найближчих 100
        return (adjusted / 100).roundToInt() * 100
    }

    fun calculateWaterTarget(weightKg: Float): Int {
        val raw = weightKg * 35 // 35 мл на кг
        // Округлити до найближчих 100
        return (raw / 100).roundToInt() * 100
    }

    fun calculateStepTarget(goalType: String): Int {
        val raw = when (goalType.uppercase()) {
            "LOSE" -> 10000
            "GAIN" -> 8000
            else -> 8000
        }
        // Округлити до найближчих 1000
        return (raw / 1000) * 1000
    }

    // Повертає Triple(Білки, Жири, Вуглеводи) в грамах на основі ваги
    fun calculateMacros(weightKg: Float, goalType: String): Triple<Float, Float, Float> {
        // Коефіцієнти (г на кг ваги)
        val (pCoeff, fCoeff, cCoeff) = when (goalType.uppercase()) {
            "LOSE" -> Triple(2.0f, 0.8f, 2.0f)     // Більше білка, менше вуглеводів/жирів
            "GAIN" -> Triple(1.8f, 1.2f, 5.0f)     // Помірний білок, більше калорій/вуглеводів
            else -> Triple(1.5f, 1.0f, 3.0f)       // Стандарт (ПІДТРИМКА)
        }

        val pGrams = (weightKg * pCoeff).roundToInt().toFloat()
        val fGrams = (weightKg * fCoeff).roundToInt().toFloat()
        val cGrams = (weightKg * cCoeff).roundToInt().toFloat()

        return Triple(pGrams, fGrams, cGrams)
    }

    fun calculateCaloriesFromMacros(p: Float, f: Float, c: Float): Int {
        return (p * 4 + f * 9 + c * 4).roundToInt()
    }
}
