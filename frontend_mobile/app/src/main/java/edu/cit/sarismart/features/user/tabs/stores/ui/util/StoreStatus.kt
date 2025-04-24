package edu.cit.sarismart.features.user.tabs.stores.ui.util

import androidx.compose.ui.graphics.Color
import edu.cit.sarismart.R

enum class StoreStatus(
    val label: String,
    val color: Color,
    val status: Int,
    val icon: Int
) {
    GOOD("Stocks are Good!", Color(0xFF016171), 0, R.drawable.smiley),
    LOW_STOCK("Stocks are Low!", Color(0xFFFFD632), 1, R.drawable.face_very_sad),
    OUT_OF_STOCK("No More Stocks!", Color(0xFFC07777), 2, R.drawable.face_dizzy)
}