package com.heisy.schema

import com.heisy.schema.UserService.Errors.emailBusy
import com.heisy.schema.UserService.Errors.wrongPair
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
data class Freel(
    @SerialName("login")
    val login: String,

    @SerialName("password")
    val password: String,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("last_name")
    val lastName: String? = null
)

class ExposedFreel(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedFreel>(FreelsService.Freels)

    var login by FreelsService.Freels.login
    var password by FreelsService.Freels.password
    var profile by ExposedProfile optionalReferencedOn FreelsService.Freels.profileId

    fun getProfile(): Profile? {
        return this.profile?.toDataClass()
    }
}

class FreelsService(database: Database) {
    object Freels : IntIdTable() {
        val login = varchar("login", length = 50)
        val password = varchar("password", length = 250)
        val profileId = reference("profileId", ProfilesService.Profiles, ReferenceOption.CASCADE).nullable()
    }

    init {
        transaction(database) {
            SchemaUtils.create(Freels)
        }
    }

    private fun checkLoginBusy(freel: Freel) {
        val checkLoginInFreels = ExposedFreel.find { Freels.login eq freel.login }.singleOrNull()
        val checkLoginInUsers = ExposedUser.find { UserService.Users.login eq freel.login }.singleOrNull()
        if (checkLoginInFreels != null || checkLoginInUsers != null) throw BadRequestException(emailBusy)
    }

    fun create(freel: Freel): ExposedFreel {
        checkLoginBusy(freel)
        return ExposedFreel.new {
            login = freel.login
            password = BCrypt.hashpw(freel.password, BCrypt.gensalt())
        }
    }

    fun checkAuth(freel: User): ExposedFreel? {
        val exposedFreel = ExposedFreel.find { Freels.login eq freel.login }
            .singleOrNull() ?: return null
        if (!BCrypt.checkpw(
                freel.password,
                exposedFreel.password
            )
        ) throw BadRequestException(wrongPair)

        return exposedFreel
    }
}
