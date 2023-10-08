package com.heisy.plugins

import com.heisy.routing.configureAuthRouting
import com.heisy.routing.configureCompanyRouting
import com.heisy.routing.configureProfileRouting
import com.heisy.routing.configureTablesRouting
import com.heisy.utils.InjectionUtils
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Application.configureDatabases() {
    val url = environment.config.property("ktor.deployment.db").getString()
    val user = environment.config.property("ktor.deployment.db_user").getString()
    val password = environment.config.property("ktor.deployment.db_password").getString()

    val database = Database.connect(
        url = url,
        driver = "org.sqlite.JDBC",
        user = user,
        password = password
    )
    configureSchema(database)

}

private fun Application.configureSchema(database: Database) {
    InjectionUtils.provideDataBase(database = database)
    InjectionUtils.provideTokensSerivce(
        rLifeTime = environment.config.property("jwt.refresh_lifetime").getString().toInt()
    )


    configureAuthRouting(InjectionUtils.provideAuthUseCase())
    configureTablesRouting(
        tablesService =  InjectionUtils.provideTablesService(),
        rowService = InjectionUtils.provideRowService(),
        profilesService = InjectionUtils.provideProfileService(),
        linksService = InjectionUtils.provideLinkService(),
        commentService = InjectionUtils.provideCommentService()
    )
    configureCompanyRouting(InjectionUtils.provideCompanyService())
    configureProfileRouting(InjectionUtils.provideProfileService())
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
