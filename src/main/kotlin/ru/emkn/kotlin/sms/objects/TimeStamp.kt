package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable
import java.time.LocalTime

/**
 * Class for storing information about participant has checked by checkpoint at some time
 */
data class TimeStamp(val time : LocalTime, val checkPointId: Int, val participantId: Int) : Readable
