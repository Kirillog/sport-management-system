package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable
import ru.emkn.kotlin.sms.targets.toIntervalString
import java.time.Duration
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
    var timeStamps: List<TimeStamp>? = null
    var startTime: LocalTime? = null
    var finishTime: LocalTime? = null
    var positionInGroup: PositionInGroup? = null

    val runTime : Duration?
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

    data class PositionInGroup(val place: Int, val laggingFromLeader: Duration)
    @JvmName("getFinishTime1")
    fun getFinishTime() : LocalTime {
        val time = finishTime
        requireNotNull(time) {"Finish time have to be set up"}
        return time
    }

    fun getDurationTime() : Duration {
        return Duration.between(getFinishTime(), getStartTime())
    }

    override fun toLine(): List<String?> = listOf(
        positionInGroup?.place.toString(),
        id?.toString(),
        name,
        surname,
        birthdayYear.toString(),
        group,
        team,
        grade,
        startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
        finishTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
        positionInGroup?.laggingFromLeader?.toIntervalString()
    )
}