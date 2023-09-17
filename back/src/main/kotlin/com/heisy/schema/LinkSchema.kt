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
import java.util.*

@Serializable
data class Link(
    @SerialName("link")
    val link: String? = null,

    @SerialName("isRegister")
    val isRegister: Boolean? = null,

    @SerialName("id")
    val id: Int? = null
)


class ExposedLink(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedLink>(LinksService.Links)

    var link by LinksService.Links.link
    var isRegister by LinksService.Links.isRegister

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


    suspend fun create(): ExposedLink = dbQuery {
        ExposedLink.new {
            this.link = UUID.randomUUID()
        }
    }


    suspend fun update(id: Int, isRegister: Boolean) {
        dbQuery {
            val link = ExposedLink.findById(id)
            if (link != null) {
                link.isRegister = isRegister
            } else {
                throw NotFoundException("Ссылка не найдена")
            }
        }
    }

    suspend fun find(uuid: String): ExposedLink? = run {
        val convertUUID = UUID.fromString(uuid)
        return dbQuery {
            ExposedLink.find { (Links.link eq convertUUID) }.singleOrNull()
        }
    }


}



