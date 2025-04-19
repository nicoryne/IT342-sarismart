package edu.cit.sarismart.features.user.tabs.sasa.data.models

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("contents") val contents: List<Content>
)
