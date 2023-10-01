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
    var profileId by  ExposedProfile referencedOn CommentService.Comments.profileId

    fun toDataClass(): Comment {
        return Comment(
            comment = this.comment,
            profileId = this.profileId.id.value,
            id = this.id.value
        )
    }
}

class CommentService(database: Database) {
    object Comments : IntIdTable() {
        val profileId = reference("profileId", ProfilesService.Profiles)
        val comment = varchar("about", length = 4128)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Comments)
        }
    }

    suspend fun create(comment: Comment): Comment = dbQuery {
        val exposedProfile = ExposedProfile.findById(comment.profileId) ?: throw NotFoundException("Профиль не найден")
        ExposedComment.new {
            this.comment = comment.comment
            this.profileId = exposedProfile
        }.toDataClass()
    }
}
