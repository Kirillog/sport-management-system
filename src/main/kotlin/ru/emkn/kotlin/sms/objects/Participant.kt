package ru.emkn.kotlin.sms.objects

import kotlinx.datetime.LocalDateTime

open class Participant(
    name: String,
    surname: String,
    birthdayYear: Int,
    group: String,
    team: String,
    val startTime: LocalDateTime,
    grade: String? = null,
) : Applicant(name, surname, birthdayYear, group, team, grade)

