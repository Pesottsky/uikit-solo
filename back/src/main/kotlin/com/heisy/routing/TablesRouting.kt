package com.heisy.routing

import com.heisy.email.EmailSender
import com.heisy.email.MailBundle
import com.heisy.email.MailFrom
import com.heisy.email.MailSubjects
import com.heisy.plugins.UserTypes
import com.heisy.plugins.dbQuery
import com.heisy.plugins.getIdTypePair
import com.heisy.schema.*
import com.heisy.utils.LogUtils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun Application.configureTablesRouting(
    tablesService: FreelsTablesService,
    rowService: FreelsRowsService,
    profilesService: ProfilesService,
    linksService: LinksService,
    commentService: CommentService
) {
    routing {
        authenticate(UserTypes.Company.name) {
            route("/table") {
                post {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val name = call.request.queryParameters["name"] ?: throw BadRequestException("name is null")
                    val table = dbQuery { tablesService.create(name, pair.first).toDataClass() }
                    call.respond(HttpStatusCode.Created, table)
                }

                post("/name/{table_id}") {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val name = call.request.queryParameters["name"] ?: throw BadRequestException("name is null")
                    val tableId = call.parameters["table_id"] ?: throw BadRequestException("table_id is null")

                    val table = dbQuery { tablesService.update(pair.first, tableId.toInt(), name).toDataClass() }
                    call.respond(HttpStatusCode.Accepted, table)
                }

                get {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val tables = dbQuery { tablesService.getByUserId(pair.first).map { it.toDataClass() } }
                    call.respond(HttpStatusCode.OK, tables)
                }

                delete {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    dbQuery { tablesService.delete(pair.first) }
                    call.respond(HttpStatusCode.Accepted)
                }

                route("/row") {
                    post {
                        val pair = getIdTypePair(call)
                        call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                        val row = dbQuery {
                            val profile = call.receive<Profile>()
                            val profileResult = profilesService.create(profile)
                            rowService.create(pair.first, profileResult).toDataClass()
                        }

                        call.respond(HttpStatusCode.Created, row)
                    }

                    // Создать строку с Id уже существующего профиля
                    post("/profile") {
                        val pair = getIdTypePair(call)
                        call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                        val profileId = call.request.queryParameters["profile_id"]
                            ?: throw BadRequestException("profile_id is null")
                        val row = dbQuery {
                            val profileResult = profilesService.get(profileId.toInt()) ?: throw NotFoundException()
                            rowService.create(pair.first, profileResult).toDataClass()
                        }
                        call.respond(HttpStatusCode.Created, row)
                    }

                    // Изменить профиль в строке
                    put("/{row_id}") {
                        val pair = getIdTypePair(call)
                        call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                        val rowId = call.parameters["row_id"] ?: throw BadRequestException("row_id is null")
                        val profile = call.receive<Profile>()
                        val profileResult = dbQuery {
                            val row = rowService.checkRowForUpdate(rowId.toInt(), pair.first)
                            rowService.checkForOwner(row)
                            val profileResult = profilesService.get(row.profile.id.value) ?: throw NotFoundException()
                            profilesService.updateByCompany(profileResult, profile).toDataClass()
                        }
                        call.respond(HttpStatusCode.Accepted, profileResult)
                    }

                    delete("/{row_id}") {
                        val pair = getIdTypePair(call)
                        call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                        val rowId = call.parameters["row_id"] ?: throw BadRequestException("row_id is null")
                        val table = dbQuery {
                            val row = rowService.checkRowForUpdate(rowId.toInt(), pair.first)
                            rowService.delete(row)
                            tablesService.getByUserId(pair.first).map { it.toDataClass() }
                        }
                        call.respond(HttpStatusCode.OK, table)
                    }

                }
            }

            get("/link") {
                val pair = getIdTypePair(call)
                call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                val rowId = call.request.queryParameters["row_id"] ?: throw BadRequestException("row_id is null")
                val link = dbQuery {
                    val row = rowService.checkRowForUpdate(rowId.toInt(), pair.first)
                    rowService.checkForOwner(row)
                    val exposedLink = linksService.create()
                    rowService.updateLink(row, exposedLink)
                    exposedLink.toDataClass()
                }
                call.respond(HttpStatusCode.Created, link)
            }

            route("send/email") {
                post {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val invite = call.receive<Invite>()
                    val row = dbQuery {
                        val row = rowService.checkRowForUpdate(invite.rowId, pair.first)
                        rowService.checkForOwner(row)
                    }

                    val exposedFreel = dbQuery {
                        ExposedFreel.find { FreelsService.Freels.login eq invite.email }.singleOrNull()
                    }
                    // Приглашение сгенерировали на почту, которая уже зарегана в системе
                    if (exposedFreel != null) {
                        dbQuery {
                            rowService.checkTableForDublicates(row.table, exposedFreel.profile)
                            row.profile = exposedFreel.profile
                        }
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }
                    val link = dbQuery {
                        val exposedLink = linksService.update(invite)
                        exposedLink.toDataClass()
                    }
                    launch(Dispatchers.IO + SupervisorJob()) {
                        EmailSender.sendMail(
                            call.application, MailBundle(
                                to = invite.email,
                                from = MailFrom.HELLO,
                                subject = MailSubjects.InviteByLink,
                                text = EmailSender.getFreelInviteText(link.link)
                            )
                        )
                        dbQuery { linksService.onEmailSending(invite.linkId) }
                    }
                    call.respond(HttpStatusCode.Created, link.copy(isEmailSending = true))
                }
            }

            route("/comment") {
                get {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val profileId = call.request.queryParameters["profile_id"]?.toInt()
                        ?: throw BadRequestException("profile_id is null")
                    val userId = pair.first
                    val comment = commentService.get(profileId, userId)
                    if (comment == null) call.respond(HttpStatusCode.NoContent)
                    else call.respond(HttpStatusCode.OK, comment)
                }

                post {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val userId = pair.first
                    val comment = commentService.create(call.receive(), userId)
                    call.respond(HttpStatusCode.Created, comment)
                }
            }
        }
    }
}