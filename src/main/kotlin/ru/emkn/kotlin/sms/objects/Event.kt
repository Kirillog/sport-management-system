package ru.emkn.kotlin.sms.objects

import kotlinx.datetime.LocalDateTime
import ru.emkn.kotlin.sms.io.Readable

data class Event(val name: String, val data: LocalDateTime) : Readable
