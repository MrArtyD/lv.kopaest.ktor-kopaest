package lv.kopaest.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import lv.kopaest.data.recipe.Recipe
import lv.kopaest.data.recipe.RecipeDataSource
import lv.kopaest.data.requests.AddRecipeRequest
import lv.kopaest.data.requests.DeleteRecipeRequest
import lv.kopaest.data.responses.SimpleResponse

fun Route.recipeRoutes(recipeDataSource: RecipeDataSource) {
    route("/saveRecipe") {
        post {
            val request = try {
                call.receive<AddRecipeRequest>()
            } catch (e: java.lang.Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Something wrong with the request body"))
                return@post
            }

            val isRecipeExists = recipeDataSource.checkIfRecipeExists(request.remoteId)
            if (isRecipeExists
                && !recipeDataSource.checkIfUserHasRecipe(request.userId, request.remoteId)
                && recipeDataSource.addUserToRecipe(request.userId, request.remoteId)
            ) {
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Recipe was successfully saved for the user"))
            } else if (!isRecipeExists
                && recipeDataSource.saveRecipe(
                    Recipe(
                        remoteId = request.remoteId,
                        title = request.title,
                        imageUrl = request.imageUrl,
                        setOf(request.userId)
                    )
                )
            ) {
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Recipe was successfully saved for the user"))
            } else {
                call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(false, "Something went wrong or user already has recipe")
                )
            }
        }
    }
    route("/{id}/getRecipes") {
        get {
            val userId = call.parameters["id"]!!
            call.respond(HttpStatusCode.OK, recipeDataSource.getAllRecipesForUser(userId))
        }
    }
    route("/deleteRecipe") {
        post {
            val request = try {
                call.receive<DeleteRecipeRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Something wrong with the request body"))
                return@post
            }
            if (recipeDataSource.deleteRecipeForUser(request.userId, request.recipeId)) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Conflict)
            }
        }
    }
}
