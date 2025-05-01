package edu.cit.sarismart.features.user.tabs.maps.domain

import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import retrofit2.http.GET
import retrofit2.http.Path

interface StoreMapApiService {

    @GET("/api/v1/stores/nearby/{latitude}/{longitude}/{radius}")
    suspend fun getNearbyStores(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double,
        @Path("radius") radius: Double
    ): List<Store>
}