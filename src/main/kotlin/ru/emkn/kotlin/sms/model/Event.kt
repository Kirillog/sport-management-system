package ru.emkn.kotlin.sms.model

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


object EventTable : Table("event") {
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
    val date: Column<LocalDate> = date("date")
}

/**
 * Class for storing metadata about the competition.
 */
data class Event(var name: String, var date: LocalDate) : SingleLineWritable {
    override fun toLine(): List<String?> {
        val pattern = "dd.MM.yyyy"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return listOf(name, date.format(formatter))
    }

    fun change(name: String, date: LocalDate) {
        this.name = name
        this.date = date
    }

    init {
        transaction {
            EventTable.deleteAll()
            EventTable.insert {
                it[this.name] = this@Event.name
                it[this.date] = this@Event.date
            }
        }
    }
}
