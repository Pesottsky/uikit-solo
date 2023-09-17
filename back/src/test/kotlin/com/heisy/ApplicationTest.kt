package com.heisy

import com.heisy.schema.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.junit.Before
import kotlin.test.*

class ApplicationTest : TestServer() {

    override val server: ApplicationEngine
        get() = embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::moduleTest)
    private lateinit var token: String



    @BeforeTest
    fun login() = runBlocking {

        val response = HttpClient() {
            install(ContentNegotiation) {
                json()
            }
            headers {
                append(HttpHeaders.Accept, ContentType.Application.Json)
            }
        }.post("http://localhost:8081/login") {
            setBody(User("user", "1234"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        token = response.body<Token>().token.access.toString()
    }

    @BeforeTest
    fun incorrectLogin() = runBlocking {

        val response = client.post("/login") {
            setBody(User("huesos", "1234"))
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun rootRouteRespondsWithHelloWorldString(): Unit = runBlocking {
        val response: String = HttpClient().get("http://localhost:8081/").body()
        assertEquals("Hello, world!", response)
    }

    @Test
    fun getTables() = runBlocking {

        val response = client.get("/table") {
            headers.appendIfNameAbsent("Authorization", "Bearer $token")
        }

        println(response.request.headers)
        println(response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)

    }

    @AfterTest
    fun printToken() {
        println("TOKEN $token")
    }
}

@Serializable
data class Token(

    @SerialName("token")
    val token: com.heisy.schema.Token
)
