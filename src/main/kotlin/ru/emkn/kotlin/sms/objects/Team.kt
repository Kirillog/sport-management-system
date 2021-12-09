package ru.emkn.kotlin.sms.objects


import ru.emkn.kotlin.sms.io.MultilineWritable
import java.time.format.DateTimeFormatter

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
}