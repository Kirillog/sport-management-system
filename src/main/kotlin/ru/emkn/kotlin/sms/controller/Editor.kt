package ru.emkn.kotlin.sms.controller

import mu.KotlinLogging
import org.jetbrains.exposed.sql.deleteWhere
import ru.emkn.kotlin.sms.controller.Creator.convert
import ru.emkn.kotlin.sms.model.*
import java.time.LocalDate
import java.time.LocalTime

private val logger = KotlinLogging.logger { }

object Editor {
    fun editParticipant(participant: Participant, values: Map<String, String>) {
        try {
            val name = convert<String>(values["name"])
            val surname = convert<String>(values["surname"])
            val birthdayYear = convert<Int>(values["birthdayYear"])
            val grade = convert<String?>(values["grade"])
            val groupName = convert<String>(values["group"])
            val teamName = convert<String>(values["team"])
            if (!Group.checkByName(groupName))
                throw IllegalArgumentException("Cannot find group $groupName")
            if (!Team.checkByName(teamName))
                throw IllegalArgumentException("Cannot find team $teamName")
            if (CompetitionController.state >= State.TOSSED) {
                val startTime = convert<LocalTime>(values["startTime"])
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
            if (!Route.checkByName(routeName))
                throw IllegalArgumentException("Cannot find route $routeName")
            group.change(name, routeName)
            logger.info { "Group was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit group ${group.name}" }
            throw err
        }
    }


    fun editEvent(event: Event, values: Map<String, String>) {
        try {
            val eventName = convert<String>(values["name"])
            val data = convert<LocalDate>(values["date"])
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
            val checkPoints = convert<List<Checkpoint>>(values["checkPoints"])
            route.change(routeName, checkPoints)
            logger.info { "Route was successfully edited" }
        } catch (err: IllegalArgumentException) {
            logger.info { "Cannot edit route ${route.name}" }
            throw err
        }
    }

    fun deleteParticipant(id: Int) {
        ParticipantTable.deleteWhere { ParticipantTable.id eq id }
        TossTable.deleteWhere { TossTable.participantID eq id }
        PersonalResultTable.deleteWhere { ParticipantTable.id eq id }
        TimestampTable.deleteWhere { TimestampTable.id eq id }
        logger.info { "Participant with id $id was deleted" }
    }

    fun deleteGroup(id: Int) {
        val members = Group.findById(id)?.members ?: return
        members.forEach { member ->
            deleteParticipant(member.id.value)
        }
        GroupTable.deleteWhere { GroupTable.id eq id }
        logger.info { "Group with id $id was deleted" }
    }

    fun deleteTeam(id: Int) {
        val members = Team.findById(id)?.members ?: return
        members.forEach { member ->
            deleteParticipant(member.id.value)
        }
        TeamResultTable.deleteWhere { TeamResultTable.teamID eq id }
        TeamTable.deleteWhere { TeamTable.id eq id }
        logger.info { "Team with id $id was deleted" }
    }

    fun deleteRoute(id: Int) {
        val route = Route.findById(id) ?: return
        RouteTable.deleteWhere { RouteTable.id eq id }
        // TODO()
    }

}