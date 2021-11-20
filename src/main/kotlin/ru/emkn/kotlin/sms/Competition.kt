package ru.emkn.kotlin.sms

import kotlinx.datetime.LocalDateTime

data class Competition(val name: String, val data: LocalDateTime, val courses : List<Course>)
