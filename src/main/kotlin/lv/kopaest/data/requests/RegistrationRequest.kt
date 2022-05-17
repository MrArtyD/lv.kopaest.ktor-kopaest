package lv.kopaest.data.requests

@kotlinx.serialization.Serializable
data class RegistrationRequest(
    val name: String,
    val surname: String,
    val email: String,
    val password: String
)
