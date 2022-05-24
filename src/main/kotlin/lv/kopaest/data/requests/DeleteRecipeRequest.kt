package lv.kopaest.data.requests

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DeleteRecipeRequest(
    @SerialName("recipe_id")
    val recipeId: Int,
    @SerialName("user_id")
    val userId: String
)
