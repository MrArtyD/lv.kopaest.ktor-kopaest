package lv.kopaest.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import lv.kopaest.data.requests.ChangePasswordRequest
import lv.kopaest.data.requests.NewPasswordRequest
import lv.kopaest.data.responses.SimpleResponse
import lv.kopaest.data.user.UserDataSource
import lv.kopaest.security.hashing.HashingService
import lv.kopaest.security.hashing.SaltedHash
import lv.kopaest.utils.getRandomString
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

fun Route.changePassword(userDataSource: UserDataSource, hashingService: HashingService) {
    post("/{id}/changePassword") {
        val userId = call.parameters["id"]!!
        val request = try {
            call.receiveOrNull<ChangePasswordRequest>() ?: kotlin.run {
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

        val user = userDataSource.getUserById(userId)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "User does not exists"))
            return@post
        }

        val isValidPassword = hashingService.verify(request.oldPassword, SaltedHash(user.password, user.salt))
        if (!isValidPassword) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Incorrect old password"))
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.newPassword)
        if (userDataSource.updatePassword(userId, saltedHash.hash, saltedHash.salt)) {
            call.respond(HttpStatusCode.OK, SimpleResponse(true, "Password has been changed successfully"))
        } else {
            call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "An unknown error occurred"))
        }
    }
}

fun Route.sendNewPassword(userDataSource: UserDataSource, hashingService: HashingService) {
    post("sendNewPassword") {
        val request = try {
            call.receiveOrNull<NewPasswordRequest>() ?: kotlin.run {
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
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "User does not exists"))
            return@post
        }
        val newPassword = getRandomString(8)
        val saltedHash = hashingService.generateSaltedHash(newPassword)
        if (userDataSource.updatePassword(user.id, saltedHash.hash, saltedHash.salt)) {
            try {
                val username = "makonskproject@gmail.com"
                val password = System.getenv("EMAIL_PASSWORD")

                val prop = Properties()
                prop["mail.smtp.ssl.trust"] = "smtp.gmail.com"
                prop["mail.smtp.user"] = username;
                prop["mail.smtp.password"] = password;
                prop["mail.smtp.port"] = "587"
                prop["mail.smtp.auth"] = "true"
                prop["mail.smtp.starttls.enable"] = "true"

                val session = Session.getDefaultInstance(prop)
                val message: Message = MimeMessage(session)
                message.setFrom(InternetAddress(username))
                message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("wcytulorvenwhbetau@bvhrk.com")
                )
                message.subject = "New password"
                message.setText("Your new password is - $newPassword")

                val transport = session.getTransport("smtp")
                transport.connect("smtp.gmail.com", username, password)
                transport.sendMessage(message, message.allRecipients)
                transport.close()

                call.respond(HttpStatusCode.OK, SimpleResponse(true, "New password has been sent to your email"))
            } catch (e: Exception) {
                call.application.environment.log.info("Error while sending an email - $e")
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "An unknown error while sending an email"))
            }
        } else {
            call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "An unknown error occurred"))
        }
    }
}
