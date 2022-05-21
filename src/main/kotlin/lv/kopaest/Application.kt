package lv.kopaest

import io.ktor.server.application.*
import lv.kopaest.data.user.MongoUserDataSource
import lv.kopaest.plugins.configureMonitoring
import lv.kopaest.plugins.configureRouting
import lv.kopaest.plugins.configureSerialization
import lv.kopaest.security.hashing.SHA256HashingService
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    val databaseName = "ko-paest-db"
    val mongoPassword = System.getenv("MONGO_PW")
    val database = KMongo.createClient(
        connectionString = "mongodb+srv://artyd:$mongoPassword@clusterkopaest.1xjfu.mongodb.net/$databaseName?retryWrites=true&w=majority"
    ).coroutine.getDatabase(databaseName)
    val userDataSource = MongoUserDataSource(database)
    val hashingService = SHA256HashingService()

    configureRouting(userDataSource, hashingService)
    configureSerialization()
    configureMonitoring()
//    configureSecurity()
}
