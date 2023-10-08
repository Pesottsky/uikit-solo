package com.heisy.routing

import com.heisy.plugins.UserTypes
import com.heisy.plugins.dbQuery
import com.heisy.plugins.getId
import com.heisy.schema.CompanyService
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
                    val companies = dbQuery {
                        companyService.getAll().map { it.toDataClass() }
                    }
                    call.respond(HttpStatusCode.OK, companies)
                }

                put {
                    val userId = getId(call)
                    val company = dbQuery {
                        companyService.update(userId, call.receive()).toDataClass()
                    }
                    call.respond(HttpStatusCode.Accepted, company)
                }

                route("/{id}") {
                    get {
                        val id = call.parameters["id"] ?: throw MissingRequestParameterException("id is null")
                        val company = dbQuery { companyService.get(id.toInt()) }.toDataClass()
                        call.respond(HttpStatusCode.OK, company)
                    }
                }

            }
        }
    }
}