package com.heisy.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import java.util.*

private const val jwtCompanyAudience = "company-audience"
private const val jwtFreelAudience = "freel-audience"
private const val generalAudience = "general-audience"
private const val jwtDomain = "http://0.0.0.0:8080/"
private const val jwtRealm = "ktor sample app"
private const val jwtCompanySecret = "secret 1"
private const val jwtFreelSecret = "secret 2"

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowHost("localhost:5173")
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.Origin)
        allowHeader(HttpHeaders.Referrer)
        allowHeader(HttpHeaders.UserAgent)
    }

    authentication {
        jwt("company") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtCompanySecret))
                    .withAudience(jwtCompanyAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtCompanyAudience)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }

        jwt("freel") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtFreelSecret))
                    .withAudience(jwtFreelAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtFreelAudience)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

}

fun createCompanyToken(userId: Int, userType: String): String = run {
    JWT.create()
        .withAudience(jwtCompanyAudience)
        .withIssuer(jwtDomain)
        .withClaim("id", userId)
        .withClaim("user_type", userType)
        .withExpiresAt(Date(System.currentTimeMillis() + 3_000_0))
        .sign(Algorithm.HMAC256(jwtCompanySecret))
}

fun createFreelToken(userId: Int, userType: String): String = run {
    JWT.create()
        .withAudience(jwtFreelAudience)
        .withIssuer(jwtDomain)
        .withClaim("id", userId)
        .withClaim("user_type", userType)
        .withExpiresAt(Date(System.currentTimeMillis() + 3_000_0))
        .sign(Algorithm.HMAC256(jwtFreelSecret))
}

fun getId(call: ApplicationCall): Int {
    val principal = call.principal<JWTPrincipal>()
    return principal!!.payload.getClaim("id").asInt()
}

fun getType(call: ApplicationCall): String {
    val principal = call.principal<JWTPrincipal>()
    return principal!!.payload.getClaim("user_type").asString()
}

enum class UserTypes(val type: String) {
    Freel("freel"),
    User("user")
}
