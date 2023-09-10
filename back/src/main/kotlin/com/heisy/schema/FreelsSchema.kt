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
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class Freel(
    @SerialName("login")
    val login: String,

    @SerialName("password")
    val password: String
)

class ExposedFreel(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedFreel>(FreelsService.Freels)

    var login by FreelsService.Freels.login
    var password by FreelsService.Freels.password
    var profile by ExposedProfile optionalReferencedOn FreelsService.Freels.profileId

    fun toDataClass(): Freel {
        return Freel(
            login = this.login,
            password = this.password
        )
    }

    fun getProfile(): Profile? {
        return this.profile?.toDataClass()
    }
}

class FreelsService(database: Database) {
    object Freels : IntIdTable() {
        val login = varchar("login", length = 50)
        val password = varchar("password", length = 250)
        val profileId = reference("profileId", ProfilesService.Profiles).nullable()
    }

    init {
        transaction(database) {
            SchemaUtils.create(Freels)
        }
    }

    suspend fun get(id: Int): ExposedFreel = dbQuery {
        ExposedFreel.findById(id) ?: throw NotFoundException("Фрилансер не найден")
    }

    suspend fun create(freel: Freel): ExposedFreel? = dbQuery {
        val checkLogin = ExposedFreel.find { Freels.login eq freel.login }.singleOrNull()

        if (checkLogin == null) {
            ExposedFreel.new {
                login = freel.login
                password = BCrypt.hashpw(freel.password, BCrypt.gensalt())
            }
        } else {
            null
        }


    }

    suspend fun update(userId: Int, row: ExposedFreelsRow) = dbQuery {
        val freel = ExposedFreel.findById(userId)
        if (freel != null) {
            freel.profile = row.profile
        } else {
            throw NotFoundException("Пользователь не найлен")
        }
    }

    suspend fun checkAuth(freel: Freel): Int {
        return dbQuery {
            val exposedUser = ExposedFreel.find { Freels.login eq freel.login }
                .singleOrNull() ?: throw BadRequestException("Неправильный логин или пароль")
            if (!BCrypt.checkpw(
                    freel.password,
                    exposedUser.password
                )
            ) throw BadRequestException("Неправильный логин или пароль")
            exposedUser.id.value
        }
    }

}
