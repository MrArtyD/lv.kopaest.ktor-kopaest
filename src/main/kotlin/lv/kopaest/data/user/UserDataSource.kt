package lv.kopaest.data.user

interface UserDataSource {
    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun registerUser(user: User): Boolean
    suspend fun updatePassword(userId: String, newPassword: String, newSalt: String): Boolean
}
