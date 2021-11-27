package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Duration

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
    var timeStamps: List<TimeStamp>? = null
    var finishData: FinishData? = null

    constructor(name : String, surname: String, birthdayYear: Int, group: String, team: String, grade: String?, participantId : Int, startTime: LocalTime) : this(name, surname, birthdayYear, group, team, grade) {
        this.id = participantId
        this.startTime = startTime
    }

    data class FinishData(val time: LocalTime, val place: Int, val laggingFromLeader: Duration)


    override fun toLine(): List<String?> = listOf (
        id?.toString(),
        name,
        surname,
        birthdayYear.toString(),
        group,
        team,
        grade,
        startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
        finishData?.time?.format(DateTimeFormatter.ISO_LOCAL_TIME),
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

    fun toLineFinished() = listOf(
        finishData?.place.toString(),
        id?.toString(),
        name,
        surname,
        birthdayYear.toString(),
        grade,
        startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
        finishData?.time?.format(DateTimeFormatter.ISO_LOCAL_TIME),
        finishData?.laggingFromLeader.toString()
    )

}