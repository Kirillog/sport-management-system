package ru.emkn.kotlin.sms.objects

import kotlinx.datetime.LocalDateTime
import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable
import java.time.LocalTime

class Participant(
    val name: String,
    val surname: String,
    val birthdayYear: Int,
    val group: String,
    val team: String,
    val grade: String? = null
) : Readable, SingleLineWritable {
    var id: Int? = null
    var startTime: LocalTime? = null
    var finishTime: LocalTime? = null
    override fun toLine(): List<String> {
        val result = mutableListOf(name, surname, birthdayYear.toString(), group, team)
        if (grade != null)
            result.add(grade)
        return result
    }
}