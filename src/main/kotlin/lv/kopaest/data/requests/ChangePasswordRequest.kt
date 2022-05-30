package lv.kopaest.data.requests

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ChangePasswordRequest(
    @SerialName("old_password")
    val oldPassword: String,
    @SerialName("new_password")
    val newPassword: String
)
