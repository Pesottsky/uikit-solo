package com.heisy.schema

import com.heisy.schema.FreelsRowsService.Errors.notEnoughtRights
import com.heisy.schema.FreelsRowsService.Errors.notFoundError
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

    fun toDataClass() = FreelsRow(
        profile = profile.toDataClass(),
        tableId = table.id.value,
        link = link?.toDataClass(),

        // У профиля есть владелец
        canChange = profile.freel.empty()
    )

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

    object Errors {
        const val notFoundError = "Таблица не найдена"
        const val notEnoughtRights = "Вы не можете редактировать профиль"
    }

    fun create(userId: Int, profile: ExposedProfile): ExposedFreelsRow {
        val table = ExposedUser.findById(userId)?.tables?.firstOrNull() ?: throw NotFoundException(notFoundError)
        return ExposedFreelsRow.new {
            this.table = table
            this.profile = profile
        }
    }

    /**
     * Есть ли у профиля владелец
     */
    fun checkForOwner(exposedRow: ExposedFreelsRow): ExposedFreelsRow {
        // У профиля есть владелец
        if (!exposedRow.profile.freel.empty()) throw BadRequestException(notEnoughtRights)
        return exposedRow
    }

    /**
     * Принадлоежит ли запись пользователю
     */
    fun checkRowForUpdate(rowId: Int, userId: Int): ExposedFreelsRow {

        val table = ExposedUser.findById(userId)?.tables?.firstOrNull() ?: throw NotFoundException(notFoundError)

        return table.rows.find { it.id.value == rowId } ?: throw NotFoundException(notFoundError)
    }


    fun updateLink(exposedRow: ExposedFreelsRow, link: ExposedLink): ExposedFreelsRow {
        exposedRow.link = link
        return exposedRow
    }

    fun delete(row: ExposedFreelsRow) {
        row.delete()
    }
}



