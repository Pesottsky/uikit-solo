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
                    // TODO проверка на то, что строка в таблице которая у пользователя а не левая

                    val tableID =
                        call.request.queryParameters["table_id"] ?: throw BadRequestException("table_id is null")
                    tablesService.delete(tableID.toInt())
                }

                route("/{table_id}/row") {
                    post {
                        val tableId = call.parameters["table_id"]
                        val profile = call.receive<Profile>()
                        if (tableId != null) {
                            val profileResult = profilesService.create(profile)
                            val row = rowService.create(tableId.toInt(), profileResult)
                            call.respond(HttpStatusCode.Created, row.toDataClass())
                        } else {
                            call.respond(HttpStatusCode.BadRequest, "table_id is null")
                        }
                    }

                    // Создать строку с Id уже существующего профиля
                    post("/profile") {
                        // TODO проверка на то, что строка в таблице которая у пользователя а не левая
                        val tableId =
                            call.parameters["table_id"] ?: throw BadRequestException("table_id is null")
                        val profileId = call.request.queryParameters["profile_id"] ?: throw BadRequestException("profile_id is null")

                        val profileResult = profilesService.get(profileId.toInt())
                        val row = rowService.create(tableId.toInt(), profileResult)
                        call.respond(HttpStatusCode.Created, row.toDataClass())

                    }

                    // Изменить профиль в строке
                    put("/{rowId}") {
                        // TODO проверка на то, что строка в таблице которая у пользователя а не левая
                        val tableId = call.parameters["table_id"] ?: throw BadRequestException("table_id is null")
                        val rowId = call.parameters["row_id"] ?: throw BadRequestException("row_id is null")
                        val profile = call.receive<Profile>()

                        rowService.updateProfile(rowId.toInt(), profile)
                        call.respond(HttpStatusCode.Accepted)

                    }

                }

                get("/link") {
                    // TODO если ссылка генерируется не на запись (записи пока нет)

                    val rowId = call.request.queryParameters["row_id"] ?: throw BadRequestException("row_id is null")
                    val link = linksService.create()
                    rowService.updateLink(rowId.toInt(), link)
                    call.respond(HttpStatusCode.Created, link.toDataClass())
                }
            }
        }
    }
}