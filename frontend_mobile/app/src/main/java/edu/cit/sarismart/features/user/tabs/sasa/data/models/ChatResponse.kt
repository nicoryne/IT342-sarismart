package edu.cit.sarismart.features.user.tabs.sasa.data.models

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("candidates") val candidates: List<Candidate>? = null,
)