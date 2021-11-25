package ru.emkn.kotlin.sms.objects

import kotlinx.datetime.LocalDate
import ru.emkn.kotlin.sms.io.Readable

data class Event(val name: String, val date: LocalDate) : Readable
