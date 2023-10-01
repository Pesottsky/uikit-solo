package com.heisy.schema

import com.heisy.plugins.dbQuery
import io.ktor.server.plugins.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
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
            rows = this.rows.toList().map { it.toDataClass() }
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


    suspend fun create(name: String, userId: Int): FreelsTable = dbQuery {
        val user = ExposedUser.findById(userId)!!
        if (user.tables.toList().size == 1) {
            throw BadRequestException("Таблица уже есть")
        }
        ExposedFreelsTable.new {
            this.name = name
            this.userId = user
        }.toDataClass()
    }

    suspend fun getByUserId(userId: Int): List<FreelsTable> {
        return dbQuery {
            val user = ExposedUser.findById(userId)
            user?.tables?.toList()?.map { it.toDataClass() } ?: listOf()
        }
    }

    suspend fun update(id: Int, table: FreelsTable) {
        dbQuery {
            FreelsTables.update({ FreelsTables.id eq id }) {
                it[name] = table.name
            }
        }
    }

    suspend fun delete(id: Int) {
        val user = ExposedUser.findById(id)!!
        user.tables.forEach {
            it.delete()
        }
    }
}



