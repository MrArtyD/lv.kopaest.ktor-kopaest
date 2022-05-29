package lv.kopaest.plugins

import io.ktor.server.application.*
import lv.kopaest.di.appModule
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
