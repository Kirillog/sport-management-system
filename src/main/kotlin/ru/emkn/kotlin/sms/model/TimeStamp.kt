package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalTime

/**
 * Class for storing information about participant has checked by checkpoint at some time
 */
data class TimeStamp(val time: LocalTime, val checkPoint: CheckPoint, val participant: Participant) {
//    TODO()
//    constructor(time: LocalTime, checkPointId: Int, participantId: Int) :
//            this(
//                time, CheckPoint(checkPointId), Participant.byId[participantId]
//                    ?: throw IllegalArgumentException("No user with this ID $participantId")
//            )
//
//    constructor(time: LocalTime, checkPoint: CheckPoint, participantId: Int) :
//            this(
//                time, checkPoint, Participant.byId[participantId]
//                    ?: throw IllegalArgumentException("No user with this ID $participantId")
//            )
}
