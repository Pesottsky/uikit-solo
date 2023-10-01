package com.heisy.routing

import com.heisy.plugins.dbQuery
import com.heisy.plugins.getId
import com.heisy.schema.FreelsService
import com.heisy.schema.Profile
import com.heisy.schema.ProfilesService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureProfileRouting(freelService: FreelsService, profileService: ProfilesService) {
    routing {
        authenticate("freel") {
            route("/profile") {
                get {
                    val userId = getId(call)
                    val freel = freelService.get(userId)
                    val profile = dbQuery { freel.getProfile() }

                    if (profile == null) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.OK, profile)
                    }
                }

                put {
                    val userId = getId(call)
                    val freel = freelService.get(userId)
                    val freelProfile = dbQuery { freel.getProfile() }
                    // Если у фрилансера нет профиля, то создадим его
                    if (freelProfile == null) {
                        val profile = profileService.create(call.receive())

                        dbQuery { freel.profile = profile }
                        call.respond(HttpStatusCode.Created)
                    } else {
                        val receivedProfile = call.receive<Profile>()
                        // Иначе проверим, что профиль фрилансера соответсвует изменяемому профилю
                        if (freelProfile.id == receivedProfile.id) {
                            profileService.update(receivedProfile)
                            call.respond(HttpStatusCode.Created)
                        } else {
                            throw BadRequestException("Id профиля не соответствует id профиля пользователя")
                        }

                    }
                }

            }
        }

        get("/profile/{id}") {
            val profileId = call.parameters["id"] ?: throw MissingRequestParameterException("id")
            val profile = profileService.get(profileId.toInt()).toDataClass()
            call.respond(HttpStatusCode.OK, profile)
        }


    }
}