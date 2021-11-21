package ru.emkn.kotlin.sms.objects

import kotlinx.datetime.LocalDateTime

open class Participant(name: String, surname: String, birthdayYear: Int, val startTime : LocalDateTime) : Applicant(name, surname, birthdayYear)

