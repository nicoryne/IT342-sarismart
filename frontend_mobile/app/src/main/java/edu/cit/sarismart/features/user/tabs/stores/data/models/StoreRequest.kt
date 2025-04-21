package edu.cit.sarismart.features.user.tabs.stores.data.models

data class StoreRequest (
    val storeName: String,
    val location: String,
    val latitude: Number,
    val longitude: Number,
    val ownerId: String
)