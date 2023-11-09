package com.heisy

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.junit.After
import org.junit.Before

abstract class TestServer {
    protected abstract val server: ApplicationEngine

    @Before
    fun startServer() {
        server.start(wait = false)
    }

    @After
    fun stopServer() {
        server.stop(100, 100)
    }
}