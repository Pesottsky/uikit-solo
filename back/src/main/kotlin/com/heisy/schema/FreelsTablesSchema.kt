package com.heisy.schema

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
data class FreelsTable(
    @SerialName("name")
    val name: String,

    @SerialName("id")
    val id: Int? = null,

    @SerialName("rows")
    val rows: List<FreelsRow>? = null
)

class ExposedFreelsTable(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedFreelsTable>(FreelsTablesService.FreelsTables)

    var name by FreelsTablesService.FreelsTables.name
    var userId by ExposedUser referencedOn FreelsTablesService.FreelsTables.userId

    val rows by ExposedFreelsRow referrersOn FreelsRowsService.FreelsRows.tableId


    fun toDataClass(): FreelsTable {
        return FreelsTable(
            name = this.name,
            id = this.id.value,
            rows = this.rows.map { it.toDataClass() }
        )
    }
}


class FreelsTablesService(database: Database) {
    object FreelsTables : IntIdTable() {
        // Пользователь, к которому таблица привязана
        val userId = reference("userID", UserService.Users, ReferenceOption.CASCADE)

        val name = varchar("name", 50)
    }

    init {
        transaction(database) {
            SchemaUtils.create(FreelsTables)
        }
    }


    fun create(name: String, userId: Int): ExposedFreelsTable {
        val user = ExposedUser.findById(userId) ?: throw NotFoundException()
        if (user.tables.count() > 0) throw BadRequestException("Таблица уже есть")

        return ExposedFreelsTable.new {
            this.name = name
            this.userId = user
        }
    }

    fun getByUserId(userId: Int): List<ExposedFreelsTable> {
        val user = ExposedUser.findById(userId) ?: throw NotFoundException()
        return user.tables.toList()
    }

    fun update(userId: Int, table: FreelsTable): ExposedFreelsTable {
        val exposedTable = ExposedUser.findById(userId)?.tables?.firstOrNull()
            ?: throw NotFoundException(FreelsRowsService.Errors.notFoundError)
        exposedTable.name = table.name
        return exposedTable
    }

    fun delete(userId: Int) {
        ExposedUser.findById(userId)?.tables?.firstOrNull()?.delete()
            ?: throw NotFoundException(FreelsRowsService.Errors.notFoundError)
    }
}



