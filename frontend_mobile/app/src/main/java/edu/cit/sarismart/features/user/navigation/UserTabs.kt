package edu.cit.sarismart.features.user.navigation

import edu.cit.sarismart.R

enum class UserTabs(
    val route: String,
    val title: String,
    val iconResId: Int
) {
    MAPS("maps", "Maps", R.drawable.ic_tab_maps),
    SASA("sasa", "Sasa AI", R.drawable.ic_tab_sasa),
    SCAN("scan", "Scan", R.drawable.ic_tab_camera),
    STORE("store", "My Store", R.drawable.ic_tab_store),
    ACCOUNT("account", "Account", R.drawable.ic_tab_profile)
}

