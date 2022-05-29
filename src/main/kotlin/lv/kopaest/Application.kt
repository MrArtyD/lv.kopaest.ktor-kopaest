package lv.kopaest

import io.ktor.server.application.*
import lv.kopaest.data.recipe.RecipeDataSource
import lv.kopaest.data.user.UserDataSource
import lv.kopaest.plugins.*
import lv.kopaest.security.hashing.HashingService
import org.koin.ktor.ext.inject

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureKoin()

    val userDataSource by inject<UserDataSource>()
    val recipeDataSource by inject<RecipeDataSource>()
    val hashingService by inject<HashingService>()

    configureRouting(userDataSource, recipeDataSource, hashingService)
    configureSerialization()
    configureMonitoring()
    configureCors()
//    configureSecurity()
}
