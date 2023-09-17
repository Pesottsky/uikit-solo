package com.heisy

import com.heisy.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 81, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    setup()
    configureDatabases()
}

fun Application.setup() {
    configureSerialization()
    configureHTTP()
    configureSecurity()
    errorsHandling()
}

fun Application.moduleTest() {
    setup()
    configureTestDatabases()
}

