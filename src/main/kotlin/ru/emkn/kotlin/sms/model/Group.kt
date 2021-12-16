package ru.emkn.kotlin.sms.model

/**
 *  Class for saving data about one sports group whose members follow the same route
 */
class Group(val name: String, routeName: String) {

    val route =
        Route.byName[routeName] ?: throw IllegalArgumentException("There is no appropriate route for $routeName")

    val members: MutableSet<Participant> = mutableSetOf()

    companion object {
        val byName: MutableMap<String, Group> = mutableMapOf()
    }

    init {
        byName[name] = this
    }
}
