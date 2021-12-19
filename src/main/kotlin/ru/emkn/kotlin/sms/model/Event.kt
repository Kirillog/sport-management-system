package ru.emkn.kotlin.sms.model

import ru.emkn.kotlin.sms.io.SingleLineWritable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
}
