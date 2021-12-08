package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.MultilineWritable
import java.time.format.DateTimeFormatter
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

/**
 *  Class for saving data about one sports group whose members follow the same route
 */
data class Group(val name: String, val course: Course, var members: List<Participant>) : MultilineWritable<Group> {

    override val strToField: Map<String, KProperty<*>> = mapOf(
        "Имя Группы" to Group::name,
        "Курс" to Group::course,
        "Имя участника" to Group::members::name
    )

    /**
     * Sets default output format for using with [ru.emkn.kotlin.sms.io.Writer] object.
     */
    override fun toMultiline(m: Map<String, KProperty<*>>): List<List<String?>> {
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
