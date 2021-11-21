package ru.emkn.kotlin.sms.objects

data class Group(val name : String, val course: Course, val members: List<Participant>)
