package com.heisy.schema

import com.heisy.schema.CompanyService.Errors.notFoundTextError
import io.ktor.server.plugins.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Company(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("link")
    val link: String? = null,

    @SerialName("about")
    val about: String? = null
)

class ExposedCompany(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedCompany>(CompanyService.Companies)

    var name by CompanyService.Companies.name
    var link by CompanyService.Companies.link
    var about by CompanyService.Companies.about

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
        val about = varchar("about", length = 2056).nullable()
    }

    object Errors {
        const val notFoundTextError = "Компания не найдена"
    }

    init {
        transaction(database) {
            SchemaUtils.create(Companies)
        }
    }

    fun create(name: String): ExposedCompany = ExposedCompany.new {
        this.name = name
    }


    fun getAll(): SizedIterable<ExposedCompany> = ExposedCompany.all()


    fun get(id: Int): ExposedCompany {
        return ExposedCompany.findById(id)
            ?: throw NotFoundException(notFoundTextError)
    }

    fun getByUserId(id: Int): ExposedCompany {
        return ExposedUser.findById(id)?.company ?: throw NotFoundException(notFoundTextError)
    }

    fun update(userId: Int, company: Company): ExposedCompany {
        val exposedCompany = ExposedUser.findById(userId)?.company ?: throw NotFoundException(notFoundTextError)
        if (exposedCompany.id.value != company.id) throw NotFoundException(notFoundTextError)

        exposedCompany.apply {
            this.name = company.name
            this.about = company.about
            this.link = company.link
        }
        return exposedCompany
    }
}
