package lv.kopaest.data.responses

@kotlinx.serialization.Serializable
data class LoginResponse(
    val id: String,
    val name: String,
    val surname: String,
    val email: String
)
