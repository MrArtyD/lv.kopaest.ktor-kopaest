package lv.kopaest.data.requests

@kotlinx.serialization.Serializable
data class NewPasswordRequest(
    val email: String
)
