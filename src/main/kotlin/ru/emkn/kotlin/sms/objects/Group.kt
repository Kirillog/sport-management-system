package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.MultilineWritable

data class Group(val name: String, val course: Course, val members: List<Participant>) : MultilineWritable {

    override fun toMultiline(): List<List<String>> {
        val result = mutableListOf(listOf(name))
        result.addAll(members.map { it.toLine() })
        return result
    }
}
