package edu.cit.sarismart.features.user.tabs.maps.data

import edu.cit.sarismart.features.user.tabs.stores.data.models.Store

interface MapRepository {

    suspend fun getNearbyStores(latitude: Double, longitude: Double, radius: Double): List<Store>

}