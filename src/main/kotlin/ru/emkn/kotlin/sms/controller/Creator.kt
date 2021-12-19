package ru.emkn.kotlin.sms.controller

import com.sksamuel.hoplite.simpleName
import mu.KotlinLogging
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.english
import ru.emkn.kotlin.sms.model.*
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

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
            String::class -> {
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
            ResultType::class ->
                ResultType.valueOf(field.uppercase(Locale.getDefault()))
            RouteType::class ->
                RouteType.valueOf(field.uppercase(Locale.getDefault()))
            else -> {
                val message = "Cannot convert essential field for ${kType.simpleName}"
                throw IllegalStateException(message)
            }
        }

    fun createEvent(): Event = createEventFrom(
        mapOf(
            ObjectFields.Name to "event",
            ObjectFields.Date to LocalDate.now().toString()
        )
    )

    fun createEventFrom(values: Map<ObjectFields, String>): Event {
        Editor.editEvent(Competition.event, values)
        logger.debug { "Event was successfully created" }
        return Competition.event
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun createRouteFrom(values: Map<ObjectFields, String>): Route {
        try {
            val routeName = convert<String>(values[ObjectFields.Name])
            val routeType = convert<RouteType>(english[values[ObjectFields.Type]])
            val amountOfCheckpoint = convert<Int?>(values[ObjectFields.Amount])
            val checkPoints = convert(values[ObjectFields.CheckPoints] ?: "", typeOf<List<Checkpoint>>()) as List<Checkpoint>
            val route = Route.create(routeName, checkPoints, routeType, amountOfCheckpoint ?: checkPoints.size)
            Competition.add(route)
            logger.debug { "Route was successfully created" }
            return route
        } catch (err: IllegalArgumentException) {
            logger.debug { "Cannot create new route" }
            throw err
        }
    }

    fun createParticipantFrom(values: Map<ObjectFields, String>): Participant {
        try {
            val name = convert<String>(values[ObjectFields.Name])
            val surname = convert<String>(values[ObjectFields.Surname])
            val birthdayYear = convert<Int>(values[ObjectFields.BirthdayYear])
            val grade = convert<String?>(values[ObjectFields.Grade])
            val groupName = convert<String>(values[ObjectFields.Group])
            val teamName = convert<String>(values[ObjectFields.Team])
            if (!Group.checkByName(groupName))
                throw IllegalArgumentException("Cannot find group $groupName")
            if (!Team.checkByName(teamName))
                throw IllegalArgumentException("Cannot find team $teamName")
            val participant = if (CompetitionController.state == State.TOSSED) {
                val startTime = convert<LocalTime>(values[ObjectFields.StartTime])
                Participant.create(name, surname, birthdayYear, groupName, teamName, startTime, grade)
            } else {
                Participant.create(name, surname, birthdayYear, groupName, teamName, grade)
            }
            Competition.add(participant)
            logger.debug { "Participant was successfully created" }
            return participant
        } catch (err: IllegalArgumentException) {
            logger.debug { "Cannot create new participant" }
            throw err
        }
    }

    fun createGroupFrom(values: Map<ObjectFields, String>): Group {
        try {
            val name = convert<String>(values[ObjectFields.Name])
            val resultType = convert<ResultType>(english[values[ObjectFields.ResultType]])
            val routeName = convert<String>(values[ObjectFields.RouteName])
            if (!Route.checkByName(routeName))
                throw IllegalArgumentException("Cannot find route $routeName")
            val group = Group.create(name, resultType, routeName)
            Competition.add(group)
            logger.debug { "Group was successfully created" }
            return group
        } catch (err: IllegalArgumentException) {
            logger.debug { "Cannot create new group" }
            throw err
        }
    }

    fun createTeamFrom(values: Map<ObjectFields, String>): Team {
        try {
            val teamName = convert<String>(values[ObjectFields.Name])
            val team = Team.create(teamName)
            Competition.add(team)
            logger.debug { "Team was successfully created" }
            return team
        } catch (err: IllegalArgumentException) {
            logger.debug { "Cannot create team" }
            throw err
        }
    }

    fun createCheckPointFrom(values: Map<ObjectFields, String>): Checkpoint {
        try {
            val name = convert<String>(values[ObjectFields.Name])
            val weight = convert<Int>(values[ObjectFields.Weight])
            val checkpoint = Checkpoint.create(name, weight)
            Competition.add(checkpoint)
            logger.debug { "Checkpoint $name was successfully created" }
            return checkpoint
        } catch (err: IllegalArgumentException) {
            logger.debug { "Cannot create checkpoint" }
            throw err
        }
    }

    fun createTimeStampFrom(values: Map<ObjectFields, String>): Timestamp {
        //TODO: решить конфликт имён
        try {
            val participantId = convert<Int>(values[ObjectFields.ID])
            val time = convert<LocalTime>(values[ObjectFields.Time])
            val checkpointId = convert<String>(values[ObjectFields.Name])
            val timestamp = Timestamp.create(time, checkpointId, participantId)
            Competition.add(timestamp)
            logger.debug { "Timestamp was successfully created" }
            return timestamp
        } catch (err: IllegalArgumentException) {
            logger.debug { "Cannot create timestamp" }
            throw err
        }
    }
}