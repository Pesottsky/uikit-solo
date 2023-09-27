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
    linksService: LinksService
) {
    routing {
        authenticate("company") {
            route("/table") {
                post {
                    val name = call.request.queryParameters["name"]
                    if (name != null) {
                        val table= tablesService.create(name, getId(call))
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
                    val tableID =
                        call.request.queryParameters["tableID"] ?: throw BadRequestException("tableID is null")
                    tablesService.delete(tableID.toInt())
                }

                route("/row") {
                    post {
                        val tableId = call.request.queryParameters["tableID"]
                        val profile = call.receive<Profile>()
                        if (tableId != null) {
                            val profileResult = profilesService.create(profile)
                            val row = rowService.create(tableId.toInt(), profileResult)
                            call.respond(HttpStatusCode.Created, row.id.value)
                        } else {
                            call.respond(HttpStatusCode.BadRequest, "tableID is null")
                        }
                    }

                    // Создать строку с Id уже существующего профиля
                    post("{profileId}") {
                        val tableId =
                            call.request.queryParameters["tableID"] ?: throw BadRequestException("tableID is null")
                        val profileId = call.parameters["profileId"] ?: throw BadRequestException("profileId is null")

                        val profileResult = profilesService.get(profileId.toInt())
                        val row = rowService.create(tableId.toInt(), profileResult)
                        call.respond(HttpStatusCode.Created, row.id.value)

                    }

                    // Изменить профиль в строке
                    put("{rowId}") {
                        val tableId =
                            call.request.queryParameters["tableID"] ?: throw BadRequestException("tableID is null")
                        val rowId = call.parameters["rowId"] ?: throw BadRequestException("profileId is null")
                        val profile = call.receive<Profile>()

                        rowService.updateProfile(rowId.toInt(), profile)
                        call.respond(HttpStatusCode.Accepted)

                    }

                }

                get("/link") {
                    val rowId = call.request.queryParameters["row"] ?: throw BadRequestException("row is null")
                    val link = linksService.create()
                    rowService.updateLink(rowId.toInt(), link)
                    call.respond(HttpStatusCode.Created, link.toDataClass())
                }
            }
        }
    }
}