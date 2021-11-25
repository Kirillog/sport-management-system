package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Participant(
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

    override fun toLine(): List<String?> {
        val result = mutableListOf<String?>()

        result.add(id?.toString())
        result.addAll(mutableListOf(name, surname, birthdayYear.toString(), group, team))

        if (grade != null) result.add(grade)

        result.add(startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME))
        result.add(finishTime?.format(DateTimeFormatter.ISO_LOCAL_TIME))

        return result
    }
}