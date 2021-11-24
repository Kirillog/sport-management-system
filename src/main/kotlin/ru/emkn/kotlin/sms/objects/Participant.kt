package ru.emkn.kotlin.sms.objects

import kotlinx.datetime.LocalDateTime
import ru.emkn.kotlin.sms.io.Readable

class Participant(
    val name: String,
    val surname: String,
    val birthdayYear: Int,
    val group: String,
    val team: String,
    val grade: String? = null
) : Readable {
    private val id: Int? = null
    private val startTime: LocalDateTime? = null
    private val finishTime: LocalDateTime? = null
}