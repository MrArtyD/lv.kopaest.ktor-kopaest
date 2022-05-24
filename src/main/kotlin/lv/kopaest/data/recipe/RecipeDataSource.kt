package lv.kopaest.data.recipe

interface RecipeDataSource {
    suspend fun saveRecipe(recipe: Recipe): Boolean
    suspend fun checkIfRecipeExists(recipeId: Int): Boolean
    suspend fun addUserToRecipe(userId: String, recipeId: Int): Boolean
    suspend fun getAllRecipesForUser(userId: String): List<Recipe>
    suspend fun checkIfUserHasRecipe(userId: String, recipeId: Int): Boolean

    suspend fun deleteRecipeForUser(userId: String, recipeId: Int): Boolean
}
