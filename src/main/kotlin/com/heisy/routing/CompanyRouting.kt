package com.heisy.routing

import com.heisy.plugins.UserTypes
import com.heisy.plugins.dbQuery
import com.heisy.plugins.getIdTypePair
import com.heisy.schema.CompanyService
import com.heisy.utils.LogUtils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureCompanyRouting(companyService: CompanyService) {
    routing {
        authenticate(UserTypes.Company.name) {
            route("/company") {
                get {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val company = dbQuery {
                        companyService.getByUserId(pair.first).toDataClass()
                    }
                    call.respond(HttpStatusCode.Accepted, company)
                }

                put {
                    val pair = getIdTypePair(call)
                    call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                    val company = dbQuery {
                        companyService.update(pair.first, call.receive()).toDataClass()
                    }
                    call.respond(HttpStatusCode.Accepted, company)
                }

            }
        }

        authenticate(UserTypes.Company.name, UserTypes.Freel.name) {
            get("company/all") {
                val pair = getIdTypePair(call)
                call.application.environment.log.info(LogUtils.createLog(pair, call.request.uri))
                val companies = dbQuery {
                    companyService.getAll().map { it.toDataClass() }
                }
                call.respond(HttpStatusCode.OK, companies)
            }

            route("/{id}") {
                get {
                    call.application.environment.log.info(
                        LogUtils.createLog(
                            getIdTypePair(call),
                            call.request.uri
                        )
                    )
                    val id = call.parameters["id"] ?: throw MissingRequestParameterException("id is null")
                    val company = dbQuery { companyService.get(id.toInt()) }.toDataClass()
                    call.respond(HttpStatusCode.OK, company)
                }
            }
        }
    }
}