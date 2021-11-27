package ru.emkn.kotlin.sms.objects

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable
import java.time.format.DateTimeFormatter

data class Event(val name: String, val date: LocalDate) : Readable, SingleLineWritable {
    override fun toLine(): List<String?> {
        val pattern = "dd.MM.yyyy"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return listOf(name, date.toJavaLocalDate().format(formatter))
    }
}
