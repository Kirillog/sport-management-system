package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.MultilineWritable
import java.time.format.DateTimeFormatter

data class Group(val name: String, val course: Course, var members: List<Participant>) : MultilineWritable {

    override fun toMultiline(): List<List<String?>> {
        val result = mutableListOf(listOf<String?>(name))
        result.addAll(members.map {
           listOf(
               it.id?.toString(),
               it.name,
               it.surname,
               it.birthdayYear.toString(),
               it.team,
               it.grade,
               it.startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME)
           )
        })
        return result
    }
}
