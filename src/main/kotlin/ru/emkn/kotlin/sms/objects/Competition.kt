package ru.emkn.kotlin.sms.objects

import kotlinx.datetime.LocalDateTime

data class Competition(val name: String, val data: LocalDateTime, val courses : List<Course>)
