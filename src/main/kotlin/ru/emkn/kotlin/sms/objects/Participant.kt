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

    override fun toLine(): List<String?> = listOf (
        id?.toString(),
        name,
        surname,
        birthdayYear.toString(),
        group,
        team,
        grade,
        startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
        finishTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
    )

    fun toLineWithoutTeam() = listOf(
        id?.toString(),
        name,
        surname,
        birthdayYear.toString(),
        group,
        grade,
        startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME)
    )

    fun toLineWithoutGroup() = listOf(
        id?.toString(),
        name,
        surname,
        birthdayYear.toString(),
        team,
        grade,
        startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME)
    )

}