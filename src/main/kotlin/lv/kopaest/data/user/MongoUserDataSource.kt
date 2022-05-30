package lv.kopaest.data.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class MongoUserDataSource(database: CoroutineDatabase) : UserDataSource {

    private val users = database.getCollection<User>()

    override suspend fun getUserById(id: String) = users.findOneById(id)

    override suspend fun getUserByEmail(email: String) = users.findOne(User::email eq email)

    override suspend fun registerUser(user: User) = users.insertOne(user).wasAcknowledged()

    override suspend fun updatePassword(userId: String, newPassword: String, newSalt: String) =
        users.updateOneById(userId, setValue(User::password, newPassword)).wasAcknowledged()
                && users.updateOneById(userId, setValue(User::salt, newSalt)).wasAcknowledged()
}
