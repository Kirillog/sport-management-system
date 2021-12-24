package ru.emkn.kotlin.sms.controller

import mu.KotlinLogging
import org.jetbrains.exposed.sql.deleteWhere
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
            val name = convert<String>(values[ObjectFields.Name])
            val surname = convert<String>(values[ObjectFields.Surname])
            val birthdayYear = convert<Int>(values[ObjectFields.BirthdayYear])
            val grade = convert<String?>(values[ObjectFields.Grade])
            val groupName = convert<String>(values[ObjectFields.Group])
            val teamName = convert<String>(values[ObjectFields.Team])
            transaction {
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
            val name = convert<String>(values[ObjectFields.Name])
            val routeName = convert<String>(values[ObjectFields.RouteName])
            transaction {
                if (!Route.checkByName(routeName))
                    throw IllegalArgumentException("Cannot find route $routeName")
                group.change(name, routeName)
            }
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
            val teamName = convert<String>(values[ObjectFields.Name])
            transaction {
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
            val checkPoints = convert<List<Checkpoint>>(values[ObjectFields.CheckPoints])
            route.change(routeName, checkPoints)
            logger.info { "Route was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit route ${route.name}" }
            throw err
        }
    }

    fun editCheckpoint(checkpoint: Checkpoint, values: Map<ObjectFields, String>) {
        transaction {
        }
        TODO()
    }

    fun editTimestamp(timestamp: Timestamp, values: Map<ObjectFields, String>) {
        TODO()
    }
}

object Deleter {

    fun deleteParticipant(id: Int) {
        transaction {
            ParticipantTable.deleteWhere { ParticipantTable.id eq id }
            TossTable.deleteWhere { TossTable.participantID eq id }
            PersonalResultTable.deleteWhere { ParticipantTable.id eq id }
            TimestampTable.deleteWhere { TimestampTable.id eq id }
        }
        logger.info { "Participant with id $id was deleted" }
    }

    fun deleteGroup(id: Int) {
        transaction {
            val members = Group.findById(id)?.members ?: throw IllegalStateException("No group with such id $id")
            members.forEach { member ->
                deleteParticipant(member.id.value)
            }
            GroupTable.deleteWhere { GroupTable.id eq id }
        }
        logger.info { "Group with id $id was deleted" }
    }

    fun deleteTeam(id: Int) {
        transaction {
            val members = Team.findById(id)?.members ?: throw IllegalStateException("No team with such id $id")
            members.forEach { member ->
                deleteParticipant(member.id.value)
            }
            TeamResultTable.deleteWhere { TeamResultTable.teamID eq id }
            TeamTable.deleteWhere { TeamTable.id eq id }
        }
        logger.info { "Team with id $id was deleted" }
    }

    fun deleteRoute(id: Int) {
        transaction {
            val route = Route.findById(id) ?: throw IllegalStateException("No route with such id $id")
            RouteCheckpointsTable.deleteWhere { RouteCheckpointsTable.route eq route.id }
            route.delete()
        }
        logger.info { "Route with id $id was deleted" }
    }

    fun deleteCheckpoint(id: Int) {
        transaction {
            val checkpoint = Checkpoint.findById(id) ?: throw IllegalStateException("No checkpoint with such id $id")
            RouteCheckpointsTable.deleteWhere { RouteCheckpointsTable.checkpoint eq checkpoint.id }
            checkpoint.delete()
        }
        logger.info { "Checkpoint with id $id was deleted" }
    }

    fun deleteTimestamp(id: Int) {
        TODO()
    }
}