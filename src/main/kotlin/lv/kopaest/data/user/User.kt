package lv.kopaest.data.user

import lv.kopaest.data.responses.LoginResponse
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val salt: String,
    @BsonId val id: String = ObjectId().toString()
) {
    fun toLoginResponse() = LoginResponse(id, name, surname, email)
}
