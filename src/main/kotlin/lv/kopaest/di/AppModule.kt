package lv.kopaest.di

import lv.kopaest.data.recipe.MongoRecipeDataSource
import lv.kopaest.data.recipe.RecipeDataSource
import lv.kopaest.data.user.MongoUserDataSource
import lv.kopaest.data.user.UserDataSource
import lv.kopaest.security.hashing.HashingService
import lv.kopaest.security.hashing.SHA256HashingService
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val appModule = module {
    single {
        val databaseName = "ko-paest-db"
        val mongoPassword = System.getenv("MONGO_PW")
        KMongo.createClient(
            connectionString = "mongodb+srv://artyd:$mongoPassword@clusterkopaest.1xjfu.mongodb.net/$databaseName?retryWrites=true&w=majority"
        ).coroutine.getDatabase(databaseName)
    }
    single<UserDataSource> {
        MongoUserDataSource(get())
    }
    single<RecipeDataSource> {
        MongoRecipeDataSource(get())
    }
    single<HashingService> {
        SHA256HashingService()
    }
}
