package ru.emkn.kotlin.sms.controller

import mu.KotlinLogging
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.model.*

private val logger = KotlinLogging.logger { }

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
        transaction {
            val timestamp = Timestamp.findById(id) ?: throw IllegalStateException("No timestamp with such id $id")
            timestamp.delete()
        }
        logger.info { "Timestamp with id $id was deleted" }
    }
}
