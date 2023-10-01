package com.heisy.routing

import com.heisy.plugins.getId
import com.heisy.schema.CompanyService
import com.heisy.schema.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureCompanyRouting(userService: UserService, companyService: CompanyService) {
    routing {
        authenticate("company", "freel") {
            route("/company") {
                get {
                    val companies = companyService.getAll()
                    call.respond(HttpStatusCode.OK, companies)
                }

                put {
                    val userId = getId(call)
                    val user = userService.read(userId)!!

                    companyService.update(user.company.id.value, call.receive())
                    call.respond(HttpStatusCode.Accepted)
                }

                route("/{id}") {
                    get {
                        val id = call.parameters["id"] ?: throw MissingRequestParameterException("id is null")
                        call.respond(HttpStatusCode.OK, companyService.read(id.toInt()))
                    }
                }

            }
        }
    }
}