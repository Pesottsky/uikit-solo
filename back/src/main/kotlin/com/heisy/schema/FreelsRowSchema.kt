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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class FreelsRow(
    @SerialName("profile")
    val profile: Profile,

    @SerialName("link")
    val link: Link? = null,

    @SerialName("table_id")
    val tableId: Int,

    @SerialName("can_change")
    val canChange: Boolean
)

class ExposedFreelsRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedFreelsRow>(FreelsRowsService.FreelsRows)

    var table by ExposedFreelsTable referencedOn FreelsRowsService.FreelsRows.tableId
    var profile by ExposedProfile referencedOn FreelsRowsService.FreelsRows.profileId
    var link by ExposedLink optionalReferencedOn FreelsRowsService.FreelsRows.linkId

    fun toDataClass() = run {
        FreelsRow(
            profile = profile.toDataClass(),
            tableId = table.id.value,
            link = link?.toDataClass(),
            canChange = profile.freel.singleOrNull() == null
        )
    }
}


class FreelsRowsService(database: Database) {
    object FreelsRows : IntIdTable() {

        // Профиль
        val profileId = reference("profileId", ProfilesService.Profiles, ReferenceOption.CASCADE)

        // Ссылка для приглашения
        val linkId = reference("linkId", LinksService.Links).nullable()

        // Таблица, которой принадлежит запись
        val tableId = reference("tableId", FreelsTablesService.FreelsTables, ReferenceOption.CASCADE)

    }

    init {
        transaction(database) {
            SchemaUtils.create(FreelsRows)
        }
    }


    suspend fun create(tableId: Int, profile: ExposedProfile): ExposedFreelsRow = dbQuery {
        val table = ExposedFreelsTable.findById(tableId)

        if (table != null) {
            ExposedFreelsRow.new {
                this.table = table
                this.profile = profile
            }
        } else {
            throw NotFoundException("Таблица с таким id не найдена")
        }

    }

    suspend fun updateProfile(id: Int, profile: Profile) {
        dbQuery {
            val row = ExposedFreelsRow.findById(id) ?: throw NotFoundException("Запись не найдена")

            // Проверить есть ли у профиля владелец
            val profileOwner = row.profile.freel.singleOrNull()
            if (profileOwner != null) throw NotFoundException("Вы не можете редактировать профиль")

            val exposedProfile =
                profile.id?.let { ExposedProfile.findById(it) } ?: throw NotFoundException("Профиля с таким id нет")
            row.profile = exposedProfile

        }
    }


    suspend fun updateLink(id: Int, link: ExposedLink) {
        dbQuery {
            val row = ExposedFreelsRow.findById(id)
            if (row != null) {
                row.link = link
            } else {
                throw NotFoundException("Запись не найдена")
            }
        }
    }

    suspend fun findByLink(link: ExposedLink) =
        dbQuery {
            ExposedFreelsRow.find { FreelsRows.linkId eq link.id }.singleOrNull()
        }

    suspend fun delete(id: Int) {
        dbQuery {
            FreelsRows.deleteWhere { FreelsRows.id eq id }
        }

    }
}



