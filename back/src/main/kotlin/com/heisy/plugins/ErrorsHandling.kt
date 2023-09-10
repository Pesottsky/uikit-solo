package com.heisy.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.errorsHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val message = cause.message.toString()
            when (cause) {
                is BadRequestException -> call.respondText(text = message, status = HttpStatusCode.BadRequest)
                else -> call.respondText(text = "500", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}