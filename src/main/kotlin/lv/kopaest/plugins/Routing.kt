package lv.kopaest.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import lv.kopaest.data.recipe.RecipeDataSource
import lv.kopaest.data.user.UserDataSource
import lv.kopaest.routes.*
import lv.kopaest.security.hashing.HashingService

fun Application.configureRouting(
    userDataSource: UserDataSource,
    recipeDataSource: RecipeDataSource,
    hashingService: HashingService
) {

    routing {
        signUp(userDataSource, hashingService)
        signIn(userDataSource, hashingService)
        recipeRoutes(recipeDataSource)
        changePassword(userDataSource, hashingService)
        sendNewPassword(userDataSource, hashingService)
    }
}
