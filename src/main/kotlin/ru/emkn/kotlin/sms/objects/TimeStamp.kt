package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable
import java.time.LocalTime

/**
 * Class for storing information about participant has checked by checkpoint at some time
 */
data class TimeStamp(val time: LocalTime, val checkPoint: CheckPoint, val participant: Participant) : Readable {
    constructor(time: LocalTime, checkPoint: CheckPoint, participantId: Int) :
            this(
                time, checkPoint, Participant.byId[participantId]
                    ?: throw IllegalArgumentException("No user with this ID $participantId")
            )
}
