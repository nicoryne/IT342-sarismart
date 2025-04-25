package edu.cit.sarismart.features.user.tabs.scan.data.models

import edu.cit.sarismart.features.user.tabs.scan.data.models.UpcDatabaseProduct

data class UpcDatabaseResponse(
    val success: Boolean,
    val product: UpcDatabaseProduct?
)