package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable

data class GroupToCourse(val group: String, val course: String) : Readable
