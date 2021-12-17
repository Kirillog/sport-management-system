package ru.emkn.kotlin.sms.model

import ru.emkn.kotlin.sms.io.MultilineWritable
import ru.emkn.kotlin.sms.io.SingleLineWritable

/**
 * Class for representing all information about one team, with read from single application file
 */
data class Team(val name: String) : MultilineWritable, SingleLineWritable {

    val members: MutableSet<Participant> = mutableSetOf()

    val score
        get() = Competition.teamResult.getScore(this)

    companion object {
        val byName: MutableMap<String, Team> = mutableMapOf()
    }

    init {
        byName[name] = this
    }

    constructor(name: String, members: List<Participant>) : this(name) {
        this.members.addAll(members)
    }

    override fun toMultiline(): List<List<Any?>> = listOf(
        listOf(name) + listOf(
            listOf(
                "Номер",
                "Имя",
                "Фамилия",
                "Г.р.",
                "Команда",
                "Разр."
            )
        ) + members.map(Participant::toLine)
    )

    override fun toLine(): List<Any?> = listOf(
        name, score
    )

    override fun toString() = this.name
}

fun formatterForApplications(team: Team) = listOf(
    listOf(team.name) + listOf(
        listOf(
            "Имя",
            "Фамилия",
            "Г.р.",
            "Группа",
            "Разр."
        )
    ) + team.members.map(::formatterParticipantForApplications)
)

