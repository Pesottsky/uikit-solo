package com.heisy.schema

import com.heisy.schema.ProfilesService.Errors.notFoundTextError
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

    @SerialName("first_name")
    val firstName: String,

    @SerialName("last_name")
    val lastName: String? = null,

    @SerialName("price")
    val price: Int? = null,

    @SerialName("portfolio")
    val portfolio: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("summary")
    val summary: String? = null,

    @SerialName("skills")
    val skills: String? = null,

    @SerialName("telegram")
    val telegram: String? = null,

    @SerialName("experience")
    val experience: String? = null,

    @SerialName("link")
    val link: String? = null,

    @SerialName("grade")
    val grade: Grade? = null,

    @SerialName("loading")
    val loading: Loading? = null
)

class ExposedProfile(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedProfile>(ProfilesService.Profiles)

    var firstName by ProfilesService.Profiles.firstName
    var lastName by ProfilesService.Profiles.lastName
    var price by ProfilesService.Profiles.price
    var portfolio by ProfilesService.Profiles.portfolio
    var experience by ProfilesService.Profiles.experience
    var email by ProfilesService.Profiles.email
    var summary by ProfilesService.Profiles.summary
    var skills by ProfilesService.Profiles.skills
    var telegram by ProfilesService.Profiles.telegram
    var link by ProfilesService.Profiles.link
    var grade by ExposedGrade optionalReferencedOn ProfilesService.Profiles.grade
    var loading by ExposedLoading optionalReferencedOn ProfilesService.Profiles.loading

    val freel by ExposedFreel referrersOn FreelsService.Freels.profileId

    fun toDataClass(): Profile {
        return Profile(
            id = this.id.value,
            firstName = this.firstName,
            lastName = this.lastName,
            price = this.price,
            portfolio = this.portfolio,
            experience = this.experience,
            email = this.email,
            summary = this.summary,
            skills = this.skills,
            telegram = this.telegram,
            link = this.link,
            grade = this.grade?.toDataClass(),
            loading = this.loading?.toDataClass()
        )
    }
}


class ProfilesService(database: Database) {
    object Profiles : IntIdTable() {
        // Пользователь, к которому таблица привязана
        val firstName = varchar("firstName", length = 128)
        val lastName = varchar("lastName", length = 128).nullable()
        val price = integer("price").nullable()
        val portfolio = varchar("portfolio", length = 1024).nullable()
        val email = varchar("email", length = 250).nullable()
        val summary = varchar("summary", length = 1024).nullable()
        val link = varchar("link", length = 128)
        val skills = varchar("skills", length = 1024).nullable()
        val telegram = varchar("telegram", length = 128).nullable()
        val experience = varchar("experience", length = 1024).nullable()
        val grade = reference("grade", GradeService.GradeLevels).nullable()
        val loading = reference("loading", LoadingService.Loading).nullable()

    }

    object Errors {
        const val notFoundTextError = "Профиль не найден"
    }

    init {
        transaction(database) {
            SchemaUtils.create(Profiles)
        }
    }

    fun get(id: Int): ExposedProfile? = ExposedProfile.findById(id)


    fun create(profile: Profile): ExposedProfile {
        return ExposedProfile.new {
            firstName = profile.firstName
            lastName = profile.lastName
            price = profile.price
            portfolio = profile.portfolio
            experience = profile.experience
            email = profile.email
            summary = profile.summary
            skills = profile.skills
            telegram = profile.telegram
            link =
                if (profile.lastName == null) "${profile.firstName}?profileId=${this.id.value}" else "${profile.firstName}${profile.lastName}?profileId=${this.id.value}"
        }
    }

    fun updateByFreel(freelId: Int, profile: Profile): ExposedProfile {
        var exposedGrade: ExposedGrade? = null
        if (profile.grade != null) {
            exposedGrade =
                ExposedGrade.find { GradeService.GradeLevels.levelKey eq profile.grade.levelKey }.singleOrNull()
        }
        val exposedProfile = ExposedFreel.findById(freelId)?.profile ?: throw NotFoundException(notFoundTextError)
        if (exposedProfile.id.value != profile.id) throw NotFoundException(notFoundTextError)
        with(exposedProfile) {
            firstName = profile.firstName
            lastName = profile.lastName
            price = profile.price
            portfolio = profile.portfolio
            experience = profile.experience
            email = profile.email
            summary = profile.summary
            skills = profile.skills
            telegram = profile.telegram
            grade = exposedGrade
        }
        return exposedProfile
    }

    fun updateLoading(freelId: Int, loading: Loading): ExposedProfile {
        val exposedProfile = ExposedFreel.findById(freelId)?.profile ?: throw NotFoundException(notFoundTextError)
        val exposedLoading =
            ExposedLoading.find { LoadingService.Loading.loadingKey eq loading.loadingKey }.singleOrNull()
                ?: throw NotFoundException(notFoundTextError)
        exposedProfile.loading = exposedLoading
        return exposedProfile
    }

    fun updateByCompany(exposedProfile: ExposedProfile, profile: Profile): ExposedProfile {
        var exposedGrade: ExposedGrade? = null
        if (profile.grade != null) {
            exposedGrade =
                ExposedGrade.find { GradeService.GradeLevels.levelKey eq profile.grade.levelKey }.singleOrNull()
        }
        with(exposedProfile) {
            firstName = profile.firstName
            lastName = profile.lastName
            price = profile.price
            portfolio = profile.portfolio
            experience = profile.experience
            email = profile.email
            summary = profile.summary
            skills = profile.skills
            telegram = profile.telegram
            grade = exposedGrade
        }
        return exposedProfile
    }
}



