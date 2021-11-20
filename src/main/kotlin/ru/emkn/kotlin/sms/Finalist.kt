package ru.emkn.kotlin.sms

import kotlinx.datetime.LocalDateTime

class Finalist(name: String, surname: String, birthdayYear: Int, startTime : LocalDateTime, val finishTime: LocalDateTime) : Participant(name, surname, birthdayYear, startTime)