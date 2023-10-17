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
data class Grade(
    @SerialName("level_key")
    val levelKey: Int,

    @SerialName("description")
    val description: String,
)

class ExposedGrade(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedGrade>(GradeService.GradeLevels)

    var levelKey by GradeService.GradeLevels.levelKey
    var description by GradeService.GradeLevels.description

    fun toDataClass(): Grade {
        return Grade(
            levelKey = this.levelKey,
            description = this.description,
        )
    }
}


class GradeService(database: Database) {
    object GradeLevels : IntIdTable() {
        val levelKey = integer("levelKey")
        val description = varchar("description", length = 50)
    }

    init {
        transaction(database) {
            SchemaUtils.create(GradeLevels)
            if (ExposedGrade.all().empty()) {
                ExposedGrade.new {
                    levelKey = 0
                    description = "Junior"
                }
                ExposedGrade.new {
                    levelKey = 1
                    description = "Middle"
                }
                ExposedGrade.new {
                    levelKey = 2
                    description = "Senior"
                }
                ExposedGrade.new {
                    levelKey = 3
                    description = "Lead"
                }
            }
        }
    }
}