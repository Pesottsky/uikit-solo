package com.heisy.errors

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
                is NotFoundException -> call.respondText(text = message, status = HttpStatusCode.NotFound)
                is ExpiredException -> call.respondText(text = message, status = HttpStatusCode.Gone)
                is UnauthorizedException -> call.respondText(text = message, status = HttpStatusCode.Unauthorized)
                else -> call.respondText(text = "Произошла ошибка.\nПовторите попытку позже", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}

class ExpiredException(message: String) : Exception(message)

class UnauthorizedException(message: String) : Exception(message)