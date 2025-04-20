package edu.cit.sarismart.features.user.tabs.stores.data.models

data class Store (
    val id: Long,
    val storeName: String,
    val location: String,
    val latitude: Number,
    val longitude: Number,
)