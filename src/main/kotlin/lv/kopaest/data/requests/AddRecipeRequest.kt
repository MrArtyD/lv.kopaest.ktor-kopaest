package lv.kopaest.data.requests

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AddRecipeRequest(
    @SerialName("remote_id")
    val remoteId: Int,
    val title: String,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("user_id")
    val userId: String
)
