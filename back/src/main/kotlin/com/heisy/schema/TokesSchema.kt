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
import org.mindrot.jbcrypt.BCrypt
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


class TokensService(database: Database) {
    object Tokens : IntIdTable() {
        val userId = integer("userId")
        val refreshToken = varchar("refreshToken", length = 1024)
        val userType = varchar("userType", 16)
        val expiresAt = long("expiresAt")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tokens)
        }
    }

    suspend fun generateTokenPair(userId: Int, userType: String): Token {
        val currentTime = System.currentTimeMillis()

        val access: String = if (userType == UserTypes.User.type) createCompanyToken(userId)
        else createFreelToken(userId)

        val refreshToken = UUID.randomUUID().toString()
        val encryptedToken = BCrypt.hashpw(refreshToken, BCrypt.gensalt())
        dbQuery {
            ExposedToken.new {
                this.userId = userId
                this.refreshToken = encryptedToken
                this.userType = userType
                expiresAt = currentTime + 1_000_000_00
            }
        }

        return Token(
            access = access,
            refresh = refreshToken,
            userType = userType
        )
    }


    suspend fun refresh(token: String, userId: Int, userType: String): Token? = dbQuery {
        val currentTime = System.currentTimeMillis()


        val exposedRefresh =
            ExposedToken.find { (Tokens.userId eq userId) and (Tokens.userType eq userType) }
                .singleOrNull() ?: return@dbQuery null

        if (!BCrypt.checkpw(
                token,
                exposedRefresh.refreshToken
            )
        ) return@dbQuery null

        if (exposedRefresh.expiresAt > currentTime) {
            val refreshToken = UUID.randomUUID().toString()
            exposedRefresh.refreshToken = BCrypt.hashpw(refreshToken, BCrypt.gensalt())
            exposedRefresh.expiresAt = Date(System.currentTimeMillis() + 1_000_000_00).time
            val access: String =
                if (exposedRefresh.userType == UserTypes.User.type) createCompanyToken(exposedRefresh.userId)
                else createFreelToken(exposedRefresh.userId)

            return@dbQuery Token(
                access = access,
                refresh = refreshToken,
                userType = userType
            )
        } else {
            null
        }
    }

    suspend fun logout(userId: Int, userType: String) {
        dbQuery {
            ExposedToken.find { (Tokens.userId eq userId) and (Tokens.userType eq userType) }.forEach {
                it.delete()
            }
        }
    }
}



