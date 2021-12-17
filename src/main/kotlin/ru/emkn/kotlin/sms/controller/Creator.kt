package ru.emkn.kotlin.sms.controller

import com.sksamuel.hoplite.simpleName
import mu.KotlinLogging
import ru.emkn.kotlin.sms.model.*
import java.time.LocalDate
import java.time.LocalTime
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

private val logger = KotlinLogging.logger { }

object Creator {
    inline fun <reified T> convert(field: String?): T {
        val message = "Cannot convert essential field for ${T::class.simpleName}"
        if (field == null)
            throw IllegalStateException(message)
        val result = convert(field, T::class.starProjectedType)
        return result as T
    }

    fun convert(field: String, kType: KType): Any? =
        when (kType.jvmErasure) {
            Int::class -> field.toIntOrNull()
                ?: throw IllegalArgumentException("Cannot parse $field as Int")
            String::class -> {
                require(kType.isMarkedNullable || field.isNotEmpty()) { "Cannot parse empty field as String" }
                field.ifEmpty { null }
            }
            List::class -> field.split(",").dropLastWhile(String::isEmpty).map { element ->
                kType.arguments.first().type?.let {
                    convert(element, it)
                }
            }
            Checkpoint::class -> Checkpoint.findByName(convert(field))
            LocalDate::class ->
                try {
                    val (date, month, year) = field.split(".").map(String::toInt)
                    LocalDate.of(year, month, date)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Cannot parse $field as Date")
                }
            LocalTime::class ->
                try {
                    val (hours, minutes, seconds) = field.split(":").map(String::toInt)
                    LocalTime.of(hours, minutes, seconds)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Cannot parse $field as Time")
                }
            else -> {
                val message = "Cannot convert essential field for ${kType.simpleName}"
                throw IllegalStateException(message)
            }
        }

    fun createEventFrom(values: Map<String, String>) {
        Editor.editEvent(Competition.event, values)
        logger.info { "Event was successfully created" }
    }

    fun createRouteFrom(values: Map<String, String>) {
        try {
            val routeName = convert<String>(values["name"])
            val checkPoints = convert<List<Checkpoint>>(values["checkPoints"])
            Competition.add(Route.create(routeName, checkPoints))
            logger.info { "Route was successfully created" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot create new route" }
            throw err
        }
    }

    fun createParticipantFrom(values: Map<String, String>) {
        try {
            val name = convert<String>(values["name"])
            val surname = convert<String>(values["surname"])
            val birthdayYear = convert<Int>(values["birthdayYear"])
            val grade = convert<String?>(values["grade"])
            val groupName = convert<String>(values["groupName"])
            val teamName = convert<String>(values["teamName"])
            if (!Group.checkByName(groupName))
                throw IllegalArgumentException("Cannot find group $groupName")
            if (!Team.checkByName(teamName))
                throw IllegalArgumentException("Cannot find team $teamName")
            Competition.add(Participant.create(name, surname, birthdayYear, groupName, teamName, grade))
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot create new participant" }
            throw err
        }
    }

    fun createGroupFrom(values: Map<String, String>) {
        try {
            val name = convert<String>(values["name"])
            val routeName = convert<String>(values["routeName"])
            if (Route.checkByName(routeName) == null)
                throw IllegalArgumentException("Cannot find route $routeName")
            Competition.add(Group.create(name, routeName))
            logger.info { "Group was successfully created" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot create new group" }
            throw err
        }
    }

    fun createTeamFrom(values: Map<String, String>) {
        try {
            val teamName = convert<String>(values["name"])
            Competition.add(Team.create(teamName))
            logger.info { "Team was successfully created" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot create team" }
            throw err
        }
    }
}