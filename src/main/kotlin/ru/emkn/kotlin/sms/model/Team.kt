package ru.emkn.kotlin.sms.model


/**
 * Class for representing all information about one team, with read from single application file
 */
data class Team(val name: String) {

    val members: MutableSet<Participant> = mutableSetOf()

    companion object {
        val byName: MutableMap<String, Team> = mutableMapOf()
    }

    init {
        byName[name] = this
    }

    override fun toString() = this.name
}