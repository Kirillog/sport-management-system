package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable

data class Course(val name: String, val checkPoints: List<CheckPoint>) : Readable, SingleLineWritable {
    override fun toLine(): List<String?> = listOf(name) + checkPoints.map { it.id.toString() }
}
