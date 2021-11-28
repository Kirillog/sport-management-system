package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration

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
    var finishTime: LocalTime? = null
    var place: Place? = null
    @OptIn(ExperimentalTime::class)
    val time : Duration?
        get() {
            if (finishTime == null || startTime == null)
                return null
            return Duration.between(finishTime, startTime)
        }

    constructor(
        name: String,
        surname: String,
        birthdayYear: Int,
        group: String,
        team: String,
        grade: String?,
        participantId: Int,
        startTime: LocalTime
    ) : this(name, surname, birthdayYear, group, team, grade) {
        this.id = participantId
        this.startTime = startTime
    }

    fun getId() : Int {
        val id = this.id
        requireNotNull(id) {"Id have to be set up"}
        return id
    }

    @JvmName("getStartTime1")
    fun getStartTime() : LocalTime {
        val time = startTime
        requireNotNull(time) {"Start time have to be set up"}
        return time
    }

    data class Place(val number: Int, val laggingFromLeader: Duration)

    @OptIn(ExperimentalTime::class)
    override fun toLine(): List<String?> = listOf(
        place?.number.toString(),
        id?.toString(),
        name,
        surname,
        birthdayYear.toString(),
        group,
        team,
        grade,
        startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
        finishTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
        place?.laggingFromLeader?.toKotlinDuration()?.toString()
    )
}