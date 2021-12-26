package ru.emkn.kotlin.sms.controller

import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Creator.convert
import ru.emkn.kotlin.sms.model.*
import java.time.LocalDate
import java.time.LocalTime

private val logger = KotlinLogging.logger { }

object Editor {

    fun editParticipant(participant: Participant, values: Map<ObjectFields, String>) {
        try {
            transaction {
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
                if (CompetitionController.state >= State.TOSSED) {
                    val startTime = convert<LocalTime>(values[ObjectFields.StartTime])
                    participant.startTime = startTime
                }
                participant.change(name, surname, birthdayYear, groupName, teamName, grade)
            }
            logger.info { "Participant was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit participant ${participant.name}" }
            throw err
        }
    }

    fun editGroup(group: Group, values: Map<ObjectFields, String>) {
        try {
            transaction {
                val name = convert<String>(values[ObjectFields.Name])
                val route = convert<Route>(values[ObjectFields.RouteName])
                group.change(name, route)
            }
            logger.info { "Group was successfully edited" }
        } catch (err: Exception) {
            logger.info { "Cannot edit group ${group.name}" }
            throw err
        }
    }


    fun editEvent(event: Event, values: Map<ObjectFields, String>) {
        try {
            val eventName = convert<String>(values[ObjectFields.Name])
            val data = convert<LocalDate>(values[ObjectFields.Date])
            transaction {
                event.change(eventName, data)
            }
            logger.info { "Event was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit event ${event.name}" }
            throw err
        }
    }

    fun editTeam(team: Team, values: Map<ObjectFields, String>) {
        try {
            transaction {
            val teamName = convert<String>(values[ObjectFields.Name])
                team.change(teamName)
            }
            logger.info { "Team was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit team ${team.name}" }
            throw err
        }
    }

    fun editRoute(route: Route, values: Map<ObjectFields, String>) {
        try {
            val routeName = convert<String>(values[ObjectFields.Name])
            transaction {
                val checkPoints = convert<List<Checkpoint>>(values[ObjectFields.CheckPoints])
                route.change(routeName, checkPoints)
            }
            logger.info { "Route was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit route ${route.name}" }
            throw err
        }
    }

    fun editCheckpoint(checkpoint: Checkpoint, values: Map<ObjectFields, String>) {
        try {
            val name = convert<String>(values[ObjectFields.Name])
            val weight = convert<Int>(values[ObjectFields.Weight])
            transaction {
                checkpoint.change(name, weight)
            }
            logger.info { "Checkpoint was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit checkpoint ${checkpoint.name}" }
            throw err
        }
    }

    fun editTimestamp(timestamp: Timestamp, values: Map<ObjectFields, String>) {
        try {
            val participantId = convert<Int>(values[ObjectFields.ID])
            val time = convert<LocalTime>(values[ObjectFields.Time])
            val checkpointName = convert<String>(values[ObjectFields.Name])
            transaction {
                timestamp.change(time, participantId, checkpointName)
            }
            Competition.add(timestamp)
            logger.debug { "Timestamp was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.debug { "Cannot edit timestamp ${timestamp.id}" }
            throw err
        }
    }
}