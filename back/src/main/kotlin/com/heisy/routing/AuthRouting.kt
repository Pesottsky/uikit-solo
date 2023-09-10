package com.heisy.routing

import com.heisy.domain.usecase.IAuthUseCase
import com.heisy.plugins.*
import com.heisy.schema.Token
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAuthRouting(authUseCase: IAuthUseCase) {
    routing {
        route("/login") {
            post {
                val token = authUseCase.loginCompany(call.receive())
                call.respond(hashMapOf("token" to token))
            }

            route("/freel") {
                post {
                    val token = authUseCase.loginFreel(call.receive())
                    call.respond(hashMapOf("token" to token))
                }
            }
        }

        post("/refresh") {
            call.respond(
                HttpStatusCode.OK,
                authUseCase.refresh(call.receive<Token>().refresh)
            )
        }

        post("/register") {
            val token = authUseCase.registerFreel(call.receive())
            call.respond(hashMapOf("token" to token))
        }

        post("/register_with_link") {
            val link = call.request.queryParameters["link"]
            if (link != null) {
                val token = authUseCase.registerFreelByLink(link, call.receive())
                call.respond(hashMapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

    }
}