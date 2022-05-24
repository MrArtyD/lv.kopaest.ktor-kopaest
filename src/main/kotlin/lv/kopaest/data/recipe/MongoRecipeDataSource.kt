package lv.kopaest.data.recipe

import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class MongoRecipeDataSource(database: CoroutineDatabase) : RecipeDataSource {

    private val recipes = database.getCollection<Recipe>()

    override suspend fun checkIfRecipeExists(recipeId: Int) = recipes.findOne(Recipe::remoteId eq recipeId) != null

    override suspend fun saveRecipe(recipe: Recipe) = recipes.insertOne(recipe).wasAcknowledged()

    override suspend fun addUserToRecipe(userId: String, recipeId: Int): Boolean {
        val users = recipes.findOne(Recipe::remoteId eq recipeId)?.users ?: return false
        return recipes.updateOne(Recipe::remoteId eq recipeId, setValue(Recipe::users, users + userId))
            .wasAcknowledged()
    }

    override suspend fun getAllRecipesForUser(userId: String) = recipes.find(Recipe::users contains userId).toList()

    override suspend fun checkIfUserHasRecipe(userId: String, recipeId: Int): Boolean {
        val recipe = recipes.findOne(Recipe::remoteId eq recipeId) ?: return false
        return userId in recipe.users
    }

    override suspend fun deleteRecipeForUser(userId: String, recipeId: Int): Boolean {
        val recipe = recipes.findOne(Recipe::remoteId eq recipeId, Recipe::users contains userId) ?: return false
        if (recipe.users.size > 1) {
            val leftUsers = recipe.users - userId
            return recipes.updateOne(Recipe::id eq recipe.id, setValue(Recipe::users, leftUsers)).wasAcknowledged()
        }
        return recipes.deleteOneById(recipe.id).wasAcknowledged()
    }
}
