package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.Readable
import ru.emkn.kotlin.sms.io.SingleLineWritable

/**
 * A class for storing a route along which one group of participants runs.
 */
data class Route(val name: String, val checkPoints: List<CheckPoint>) : Readable, SingleLineWritable {

    companion object {
        val byName: MutableMap<String, Route> = mutableMapOf()
    }

    init {
        Route.byName[name] = this
    }

    override fun toLine(): List<String?> = listOf(name) + checkPoints.map { it.id.toString() }
}
