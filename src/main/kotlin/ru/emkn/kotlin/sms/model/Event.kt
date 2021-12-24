package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.MAX_TEXT_FIELD_SIZE
import ru.emkn.kotlin.sms.io.SingleLineWritable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object EventTable : IntIdTable("event") {
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
    val date: Column<LocalDate> = date("date")
}

/**
 * Class for storing metadata about the competition.
 */
class Event(id: EntityID<Int>) : IntEntity(id), SingleLineWritable {

    companion object : IntEntityClass<Event>(EventTable) {
        fun create(name: String, date: LocalDate): Event {
            return if (Event.all().empty()) {
                Event.new {
                    this.name = name
                    this.date = date
                }
            } else {
                Event.all().first()
            }
        }
    }

    var name by EventTable.name
    var date by EventTable.date

    override fun toLine(): List<String?> {
        val pattern = "dd.MM.yyyy"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return listOf(name, date.format(formatter))
    }

    fun change(name: String, date: LocalDate) {
        this.name = name
        this.date = date
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (name != other.name) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}