package lv.kopaest.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import lv.kopaest.data.requests.LoginRequest
import lv.kopaest.data.requests.RegistrationRequest
import lv.kopaest.data.responses.SimpleResponse
import lv.kopaest.data.user.User
import lv.kopaest.data.user.UserDataSource
import lv.kopaest.security.hashing.HashingService
import lv.kopaest.security.hashing.SaltedHash

fun Route.signUp(userDataSource: UserDataSource, hashingService: HashingService) {
    post("signup") {
        val request = try {
            call.receiveOrNull<RegistrationRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Something is wrong with the request"))
                return@post
            }
        } catch (exception: SerializationException) {
            call.respond(
                HttpStatusCode.BadRequest,
                SimpleResponse(false, "Request body does not contain all required fields")
            )
            return@post
        }

        if (userDataSource.getUserByEmail(request.email) != null) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "User with this email already exists"))
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            name = request.name,
            surname = request.surname,
            email = request.email,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        if (userDataSource.registerUser(user)) {
            call.respond(HttpStatusCode.OK, SimpleResponse(true, "User has been registered successfully!"))
        } else {
            call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "An unknown error occurred"))
        }
    }
}

fun Route.signIn(userDataSource: UserDataSource, hashingService: HashingService) {
    post("signin") {
        val request = try {
            call.receiveOrNull<LoginRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Something is wrong with the request"))
                return@post
            }
        } catch (exception: SerializationException) {
            call.respond(
                HttpStatusCode.BadRequest,
                SimpleResponse(false, "Request body fields error")
            )
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Incorrect username or password"))
            return@post
        }

        val isValidPassword = hashingService.verify(request.password, SaltedHash(user.password, user.salt))
        if (!isValidPassword) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Incorrect username or password"))
            return@post
        }

        call.respond(HttpStatusCode.OK, user.toLoginResponse())
    }
}
