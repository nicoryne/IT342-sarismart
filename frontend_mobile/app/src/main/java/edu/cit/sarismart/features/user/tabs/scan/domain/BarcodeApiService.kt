package edu.cit.sarismart.features.user.tabs.scan.domain


import edu.cit.sarismart.features.user.tabs.scan.data.models.OpenFoodFactsResponse
import edu.cit.sarismart.features.user.tabs.scan.data.models.UpcDatabaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BarcodeApiService {

    @GET("api/v2/product/{barcode}")
    suspend fun getOpenFoodFactsProduct(@Path("barcode") barcode: String): Response<OpenFoodFactsResponse>


    @GET("prod/trial/lookup")
    suspend fun getUpcDatabaseProduct(
        @Query("upc") upc: String,
    ): Response<UpcDatabaseResponse>
}

