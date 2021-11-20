package ru.emkn.kotlin.sms

import kotlinx.datetime.LocalDateTime

open class Participant(name: String, surname: String, birthdayYear: Int, val startTime : LocalDateTime) : Applicant(name, surname, birthdayYear)

