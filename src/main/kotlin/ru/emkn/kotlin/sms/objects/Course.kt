package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable

data class Course(val name: String, val checkPoints: List<CheckPoint>) : Readable
