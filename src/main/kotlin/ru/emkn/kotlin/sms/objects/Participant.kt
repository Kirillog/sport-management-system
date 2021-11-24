package ru.emkn.kotlin.sms.objects

import kotlinx.datetime.LocalDateTime
import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable

class Participant(
    val name: String,
    val surname: String,
    val birthdayYear: Int,
    val group: String,
    val team: String,
    val grade: String? = null
) : Readable, SingleLineWritable {
    private val id: Int? = null
    private val startTime: LocalDateTime? = null
    private val finishTime: LocalDateTime? = null
    override fun toLine(): List<String> {
        val result = mutableListOf(name, surname, birthdayYear.toString(), group, team)
        if (grade != null)
            result.add(grade)
        return result
    }
}