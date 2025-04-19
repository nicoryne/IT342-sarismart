package edu.cit.sarismart.features.user.tabs.sasa.data.models

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("parts") val parts: List<Part>
)