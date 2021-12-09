package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.MultilineWritable
import java.time.format.DateTimeFormatter

/**
 *  Class for saving data about one sports group whose members follow the same route
 */
class Group(val name: String, routeName: String) {

    val route = Route.byName[routeName]

    val members: MutableSet<Participant> = mutableSetOf()

    companion object {
        val byName: MutableMap<String, Group> = mutableMapOf()
    }

    init {
        byName[name] = this
    }
}
