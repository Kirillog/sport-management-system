package ru.emkn.kotlin.sms.model

import ru.emkn.kotlin.sms.io.SingleLineWritable

/**
 * A class for storing a route along which one group of participants runs.
 */
data class Route(var name: String, var checkPoints: List<CheckPoint>) : SingleLineWritable {

    companion object {
        val byName: MutableMap<String, Route> = mutableMapOf()
    }

    init {
        byName[name] = this
    }

    fun change(name: String, checkPoints: List<CheckPoint>) {
        if (name != this.name) {
            byName.remove(this.name)
            this.name = name
            byName[name] = this
        }
        this.checkPoints = checkPoints
    }

    override fun toLine(): List<String?> = listOf(name) + checkPoints.map { it.id.toString() }
}
