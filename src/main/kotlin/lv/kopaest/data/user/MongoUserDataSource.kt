package lv.kopaest.data.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserDataSource(database: CoroutineDatabase) : UserDataSource {

    private val users = database.getCollection<User>()

    override suspend fun getUserByEmail(email: String) = users.findOne(User::email eq email)

    override suspend fun registerUser(user: User) = users.insertOne(user).wasAcknowledged()
}
