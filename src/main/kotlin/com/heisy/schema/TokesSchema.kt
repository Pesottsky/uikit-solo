package com.heisy.schema

import com.heisy.plugins.UserTypes
import com.heisy.plugins.createCompanyToken
import com.heisy.plugins.createFreelToken
import com.heisy.plugins.dbQuery
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class Token(
    @SerialName("access")
    val access: String? = null,

    @SerialName("refresh")
    val refresh: String,

    @SerialName("user_type")
    val userType: String? = null
)


class ExposedToken(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedToken>(TokensService.Tokens)

    var userId by TokensService.Tokens.userId
    var refreshToken by TokensService.Tokens.refreshToken
    var userType by TokensService.Tokens.userType
    var expiresAt by TokensService.Tokens.expiresAt
}

class ExposedForgetPassword(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedForgetPassword>(TokensService.Passwords)

    var email by TokensService.Passwords.email
    var code by TokensService.Passwords.code
    var expiresAt by TokensService.Passwords.expiresAt
    var isRecovered by TokensService.Passwords.isRecovered
}


class TokensService(database: Database, private val refreshLifeTime: Long, private val recoveryTime: Long) {
    object Tokens : IntIdTable() {
        val userId = integer("userId")
        val refreshToken = uuid("refreshToken")
        val userType = varchar("userType", 16)
        val expiresAt = long("expiresAt")
    }

    object Passwords : IntIdTable() {
        val email = varchar("email", length = 50)
        val code = uuid("code")
        val expiresAt = long("expiresAt")
        val isRecovered = bool("isRecovered").nullable()
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tokens)
            SchemaUtils.create(Passwords)
        }
    }

    fun generateTokenPair(userId: Int, userType: String): Token {

        val access: String = if (userType == UserTypes.Company.name) createCompanyToken(userId)
        else createFreelToken(userId)

        val refreshToken = UUID.randomUUID()
        val currentTime = System.currentTimeMillis()

        ExposedToken.new {
            this.userId = userId
            this.refreshToken = refreshToken
            this.userType = userType
            expiresAt = currentTime + refreshLifeTime
        }

        return Token(
            access = access,
            refresh = refreshToken.toString(),
            userType = userType
        )
    }


    fun refresh(token: String): Token? {
        val currentTime = System.currentTimeMillis()

        val exposedRefresh =
            ExposedToken.find { Tokens.refreshToken eq UUID.fromString(token) }.singleOrNull() ?: return null

        return if (exposedRefresh.expiresAt > currentTime) {
//            val refreshToken = UUID.randomUUID()
//            exposedRefresh.refreshToken = refreshToken
//
//            exposedRefresh.expiresAt = System.currentTimeMillis() + refreshLifeTime
            val access: String =
                if (exposedRefresh.userType == UserTypes.Company.name) createCompanyToken(exposedRefresh.userId)
                else createFreelToken(exposedRefresh.userId)

            Token(
                access = access,
                refresh = exposedRefresh.refreshToken.toString(),
                userType = exposedRefresh.userType
            )
        } else null
    }

    fun onForgetPassword(forgetPassword: ForgetPassword): UUID {
        val code = UUID.randomUUID()
        ExposedForgetPassword.new {
            this.code = code
            this.expiresAt = System.currentTimeMillis() + recoveryTime
            this.email = forgetPassword.login
        }
        return code
    }

    suspend fun logout(userId: Int, userType: String) {
        dbQuery {
            ExposedToken.find { (Tokens.userId eq userId) and (Tokens.userType eq userType) }.forEach {
                it.delete()
            }
        }
    }
}



