package edu.cit.sarismart.features.user.tabs.maps.data

import edu.cit.sarismart.features.user.tabs.maps.domain.StoreMapApiService
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(private val storeMapApiService: StoreMapApiService): MapRepository {

    override suspend fun getNearbyStores(
        latitude: Double,
        longitude: Double,
        radius: Double
    ): List<Store> {
        return storeMapApiService.getNearbyStores(latitude, longitude, radius)
    }


}