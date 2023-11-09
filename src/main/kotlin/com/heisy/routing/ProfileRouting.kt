package com.heisy.routing

import com.heisy.plugins.UserTypes
import com.heisy.plugins.dbQuery
import com.heisy.plugins.getIdTypePair
import com.heisy.schema.ExposedFreel
import com.heisy.schema.ExposedGrade
import com.heisy.schema.ExposedLoading
import com.heisy.schema.ProfilesService
import com.heisy.utils.LogUtils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureProfileRouting(profileService: ProfilesService) {
    routing {
        authenticate(UserTypes.Freel.name) {
            route("/profile") {
                get {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val profile = dbQuery { ExposedFreel.findById(pair.first)?.getProfile() ?: NotFoundException() }
                    call.respond(HttpStatusCode.OK, profile)
                }

                put {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val profile =
                        dbQuery { profileService.updateByFreel(pair.first, call.receive()).toDataClass() }
                    call.respond(HttpStatusCode.Accepted, profile)
                }

            }

            route("/loading") {
                post {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val profile =
                        dbQuery { profileService.updateLoading(pair.first, call.receive()).toDataClass() }
                    call.respond(HttpStatusCode.Accepted, profile)
                }
            }
        }

        authenticate(UserTypes.Company.name, UserTypes.Freel.name) {
            route("/grade") {
                get {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val gradeList =
                        dbQuery { ExposedGrade.all().map { it.toDataClass() } }
                    call.respond(HttpStatusCode.OK, gradeList)
                }
            }

            route("/loading") {
                get {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val loadingList =
                        dbQuery { ExposedLoading.all().map { it.toDataClass() } }
                    call.respond(HttpStatusCode.OK, loadingList)
                }
            }
        }

        get("/profile/{id}") {
            val profileId = call.parameters["id"] ?: throw MissingRequestParameterException("id")
            val profile = dbQuery { profileService.get(profileId.toInt())?.toDataClass() }
            if (profile == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, profile)
        }
    }
}