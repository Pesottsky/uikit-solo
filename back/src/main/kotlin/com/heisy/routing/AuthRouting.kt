package com.heisy.routing

import com.heisy.domain.usecase.IAuthUseCase
import com.heisy.plugins.UserTypes
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
                val token = authUseCase.login(call.receive())
                call.respond(HttpStatusCode.Created, token)
            }
        }

        post("/refresh") {
            val principal = call.principal<JWTPrincipal>()
            val id =  principal!!.payload.getClaim("id").asInt()
            val type = principal.payload.getClaim("user_type").asString()
            call.respond(
                HttpStatusCode.Created,
                authUseCase.refresh(call.receive<Token>().refresh, id ,type)
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

//        route("update_password") {
//            post {
//                val tokens = authUseCase.updatePassword(call.receive())
//                call.respond(HttpStatusCode.Accepted, tokens)
//            }
//        }
        authenticate(UserTypes.Company.name, UserTypes.Freel.name) {
            route("/logout") {
                delete {
                    val principal = call.principal<JWTPrincipal>()
                    val id =  principal!!.payload.getClaim("id").asInt()
                    val type = principal.payload.getClaim("user_type").asString()
                    authUseCase.logout(id, type)
                    call.respond(HttpStatusCode.Accepted)
                }
            }
        }
    }
}