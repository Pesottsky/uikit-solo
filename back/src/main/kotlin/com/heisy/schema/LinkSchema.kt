package com.heisy.schema

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
    val id: Int
)


class ExposedLink(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedLink>(LinksService.Links)

    var link by LinksService.Links.link
    var isRegister by LinksService.Links.isRegister
    val rows by ExposedFreelsRow optionalReferrersOn FreelsRowsService.FreelsRows.linkId


    fun toDataClass() = run {
        Link(
            id = this.id.value,
            link = this.link.toString(),
            isRegister = this.isRegister,
        )
    }
}


class LinksService(database: Database) {
    object Links : IntIdTable() {


        val link = uuid("link")
        val isRegister = bool("isRegister").nullable()

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


    fun findByUUID(uuid: String): ExposedLink? {
        val convertUUID = UUID.fromString(uuid)
        return ExposedLink.find { (Links.link eq convertUUID) }.singleOrNull()
    }
}



