package com.heisy.schema

import com.heisy.utils.StringUtils
import io.ktor.server.application.*
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
import java.util.*

@Serializable
data class Link(
    @SerialName("link")
    val link: String,

    @SerialName("is_register")
    val isRegister: Boolean? = null,

    @SerialName("id")
    val id: Int,

    @SerialName("email")
    val email: String? = null,

    @SerialName("is_email_sending")
    val isEmailSending: Boolean? = null
)

@Serializable
data class Invite(
    @SerialName("link_id")
    val linkId: Int,

    @SerialName("email")
    val email: String,

    @SerialName("row_id")
    val rowId: Int
)


class ExposedLink(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedLink>(LinksService.Links)

    var link by LinksService.Links.link
    var isRegister by LinksService.Links.isRegister
    var isEmailSending by LinksService.Links.isEmailSending
    var email by LinksService.Links.email
    val rows by ExposedFreelsRow optionalReferrersOn FreelsRowsService.FreelsRows.linkId


    fun toDataClass() = run {
        Link(
            id = this.id.value,
            link = StringUtils.linkToFront(this.link.toString()),
            isRegister = this.isRegister ?: false,
            isEmailSending = this.isEmailSending ?: false,
            email = this.email
        )
    }
}


class LinksService(database: Database) {
    object Links : IntIdTable() {


        val link = uuid("link")
        val isRegister = bool("isRegister").nullable()
        val isEmailSending = bool("isEmailSending").nullable()
        val email = varchar("email", length = 256).nullable()
    }

    init {
        transaction(database) {
            SchemaUtils.create(Links)
        }
    }


    fun create(): ExposedLink =
        ExposedLink.new {
            this.link = UUID.randomUUID()
        }


    fun update(invite: Invite): ExposedLink {
        val exposedLink = ExposedLink.findById(invite.linkId) ?: throw NotFoundException()
        exposedLink.apply {
            this.email = invite.email
        }
        return exposedLink
    }

    fun onEmailSending(linkId: Int): ExposedLink {
        val exposedLink = ExposedLink.findById(linkId) ?: throw NotFoundException()
        exposedLink.apply {
            this.isEmailSending = true
        }
        return exposedLink
    }

    fun findByUUID(uuid: String): ExposedLink? {
        val convertUUID = UUID.fromString(uuid)
        return ExposedLink.find { (Links.link eq convertUUID) }.singleOrNull()
    }
}



