package ru.emkn.kotlin.sms.objects

import kotlinx.datetime.LocalDateTime

class Finalist(name: String, surname: String, birthdayYear: Int, startTime : LocalDateTime, val finishTime: LocalDateTime) : Participant(name, surname, birthdayYear, startTime)