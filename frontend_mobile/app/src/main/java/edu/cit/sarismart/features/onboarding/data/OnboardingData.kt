package edu.cit.sarismart.features.onboarding.data

import androidx.annotation.DrawableRes

data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    val titleLine1: String,
    val titleLine2: String,
    val description: String
)
