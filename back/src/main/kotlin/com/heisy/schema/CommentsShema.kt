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
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Comment(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("comment")
    val comment: String,

    @SerialName("profile_id")
    val profileId: Int,
)

class ExposedComment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedComment>(CommentService.Comments)

    var comment by CommentService.Comments.comment
    var profile by ExposedProfile referencedOn CommentService.Comments.profile
    var user by ExposedUser referencedOn CommentService.Comments.user

    fun toDataClass(): Comment {
        return Comment(
            comment = this.comment,
            profileId = this.profile.id.value,
            id = this.id.value
        )
    }
}

class CommentService(database: Database) {
    object Comments : IntIdTable() {
        val profile = reference("profileId", ProfilesService.Profiles, ReferenceOption.CASCADE)
        val comment = varchar("comment", length = 4128)
        val user = reference("userId", UserService.Users, ReferenceOption.CASCADE)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Comments)
        }
    }

    suspend fun get(profileId: Int, userId: Int): Comment? = dbQuery {
        ExposedComment.find { (Comments.user eq userId) and (Comments.profile eq profileId) }.singleOrNull()
            ?.toDataClass()
    }

    suspend fun create(comment: Comment, userId: Int): Comment = dbQuery {
        val exposedComment =
            ExposedComment.find { (Comments.user eq userId) and (Comments.profile eq comment.profileId) }.singleOrNull()
        if (exposedComment == null) {
            val exposedProfile =
                ExposedProfile.findById(comment.profileId) ?: throw NotFoundException("Профиль не найден")
            val exposeUser = ExposedUser.findById(userId) ?: throw NotFoundException("Профиль не найден")
            ExposedComment.new {
                this.comment = comment.comment
                this.profile = exposedProfile
                this.user = exposeUser
            }.toDataClass()
        } else {
            exposedComment.comment = comment.comment
            exposedComment.toDataClass()
        }
    }
}
