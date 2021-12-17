package ru.emkn.kotlin.sms.model

import ru.emkn.kotlin.sms.io.MultilineWritable

/**
 *  Class for saving data about one sports group whose members follow the same route
 */
class Group(val name: String, routeName: String) : MultilineWritable {

    val route =
        Route.byName[routeName] ?: throw IllegalArgumentException("There is no appropriate route for $routeName")

    val members: MutableSet<Participant> = mutableSetOf()


    init {
        byName[name] = this
    }

    constructor(name: String, routeName: String, participants: List<Participant>) : this(name, routeName) {
        members.addAll(participants)
    }

    companion object {
        val byName: MutableMap<String, Group> = mutableMapOf()
    }


    override fun toString() = this.name

    override fun toMultiline(): List<List<Any?>> {
        val res: MutableList<List<Any?>> = mutableListOf(listOf(this))
        res.addAll(this.members.map { it.toLine() })
        return res
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (name != other.name) return false
        if (route != other.route) return false
        if (members != other.members) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + route.hashCode()
        result = 31 * result + members.hashCode()
        return result
    }

}
