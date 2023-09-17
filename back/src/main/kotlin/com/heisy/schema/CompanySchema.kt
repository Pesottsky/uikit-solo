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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

@Serializable
data class Company(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("link")
    val link: String?,

    @SerialName("about")
    val about: String?
)

class ExposedCompany(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedCompany>(CompanyService.Companies)

    var name by CompanyService.Companies.name
    var link by CompanyService.Companies.link
    val about by CompanyService.Companies.about

    fun toDataClass(): Company {
        return Company(
            name = this.name,
            link = this.link,
            about = this.about,
            id = this.id.value
        )
    }
}

class CompanyService(database: Database) {
    object Companies : IntIdTable() {
        val name = varchar("name", length = 250)
        val link = varchar("link", length = 250).nullable()
        val about = varchar("about", length = 1024).nullable()
    }

    init {
        transaction(database) {
            SchemaUtils.create(Companies)
        }
    }

    suspend fun create(name: String): Int = dbQuery {
        ExposedCompany.new {
            this.name = name
        }.id.value
    }

    suspend fun getAll() = dbQuery {
        ExposedCompany.all()
    }

    suspend fun read(id: Int): ExposedCompany {
        return dbQuery {
            ExposedCompany.findById(id) ?: throw NotFoundException("Комания не найдена")
        }
    }

    suspend fun update(id: Int, company: Company) {
        dbQuery {
            Companies.update({ Companies.id eq id }) {
                it[name] = company.name
                it[about] = company.about
                it[link] = company.link
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Companies.deleteWhere { Companies.id.eq(id) }
        }
    }
}
