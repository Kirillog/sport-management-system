package ru.emkn.kotlin.sms.model

import ru.emkn.kotlin.sms.io.MultilineWritable

/**
 *  Class for saving data about one sports group whose members follow the same route
 */
class Group(val name: String, routeName: String): MultilineWritable {

    val route =
        Route.byName[routeName] ?: throw IllegalArgumentException("There is no appropriate route for $routeName")

    val members: MutableSet<Participant> = mutableSetOf()

    companion object {
        val byName: MutableMap<String, Group> = mutableMapOf()
    }

    init {
        byName[name] = this
    }

    override fun toString() = this.name

    override fun toMultiline(): List<List<Any?>> {
        val res: MutableList<List<Any?>> = mutableListOf(listOf(this))
        res.addAll(this.members.map { it.toLine() })
        return res
    }
}
