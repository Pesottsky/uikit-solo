package com.heisy.routing

import com.heisy.email.EmailSender
import com.heisy.email.MailBundle
import com.heisy.email.MailFrom
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
                    val name = call.request.queryParameters["name"] ?: throw BadRequestException("Name is null")
                    val table = dbQuery { tablesService.create(name, pair.first.toInt()).toDataClass() }
                    call.respond(HttpStatusCode.Created, table)
                }

                get {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val tables = dbQuery { tablesService.getByUserId(pair.first.toInt()).map { it.toDataClass() } }
                    call.respond(HttpStatusCode.OK, tables)
                }

                delete {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    dbQuery { tablesService.delete(pair.first.toInt()) }
                    call.respond(HttpStatusCode.Accepted)
                }

                route("/row") {
                    post {
                        val pair = getIdTypePair(call)
                        call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                        val row = dbQuery {
                            val profile = call.receive<Profile>()
                            val profileResult = profilesService.create(profile)
                            rowService.create(pair.first.toInt(), profileResult).toDataClass()
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
                            rowService.create(pair.first.toInt(), profileResult).toDataClass()
                        }
                        call.respond(HttpStatusCode.Created, row)
                    }

                    // Изменить профиль в строке
                    put("/{rowId}") {
                        val pair = getIdTypePair(call)
                        call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                        val rowId = call.parameters["row_id"] ?: throw BadRequestException("row_id is null")
                        val profile = call.receive<Profile>()
                        val profileResult = dbQuery {
                            val row = rowService.checkRowForUpdate(rowId.toInt(), pair.first.toInt())
                            rowService.checkForOwner(row)
                            val profileResult = profilesService.get(profile.id!!) ?: throw NotFoundException()
                            profilesService.updateByCompany(profileResult, profile).toDataClass()
                        }
                        call.respond(HttpStatusCode.Accepted, profileResult)

                    }

                }
            }

            get("/link") {
                val pair = getIdTypePair(call)
                call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                val rowId = call.request.queryParameters["row_id"] ?: throw BadRequestException("row_id is null")
                val link = dbQuery {
                    val row = rowService.checkRowForUpdate(rowId.toInt(), pair.first.toInt())
                    rowService.checkForOwner(row)
                    val exposedLink = linksService.create()
                    rowService.updateLink(row, exposedLink)
                    exposedLink.toDataClass()
                }
                call.respond(HttpStatusCode.Created, link)
            }

            route("send") {
                post("/email") {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val invite = call.receive<Invite>()
                    val link = dbQuery {
                        val row = rowService.checkRowForUpdate(invite.rowId, pair.first.toInt())
                        rowService.checkForOwner(row)
                        val exposedLink = linksService.update(invite)
                        exposedLink.toDataClass()
                    }

                    // TODO рассылка приглашение в сервис
                    launch(Dispatchers.IO + SupervisorJob()) {
                        EmailSender.sendMail(
                            call.application, MailBundle(
                                to = "noreplay@soloteam.io",
                                from = MailFrom.NO_REPLAY,
                                subject = "Text",
                                text = "message text"
                            )
                        )
                    }
                    call.respond(HttpStatusCode.Created, link)
                }
            }

            route("/comment") {
                get {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val profileId = call.request.queryParameters["profile_id"]?.toInt()
                        ?: throw BadRequestException("profile_id is null")
                    val userId = pair.first.toInt()
                    val comment = commentService.get(profileId, userId)
                    if (comment == null) call.respond(HttpStatusCode.NoContent)
                    else call.respond(HttpStatusCode.OK, comment)
                }

                post {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val userId = pair.first.toInt()
                    val comment = commentService.create(call.receive(), userId)
                    call.respond(HttpStatusCode.Created, comment)
                }
            }
        }
    }
}