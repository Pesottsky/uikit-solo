package com.heisy.plugins

import com.heisy.data.usecase.AuthUseCase
import com.heisy.routing.configureAuthRouting
import com.heisy.routing.configureCompanyRouting
import com.heisy.routing.configureProfileRouting
import com.heisy.routing.configureTablesRouting
import com.heisy.schema.*
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:sqlite:./main",
        driver = "org.sqlite.JDBC",
    )
    configureSchema(database)

}

fun Application.configureTestDatabases() {
    val database = Database.connect(
        url = "jdbc:sqlite:./test",
        driver = "org.sqlite.JDBC",
    )
    configureSchema(database)
}

fun Application.configureSchema(database: Database) {
    val userService = UserService(database)
    val tablesService = FreelsTablesService(database)
    val rowService = FreelsRowsService(database)
    val profileService = ProfilesService(database)
    val freelsService = FreelsService(database)
    val linkService = LinksService(database)
    val companyService = CompanyService(database)
    val tokensService = TokensService(database)
    val commentService = CommentService(database)
    val authUseCase = AuthUseCase(
        userService = userService,
        freelsService = freelsService,
        linkService = linkService,
        rowService = rowService,
        tokensService = tokensService
    )

    configureAuthRouting(authUseCase)
    configureTablesRouting(tablesService, rowService, profileService, linkService, commentService)
    configureCompanyRouting(userService, companyService)
    configureProfileRouting(freelsService, profileService)


}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
