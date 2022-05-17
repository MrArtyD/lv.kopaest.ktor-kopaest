package lv.kopaest.data.user

interface UserDataSource {
    suspend fun getUserByEmail(email: String): User?
    suspend fun registerUser(user: User): Boolean
}
