package com.heisy.routing

import com.heisy.domain.usecase.IAuthUseCase
import com.heisy.email.EmailSender
import com.heisy.email.MailBundle
import com.heisy.email.MailFrom
import com.heisy.email.MailSubjects
import com.heisy.plugins.UserTypes
import com.heisy.plugins.getIdTypePair
import com.heisy.schema.ForgetPassword
import com.heisy.schema.Freel
import com.heisy.schema.Token
import com.heisy.schema.User
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
                val link = call.request.queryParameters["link"]
                val token: Token = if (link != null) {
                    authUseCase.loginByLink(link, call.receive())
                } else {
                    authUseCase.login(call.receive())
                }
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
                val user: User = call.receive()
                val token = authUseCase.registerCompany(user)
                launch(Dispatchers.IO + SupervisorJob()) {
                    EmailSender.sendMail(
                        call.application, MailBundle(
                            to = user.login,
                            from = MailFrom.HELLO,
                            subject = MailSubjects.Registration,
                            text = "Привет, вы зарегистрировались на сервисе Soloteam! \n" +
                                    "\n" +
                                    "Soloteam помогает создавать и управлять вашей базой фрилансеров, вовремя находить людей на проект и не тратить время на переговоры.\n" +
                                    "\n" +
                                    "Вы сможете:\n" +
                                    "— организовать и управлять своей базой фрилансеров\n" +
                                    "— легко добавлять новых специалистов и не терять старых\n" +
                                    "— всегда видеть свежую информацию, занятость и расценки\n" +
                                    "\n" +
                                    "Можем импортировать вашу, уже существующую базу, напишите нам в телеграм — @vviiktoor.\n" +
                                    "Мы всегда рады помочь, любые вопросы по работе сервиса и предложения — пишите на service@soloteam.io или лично в телеграм @vviiktoor\n" +
                                    "\n" +
                                    "Спасибо, что вы с нами"
                        )
                    )
                }
                call.respond(HttpStatusCode.Created, token)
            }

            route("/freel") {
                post {
                    val link = call.request.queryParameters["link"]
                    val freel =  call.receive<Freel>()
                    val token: Token = if (link != null) {
                        authUseCase.registerFreelByLink(link, freel)
                    } else {
                        authUseCase.registerFreel(freel)
                    }
                    launch(Dispatchers.IO + SupervisorJob()) {
                        EmailSender.sendMail(
                            call.application, MailBundle(
                                to = freel.login,
                                from = MailFrom.HELLO,
                                subject = MailSubjects.Registration,
                                text = "Привет, вы зарегистрировались на сервисе Soloteam! \n" +
                                        "\n" +
                                        "Soloteam помогает фрилансерам быть на связи с заказчиками и получать заказы.\n" +
                                        "\n" +
                                        "Вы сможете:\n" +
                                        "— в один клик делиться резюме и портфолио\n" +
                                        "— уведомлять заказчиков, что вы открыты к новым заказам\n" +
                                        "— не терять контакты и планировать проекты на будущее\n" +
                                        "\n" +
                                        "Что дальше?\n" +
                                        "1. Заполните, пожалуйста всю информацию о себе \n" +
                                        "2. Обновляйте ваш статус, когда открыты к проектам и когда заняты\n" +
                                        "3. Поделитесь ссылкой на сервис https://soloteam.io с вашими заказчиками, чтобы сотрудничать в будущем\n" +
                                        "\n" +
                                        "Мы всегда рады помочь, любые вопросы по работе сервиса и предложения — пишите на service@soloteam.io или лично в телеграм @vviiktoor\n" +
                                        "\n" +
                                        "Спасибо, что вы с нами"
                            )
                        )
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
                val code = authUseCase.forgetPassword(call.application, forgetPassword)
                launch(Dispatchers.IO + SupervisorJob()) {
                    EmailSender.sendMail(
                        call.application, MailBundle(
                            to = forgetPassword.login,
                            from = MailFrom.NO_REPLAY,
                            subject = MailSubjects.PasswordRecovery,
                            text = "Здравствуйте, мы получили запрос на сброс вашего пароля Soloteam.\n" +
                                    "Для сброса пароля перейдите по ссылке ${call.application.environment.config.property("path.recovery").getString() + code} и следуйте дальнейшим шагам.\n" +
                                    "\n" +
                                    "С уважением, команда Soloteam"
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
                    authUseCase.logout(pair.first, pair.second)
                    call.respond(HttpStatusCode.Accepted)
                }
            }
        }
    }
}