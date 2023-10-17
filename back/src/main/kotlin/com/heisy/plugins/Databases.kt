package com.heisy.plugins

import com.heisy.utils.InjectionUtils
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Application.configureDatabases() {
    val url = environment.config.property("ktor.deployment.db").getString()
    val user = environment.config.property("ktor.deployment.db_user").getString()
    val password = environment.config.property("ktor.deployment.db_password").getString()
    val port = environment.config.property("ktor.deployment.db_port").getString()
    val table = environment.config.property("ktor.deployment.db_table").getString()

    val database = Database.connect(
        url = "jdbc:mysql://$url:$port/$table",
        user = user,
        password = password
    )
    InjectionUtils.provideDataBase(database = database)

}

fun Application.configureSchema() {
    InjectionUtils.provideTokensSerivce(
        rLifeTime = environment.config.property("jwt.refresh_lifetime").getString().toInt(),
        recoveryTime = environment.config.property("recovery.password").getString().toInt()
    )

    InjectionUtils.provideGradeService()
    InjectionUtils.provideLoadingService()
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
