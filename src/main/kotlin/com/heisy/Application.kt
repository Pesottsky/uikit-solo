package com.heisy

import com.heisy.errors.errorsHandling
import com.heisy.plugins.*
import com.heisy.routing.*
import com.heisy.utils.InjectionUtils
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    InjectionUtils.provideApplication(this)
    configureSerialization()
    configureHTTP()
    configureSecurity()
    errorsHandling()
    configureDatabases()
    configureSchema()
    configureAuthRouting(InjectionUtils.provideAuthUseCase())

    configureTablesRouting(
        tablesService = InjectionUtils.provideTablesService(),
        rowService = InjectionUtils.provideRowService(),
        profilesService = InjectionUtils.provideProfileService(),
        linksService = InjectionUtils.provideLinkService(),
        commentService = InjectionUtils.provideCommentService()
    )
    configureCompanyRouting(InjectionUtils.provideCompanyService())
    configureProfileRouting(InjectionUtils.provideProfileService())
    configureAdminRouting(
        profilesService = InjectionUtils.provideProfileService(),
        linksService = InjectionUtils.provideLinkService(),
        rowService = InjectionUtils.provideRowService()
    )
}
