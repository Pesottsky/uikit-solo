package com.heisy

import com.heisy.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

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

