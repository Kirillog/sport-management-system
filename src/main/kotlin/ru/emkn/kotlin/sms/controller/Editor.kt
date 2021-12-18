package ru.emkn.kotlin.sms.controller

import com.sksamuel.hoplite.simpleName
import mu.KotlinLogging
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.model.*
import java.time.LocalDate
import java.time.LocalTime
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

private val logger = KotlinLogging.logger { }

object Editor {
    private inline fun <reified T> convert(field: String?): T {
        val message = "Cannot convert essential field for ${T::class.simpleName}"
        if (field == null)
            throw IllegalStateException(message)
        val result = convert(field, T::class.starProjectedType)
        return result as T
    }

    private fun convert(field: String, kType: KType): Any? =
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
            CheckPoint::class -> CheckPoint(convert(field, Int::class.starProjectedType) as Int)
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

    fun editParticipant(participant: Participant, values: Map<ObjectFields, String>) {
        try {
            val name = convert<String>(values[ObjectFields.Name])
            val surname = convert<String>(values[ObjectFields.Surname])
            val birthdayYear = convert<Int>(values[ObjectFields.BirthdayYear])
            val grade = convert<String?>(values[ObjectFields.Grade])
            val groupName = convert<String>(values[ObjectFields.Group])
            val teamName = convert<String>(values[ObjectFields.Team])
            if (Group.byName[groupName] == null)
                throw IllegalArgumentException("Cannot find group $groupName")
            if (Team.byName[teamName] == null)
                throw IllegalArgumentException("Cannot find team $teamName")
            if (CompetitionController.state >= State.TOSSED) {
                val startTime = convert<LocalTime>(values[ObjectFields.StartTime])
                participant.startTime = startTime
            }
            participant.change(name, surname, birthdayYear, groupName, teamName, grade)
            logger.info { "Participant was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit participant ${participant.name}" }
            throw err
        }
    }

    fun editGroup(group: Group, values: Map<String, String>) {
        try {
            val name = convert<String>(values["name"])
            val routeName = convert<String>(values["routeName"])
            if (Route.byName[routeName] == null)
                throw IllegalArgumentException("Cannot find route $routeName")
            group.change(name, routeName)
            logger.info { "Group was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit group ${group.name}" }
            throw err
        }
    }


    fun editEvent(event: Event, values: Map<ObjectFields, String>) {
        try {
            val eventName = convert<String>(values[ObjectFields.Name])
            val data = convert<LocalDate>(values[ObjectFields.Date])
            event.change(eventName, data)
            logger.info { "Event was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit event ${event.name}" }
            throw err
        }
    }

    fun editTeam(team: Team, values: Map<String, String>) {
        try {
            val teamName = convert<String>(values["name"])
            team.change(teamName)
            logger.info { "Team was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit team ${team.name}" }
            throw err
        }
    }

    fun editRoute(route: Route, values: Map<String, String>) {
        try {
            val routeName = convert<String>(values["name"])
            val checkPoints = convert<List<CheckPoint>>(values["checkPoints"])
            route.change(routeName, checkPoints)
            logger.info { "Route was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit route ${route.name}" }
            throw err
        }
    }

    fun createEventFrom(values: Map<ObjectFields, String>) {
        editEvent(Competition.event, values)
        logger.info { "Event was successfully created" }
    }

    fun createRouteFrom(values: Map<String, String>) {
        try {
            val routeName = convert<String>(values["name"])
            val checkPoints = convert<List<CheckPoint>>(values["checkPoints"])
            Competition.add(Route(routeName, checkPoints))
            logger.info { "Route was successfully created" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot create new route" }
            throw err
        }
    }

    fun deleteParticipant(id: Int) {
        Participant.byId.remove(id)
        logger.info { "participant deleted" }
    }

    fun createParticipantFrom(values: Map<ObjectFields, String>): Participant {
        try {
            val name = convert<String>(values[ObjectFields.Name])
            val surname = convert<String>(values[ObjectFields.Surname])
            val birthdayYear = convert<Int>(values[ObjectFields.BirthdayYear])
            val grade = convert<String?>(values[ObjectFields.Grade])
            val groupName = convert<String>(values[ObjectFields.Group])
            val teamName = convert<String>(values[ObjectFields.Team])
            if (Group.byName[groupName] == null)
                throw IllegalArgumentException("Cannot find group $groupName")
            if (Team.byName[teamName] == null)
                throw IllegalArgumentException("Cannot find team $teamName")
            val participant = Participant(name, surname, birthdayYear, groupName, teamName, grade)
            Competition.add(participant)
            return participant
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot create new participant" }
            throw err
        }
    }

    fun createGroupFrom(values: Map<String, String>) {
        try {
            val name = convert<String>(values["name"])
            val routeName = convert<String>(values["routeName"])
            if (Route.byName[routeName] == null)
                throw IllegalArgumentException("Cannot find route $routeName")
            Competition.add(Group(name, routeName))
            logger.info { "Group was successfully created" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot create new group" }
            throw err
        }
    }

    fun createTeamFrom(values: Map<String, String>) {
        try {
            val teamName = convert<String>(values["name"])
            Competition.add(Team(teamName))
            logger.info { "Team was successfully created" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot create team" }
            throw err
        }
    }
}