package com.heisy.routing

import com.heisy.plugins.UserTypes
import com.heisy.plugins.dbQuery
import com.heisy.plugins.getId
import com.heisy.schema.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.math.exp

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
                    val name = call.request.queryParameters["name"] ?: throw BadRequestException("Name is null")
                    val table = dbQuery { tablesService.create(name, getId(call)).toDataClass() }
                    call.respond(HttpStatusCode.Created, table)
                }

                get {
                    val tables = dbQuery { tablesService.getByUserId(getId(call)).map { it.toDataClass() } }
                    call.respond(HttpStatusCode.OK, tables)
                }

                delete {
                    dbQuery { tablesService.delete(getId(call)) }
                    call.respond(HttpStatusCode.Accepted)
                }

                route("/row") {
                    post {
                        val row = dbQuery {
                            val profile = call.receive<Profile>()
                            val profileResult = profilesService.create(profile)
                            rowService.create(getId(call), profileResult).toDataClass()
                        }

                        call.respond(HttpStatusCode.Created, row)

                    }

                    // Создать строку с Id уже существующего профиля
                    post("/profile") {

                        val profileId = call.request.queryParameters["profile_id"]
                            ?: throw BadRequestException("profile_id is null")
                        val row = dbQuery {
                            val profileResult = profilesService.get(profileId.toInt()) ?: throw NotFoundException()
                            rowService.create(getId(call), profileResult).toDataClass()
                        }
                        call.respond(HttpStatusCode.Created, row)
                    }

                    // Изменить профиль в строке
                    put("/{rowId}") {
                        val rowId = call.parameters["row_id"] ?: throw BadRequestException("row_id is null")
                        val profile = call.receive<Profile>()
                        val profileResult  = dbQuery {
                            val row = rowService.checkRowForUpdate(rowId.toInt(), getId(call))
                            rowService.checkForOwner(row)
                            val profileResult = profilesService.get(profile.id!!) ?: throw NotFoundException()
                            profilesService.updateByCompany(profileResult, profile).toDataClass()
                            }
                        call.respond(HttpStatusCode.Accepted, profileResult)

                    }

                }
            }

            get("/link") {
                val rowId = call.request.queryParameters["row_id"] ?: throw BadRequestException("row_id is null")
                val link = dbQuery {
                    val exposedLink = linksService.create()
                    val row = rowService.checkRowForUpdate(rowId.toInt(), getId(call))
                    rowService.checkForOwner(row)
                    rowService.updateLink(row, exposedLink)
                    exposedLink.toDataClass()
                }
                call.respond(HttpStatusCode.Created, link)
            }

//            post("/comment") {
//                val comment = commentService.create(call.receive())
//                call.respond(HttpStatusCode.Created, comment)
//            }
        }
    }
}