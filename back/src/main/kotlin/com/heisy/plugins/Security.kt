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


object TokensConfigStorage {
    var accessLifetime = 0
    var domain = ""
    var companyAudience = ""
    var freelAudience = ""
    var companySecret = ""
    var freelSecret = ""
}

fun Application.configureSecurity() {
    val jwtCompanyAudience = environment.config.property("jwt.company_audience").getString()
    val jwtFreelAudience = environment.config.property("jwt.freel_audience").getString()
    val jwtCompanySecret = environment.config.property("jwt.company_secret").getString()
    val jwtFreelSecret = environment.config.property("jwt.freel_secret").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val allowHost = environment.config.property("jwt.allow_host").getString()

    val accessLifeTime = environment.config.property("jwt.access_lifetime").getString().toInt()

    val domain = "${
        environment.config.property("ktor.deployment.host").getString()
    }:${environment.config.property("ktor.deployment.port").getString()}"

    TokensConfigStorage.apply {
        this.accessLifetime = accessLifeTime
        this.domain = domain
        this.companyAudience = jwtCompanyAudience
        this.freelAudience = jwtFreelAudience
        this.companySecret = jwtCompanySecret
        this.freelSecret = jwtFreelSecret
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Put)
        allowHost(allowHost)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.Origin)
        allowHeader(HttpHeaders.Referrer)
        allowHeader(HttpHeaders.UserAgent)
    }

    authentication {
        jwt(UserTypes.Company.name) {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtCompanySecret))
                    .withAudience(jwtCompanyAudience)
                    .withIssuer(domain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtCompanyAudience)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }

        jwt(UserTypes.Freel.name) {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtFreelSecret))
                    .withAudience(jwtFreelAudience)
                    .withIssuer(domain)
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

fun createCompanyToken(userId: Int): String {
    return JWT.create()
        .withAudience(TokensConfigStorage.companyAudience)
        .withIssuer(TokensConfigStorage.domain)
        .withClaim("id", userId)
        .withClaim("user_type", UserTypes.Company.name)
        .withExpiresAt(Date(System.currentTimeMillis() + TokensConfigStorage.accessLifetime))
        .sign(Algorithm.HMAC256(TokensConfigStorage.companySecret))
}

fun createFreelToken(userId: Int): String {
    return JWT.create()
        .withAudience(TokensConfigStorage.freelAudience)
        .withIssuer(TokensConfigStorage.domain)
        .withClaim("id", userId)
        .withClaim("user_type", UserTypes.Freel.name)
        .withExpiresAt(Date(System.currentTimeMillis() + TokensConfigStorage.accessLifetime))
        .sign(Algorithm.HMAC256(TokensConfigStorage.freelSecret))
}

fun getId(call: ApplicationCall): Int {
    val principal = call.principal<JWTPrincipal>()
    return principal!!.payload.getClaim("id").asInt()
}

fun getType(call: ApplicationCall): String {
    val principal = call.principal<JWTPrincipal>()
    return principal!!.payload.getClaim("user_type").asString()
}

fun getIdTypePair(call: ApplicationCall): Pair<Int, String> {
    val principal = call.principal<JWTPrincipal>()
    val id = principal!!.payload.getClaim("id").asInt()
    val type = principal.payload.getClaim("user_type").asString()
    return Pair(id, type)
}

enum class UserTypes {
    Freel,
    Company
}