package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable

/**
 * A class for storing a route along which one group of participants runs.
 */
data class Course(val name: String, val checkPoints: List<CheckPoint>) : Readable, SingleLineWritable {
    override fun toLine(): List<String?> = listOf(name) + checkPoints.map { it.id.toString() }
}
