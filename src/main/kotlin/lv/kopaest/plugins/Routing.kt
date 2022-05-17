package lv.kopaest.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import lv.kopaest.data.user.UserDataSource
import lv.kopaest.routes.signIn
import lv.kopaest.routes.signUp
import lv.kopaest.security.hashing.HashingService

fun Application.configureRouting(userDataSource: UserDataSource, hashingService: HashingService) {

    routing {
        signUp(userDataSource, hashingService)
        signIn(userDataSource, hashingService)
    }
}
