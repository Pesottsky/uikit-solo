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

@Serializable
data class Loading(
    @SerialName("loading_key")
    val loadingKey: Int,

    @SerialName("description")
    val description: String,
)

class ExposedLoading(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedLoading>(LoadingService.Loading)

    var loadingKey by LoadingService.Loading.loadingKey
    var description by LoadingService.Loading.description

    fun toDataClass(): Loading {
        return Loading(
            loadingKey = this.loadingKey,
            description = this.description,
        )
    }
}


class LoadingService(database: Database) {
    object Loading : IntIdTable() {
        val loadingKey = integer("loadingKey")
        val description = varchar("loadingDescription", length = 50)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Loading)
            if (ExposedLoading.all().empty()) {
                ExposedLoading.new {
                    loadingKey = 0
                    description = "Свободен"
                }
                ExposedLoading.new {
                    loadingKey = 1
                    description = "Есть время"
                }
                ExposedLoading.new {
                    loadingKey = 2
                    description = "Занят"
                }
            }
        }
    }
}