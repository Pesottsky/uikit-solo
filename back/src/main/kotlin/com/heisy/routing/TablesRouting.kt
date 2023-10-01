package com.heisy.routing

import com.heisy.plugins.getId
import com.heisy.schema.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureTablesRouting(
    tablesService: FreelsTablesService,
    rowService: FreelsRowsService,
    profilesService: ProfilesService,
    linksService: LinksService,
    commentService: CommentService
) {
    routing {
        authenticate("company") {
            route("/table") {
                post {
                    val name = call.request.queryParameters["name"]
                    if (name != null) {
                        val table = tablesService.create(name, getId(call))
                        call.respond(HttpStatusCode.Created, table)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Name is null")
                    }
                }

                get {
                    val tables = tablesService.getByUserId(getId(call))
                    call.respond(HttpStatusCode.OK, tables)
                }

                delete {
                    tablesService.delete(getId(call))
                }

                route("/row") {
                    post {
                        val profile = call.receive<Profile>()
                        val profileResult = profilesService.create(profile)
                        val row = rowService.create(getId(call), profileResult)
                        call.respond(HttpStatusCode.Created, row.toDataClass())

                    }

                    // Создать строку с Id уже существующего профиля
                    post("/profile") {

                        val profileId = call.request.queryParameters["profile_id"]
                            ?: throw BadRequestException("profile_id is null")

                        val profileResult = profilesService.get(profileId.toInt())
                        val row = rowService.create(getId(call), profileResult)
                        call.respond(HttpStatusCode.Created, row.toDataClass())

                    }

                    // Изменить профиль в строке
                    put("/{rowId}") {
                        val rowId = call.parameters["row_id"] ?: throw BadRequestException("row_id is null")
                        val profile = call.receive<Profile>()

                        rowService.updateProfile(rowId.toInt(), profile, getId(call))
                        call.respond(HttpStatusCode.Accepted)

                    }

                }
            }

            get("/link") {
                val rowId = call.request.queryParameters["row_id"] ?: throw BadRequestException("row_id is null")
                val link = linksService.create()
                rowService.updateLink(rowId.toInt(), link, getId(call))
                call.respond(HttpStatusCode.Created, link.toDataClass())
            }

            post("/comment") {
                val comment = commentService.create(call.receive())
                call.respond(HttpStatusCode.Created, comment)
            }

        }
    }
}