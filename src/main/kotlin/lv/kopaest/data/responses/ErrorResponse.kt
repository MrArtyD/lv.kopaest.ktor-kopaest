package lv.kopaest.data.responses

@kotlinx.serialization.Serializable
data class ErrorResponse(
    val error: String
)
