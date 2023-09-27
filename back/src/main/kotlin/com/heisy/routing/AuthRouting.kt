package com.heisy.routing

import com.heisy.domain.usecase.IAuthUseCase
import com.heisy.schema.Token
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAuthRouting(authUseCase: IAuthUseCase) {
    routing {
        route("/login") {
            post {
                val token = authUseCase.login(call.receive())
                call.respond(HttpStatusCode.Created, token)
            }
        }

        post("/refresh") {
            call.respond(
                HttpStatusCode.Created,
                authUseCase.refresh(call.receive<Token>().refresh)
            )
        }

        route("/registration") {
            post {
                val token = authUseCase.registerCompany(call.receive())
                call.respond(HttpStatusCode.Created, token)
            }

            route("/freel") {
                post {
                    val link = call.request.queryParameters["link"]
                    val token: Token = if (link != null) {
                        authUseCase.registerFreelByLink(link, call.receive())
                    } else {
                        authUseCase.registerFreel(call.receive())
                    }
                    call.respond(HttpStatusCode.Created, token)
                }
            }
        }
    }
}