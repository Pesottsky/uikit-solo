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
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class Token(
    @SerialName("access")
    val access: String? = null,

    @SerialName("refresh")
    val refresh: String,

    @SerialName("user_type")
    val  userType: String? = null
)


class ExposedToken(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedToken>(TokensService.Tokens)

    var userId by TokensService.Tokens.userId
    var refreshToken by TokensService.Tokens.refreshToken
    var userType by TokensService.Tokens.userType
    var expiresAt by TokensService.Tokens.expiresAt

    fun toDataClass(access: String) = run {
        Token(
            access = access,
            refresh = refreshToken.toString(),
            userType = userType
        )
    }
}


class TokensService(database: Database) {
    object Tokens : IntIdTable() {
        val userId = integer("userId")
        val refreshToken = uuid("refreshToken")
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

        val access: String = if (userType == UserTypes.User.type) createCompanyToken(userId, userType)
        else createFreelToken(userId, userType)

        val refreshToken = UUID.randomUUID()
        val exposedToken = dbQuery {
            ExposedToken.new {
                this.userId = userId
                this.refreshToken = refreshToken
                this.userType = userType
                expiresAt = currentTime + 1_000_000_00
            }
        }

        return exposedToken.toDataClass(access)
    }


    suspend fun refresh(token: String): Token? = dbQuery {
        val currentTime = System.currentTimeMillis()

        val refresh = UUID.fromString(token)
        val exposedRefresh =
            ExposedToken.find { Tokens.refreshToken eq refresh }
                .singleOrNull() ?: return@dbQuery null

        if (exposedRefresh.expiresAt > currentTime) {
            exposedRefresh.refreshToken = UUID.randomUUID()
            exposedRefresh.expiresAt = Date(System.currentTimeMillis() + 1_000_000_00).time
            val access: String = if (exposedRefresh.userType == UserTypes.User.type) createCompanyToken(
                exposedRefresh.userId,
                exposedRefresh.userType
            )
            else createFreelToken(exposedRefresh.userId, exposedRefresh.userType)

            exposedRefresh.toDataClass(access)
        } else {
            null
        }
    }
}



