package ru.emkn.kotlin.sms.objects


import ru.emkn.kotlin.sms.io.*

class Team(val name : String, val members: List<Participant>): MultilineWritable {

    override fun toMultiline(): List<List<String?>> {
        val result = mutableListOf(listOf<String?>(name))
        result.addAll(members.map { it.toLine() })
        return result
    }
}
