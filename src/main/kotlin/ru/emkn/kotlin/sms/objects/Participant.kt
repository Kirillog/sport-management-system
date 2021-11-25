package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
        val result = mutableListOf<String>()

        //TODO("Возможно добавить во writer функцию для nullable типов")

        id?.let {
            result.add(it.toString())
        }

        result.addAll(mutableListOf(name, surname, birthdayYear.toString(), group, team))

        if (grade != null) result.add(grade)

        startTime?.let {
            result.add(it.format(DateTimeFormatter.ISO_LOCAL_TIME))
        }

        finishTime?.let {
            result.add(it.format(DateTimeFormatter.ISO_LOCAL_TIME))
        }

        return result
    }
}