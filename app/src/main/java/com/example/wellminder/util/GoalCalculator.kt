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

    // Повертає Triple(Білки, Жири, Вуглеводи) в грамах на основі калорій
    fun calculateMacros(targetCalories: Int, goalType: String): Triple<Float, Float, Float> {
        // Відсоткові співвідношення макронутрієнтів
        val (pPercent, fPercent, cPercent) = when (goalType.uppercase()) {
            "LOSE" -> Triple(0.40f, 0.30f, 0.30f)     // Більше білка для збереження м'язів, менше вуглеводів
            "GAIN" -> Triple(0.25f, 0.25f, 0.50f)     // Помірний білок, більше вуглеводів для енергії
            else -> Triple(0.30f, 0.30f, 0.40f)       // Стандартна пропорція для підтримки форми
        }

        val pGrams = (targetCalories * pPercent) / 4f
        val fGrams = (targetCalories * fPercent) / 9f
        val cGrams = (targetCalories * cPercent) / 4f

        return Triple(pGrams.roundToInt().toFloat(), fGrams.roundToInt().toFloat(), cGrams.roundToInt().toFloat())
    }

    fun calculateCaloriesFromMacros(p: Float, f: Float, c: Float): Int {
        return (p * 4 + f * 9 + c * 4).roundToInt()
    }
}
