package com.example.wellminder.data.remote

import com.google.gson.annotations.SerializedName

data class OpenFoodFactsResponse(
    val product: ProductData?,
    val status: Int,
    @SerializedName("status_verbose") val statusVerbose: String?
)

data class ProductData(
    @SerializedName("product_name") val productName: String?,
    val nutriments: NutrimentsData?,
    @SerializedName("image_url") val imageUrl: String?
)

data class NutrimentsData(
    @SerializedName("proteins_100g") val proteins100g: Any?,
    @SerializedName("fat_100g") val fat100g: Any?,
    @SerializedName("carbohydrates_100g") val carbohydrates100g: Any?,
    @SerializedName("energy-kcal_100g") val energyKcal100g: Any?,
    @SerializedName("energy-kcal") val energyKcal: Any? // fallback
)
