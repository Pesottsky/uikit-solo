package com.heisy.routing

import com.heisy.domain.usecase.IAuthUseCase
import com.heisy.email.EmailSender
import com.heisy.email.MailBundle
import com.heisy.email.MailFrom
import com.heisy.email.MailSubjects
import com.heisy.plugins.UserTypes
import com.heisy.plugins.getIdTypePair
import com.heisy.schema.ForgetPassword
import com.heisy.schema.Token
import com.heisy.utils.LogUtils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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

        route("update_password") {
            post {
                val tokens = authUseCase.updatePassword(call.receive())
                call.respond(HttpStatusCode.Accepted, tokens)
            }
        }

        route("forget_password") {
            post {
                val forgetPassword = call.receive<ForgetPassword>()
//                val code = authUseCase.forgetPassword(call.application, forgetPassword)
//                val text = app.environment.config.property("smtp.${bundle.from.configParam}.password").getString()
                launch(Dispatchers.IO + SupervisorJob()) {
                    EmailSender.sendMail(
                        call.application, MailBundle(
                            to = forgetPassword.login,
                            from = MailFrom.NO_REPLAY,
                            subject = MailSubjects.PasswordRecovery,
                            text = "message text"
                        )
                    )
                }
                call.respond(HttpStatusCode.Accepted)
            }
        }

        authenticate(UserTypes.Company.name, UserTypes.Freel.name) {
            route("/logout") {
                delete {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    authUseCase.logout(pair.first.toInt(), pair.second)
                    call.respond(HttpStatusCode.Accepted)
                }
            }
        }
    }
}