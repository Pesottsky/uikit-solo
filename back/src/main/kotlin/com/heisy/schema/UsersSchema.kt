package com.heisy.schema

import io.ktor.server.plugins.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class User(
    @SerialName("login")
    val login: String,

    @SerialName("password")
    val password: String,

    @SerialName("name")
    val name: String? = null
)

@Serializable
data class UpdatePassword(
    @SerialName("login")
    val login: String,

    @SerialName("new_password")
    val newPassword: String,

    @SerialName("code")
    val code: String
)

@Serializable
data class ForgetPassword(
    @SerialName("login")
    val login: String,
)

class ExposedUser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedUser>(UserService.Users)

    var login by UserService.Users.login
    var password by UserService.Users.password
    val tables by ExposedFreelsTable referrersOn FreelsTablesService.FreelsTables.userId
    var company by ExposedCompany referencedOn UserService.Users.company
    var registrationDate by UserService.Users.registrationDate
    var paymentUntil by UserService.Users.paymentUntil
}

class UserService(database: Database) {
    object Users : IntIdTable() {
        val login = varchar("login", length = 50)
        val password = varchar("password", length = 250)
        val company = reference("company", CompanyService.Companies, ReferenceOption.CASCADE)
        val registrationDate = long("registrationDate")
        val paymentUntil = long("paymentUntil").nullable()
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    object Errors {
        const val wrongPair = "Неправильный логин или пароль"
        const val emailBusy = "Измените E-mail"
    }

    private fun checkLoginBusy(user: User) {
        val checkLoginInFreels = ExposedFreel.find { FreelsService.Freels.login eq user.login }.singleOrNull()
        val checkLoginInUsers = ExposedUser.find { Users.login eq user.login }.singleOrNull()
        if (checkLoginInFreels != null || checkLoginInUsers != null) throw BadRequestException(Errors.emailBusy)
    }

    fun create(user: User): ExposedUser {
        checkLoginBusy(user)

        val exposedCompany = ExposedCompany.new {
            name = "Ваша компания"
        }
        return ExposedUser.new {
            login = user.login
            password = BCrypt.hashpw(user.password, BCrypt.gensalt())
            company = exposedCompany
            registrationDate = System.currentTimeMillis()
        }
    }

    fun checkAuth(user: User): ExposedUser? {
        val exposedUser = ExposedUser.find { Users.login eq user.login }
            .singleOrNull() ?: return null
        if (!BCrypt.checkpw(
                user.password,
                exposedUser.password
            )
        ) throw BadRequestException(Errors.wrongPair)

        return exposedUser
    }
}
