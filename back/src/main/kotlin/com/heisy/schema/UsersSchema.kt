package com.heisy.schema

import com.heisy.plugins.dbQuery
import io.ktor.server.plugins.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

class ExposedUser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedUser>(UserService.Users)

    var login by UserService.Users.login
    var password by UserService.Users.password
    val tables by ExposedFreelsTable referrersOn FreelsTablesService.FreelsTables.userId
    var company by ExposedCompany referencedOn UserService.Users.company

    fun toDataClass(): User {
        return User(
            login = this.login,
            password = this.password,
        )
    }
}

class UserService(database: Database) {
    object Users : IntIdTable() {
        val login = varchar("login", length = 50)
        val password = varchar("password", length = 250)
        val company = reference("company", CompanyService.Companies, ReferenceOption.CASCADE)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun create(user: User): Int? = dbQuery {
        val checkLoginInFreels = ExposedFreel.find { FreelsService.Freels.login eq user.login }.singleOrNull()
        val checkLoginInUsers = ExposedUser.find { Users.login eq user.login }.singleOrNull()

        if (checkLoginInFreels == null && checkLoginInUsers == null) {
            val exposedCompany = ExposedCompany.new {
                name = user.name!!
            }

            ExposedUser.new {
                login = user.login
                password = BCrypt.hashpw(user.password, BCrypt.gensalt())
                company = exposedCompany
            }.id.value
        } else {
            null
        }
    }

    suspend fun read(id: Int): ExposedUser? {
        return dbQuery {
            ExposedUser.findById(id)
        }
    }

    suspend fun checkAuth(user: User): Int? =
        dbQuery {
            val exposedUser = ExposedUser.find { (Users.login eq user.login) }
                .singleOrNull() ?: return@dbQuery null
            if (!BCrypt.checkpw(
                    user.password,
                    exposedUser.password
                )
            ) throw BadRequestException("Неправильный логин или пароль")
            exposedUser.id.value
        }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[password] = user.password
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}
