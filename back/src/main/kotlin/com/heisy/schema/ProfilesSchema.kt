package com.heisy.schema

import com.heisy.plugins.dbQuery
import io.ktor.server.plugins.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Profile(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("firstName")
    val firstName: String,

    @SerialName("lastName")
    val lastName: String,

    @SerialName("surname")
    val surname: String? = null,

    @SerialName("price")
    val price: Int? = null,

    @SerialName("portfolio")
    val portfolio: String? = null,

    @SerialName("comments")
    val comments: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("summary")
    val summary: String? = null,

    @SerialName("skills")
    val skills: String? = null,

    @SerialName("telegram")
    val telegram: String? = null
)

class ExposedProfile(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedProfile>(ProfilesService.Profiles)

    var firstName by ProfilesService.Profiles.firstName
    var lastName by ProfilesService.Profiles.lastName
    var surname by ProfilesService.Profiles.surname
    var price by ProfilesService.Profiles.price
    var portfolio by ProfilesService.Profiles.portfolio
    var comments by ProfilesService.Profiles.comments
    var email by ProfilesService.Profiles.email
    var summary by ProfilesService.Profiles.summary
    var skills by ProfilesService.Profiles.skills
    var telegram by ProfilesService.Profiles.telegram

    val freel by ExposedFreel optionalReferrersOn FreelsService.Freels.profileId

    fun toDataClass() = run {
        Profile(
            id = this.id.value,
            firstName = this.firstName,
            lastName = this.lastName,
            surname = this.surname,
            price = this.price,
            portfolio = this.portfolio,
            comments = this.comments,
            email = this.email,
            summary = this.summary,
            skills = this.skills,
            telegram = this.telegram,
        )
    }
}


class ProfilesService(database: Database) {
    object Profiles : IntIdTable() {
        // Пользователь, к которому таблица привязана
        val firstName = varchar("firstName", length = 128)
        val lastName = varchar("lastName", length = 128)
        val surname = varchar("surname", length = 128).nullable()
        val price = integer("price").nullable()
        val portfolio = varchar("portfolio", length = 250).nullable()
        val comments = varchar("comments", length = 250).nullable()
        val email = varchar("email", length = 250).nullable()
        val summary = varchar("summary", length = 1024).nullable()
        val skills = varchar("skills", length = 1024).nullable()
        val telegram = varchar("telegram", length = 128).nullable()

        //Todo ссылка на грейд, загрузку и решить что с опытом

    }

    init {
        transaction(database) {
            SchemaUtils.create(Profiles)
        }
    }

    suspend fun get(id: Int) = dbQuery {
        ExposedProfile.findById(id) ?: throw NotFoundException("Профиль не найден")
    }


    suspend fun create(profile: Profile): ExposedProfile = dbQuery {
        ExposedProfile.new {
            firstName = profile.firstName
            lastName = profile.lastName
            surname = profile.surname
            price = profile.price
            portfolio = profile.portfolio
            comments = profile.comments
            email = profile.email
            summary = profile.summary
            skills = profile.skills
            telegram = profile.telegram
        }
    }

    suspend fun update(profile: Profile) {
        dbQuery {
            val profileResult = ExposedProfile.findById(profile.id!!)
            if (profileResult != null) {
                with(profileResult) {
                    firstName = profile.firstName
                    lastName = profile.lastName
                    surname = profile.surname
                    price = profile.price
                    portfolio = profile.portfolio
                    comments = profile.comments
                    email = profile.email
                    summary = profile.summary
                    skills = profile.skills
                    telegram = profile.telegram
                }
            } else {
                throw NotFoundException("Такой профиль не найден")
            }
        }
    }

}



