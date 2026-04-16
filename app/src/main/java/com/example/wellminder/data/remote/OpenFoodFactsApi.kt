package com.example.wellminder.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Header

interface OpenFoodFactsApi {
    /**
     * Get product by barcode.
     * Open Food Facts requires a unique User-Agent header to identify the app.
     */
    @GET("api/v2/product/{barcode}.json")
    suspend fun getProduct(
        @Path("barcode") barcode: String,
        @Header("User-Agent") userAgent: String = "WellMinder - Android - Version 1.0"
    ): OpenFoodFactsResponse
}
