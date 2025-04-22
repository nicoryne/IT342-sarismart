package edu.cit.sarismart.features.user.tabs.stores.data.models

import edu.cit.sarismart.features.user.tabs.account.data.models.User

data class Store (
    val id: Long,
    val storeName: String,
    val location: String,
    val latitude: Number,
    val longitude: Number,
    val owner: User,
    val workers: List<User>,
    val sales: List<Sale>
)