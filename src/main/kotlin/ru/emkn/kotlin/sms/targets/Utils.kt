package ru.emkn.kotlin.sms.targets

import mu.KotlinLogging
import ru.emkn.kotlin.sms.objects.Course
import ru.emkn.kotlin.sms.objects.Group
import ru.emkn.kotlin.sms.objects.Participant
import ru.emkn.kotlin.sms.objects.TimeStamp
import java.time.Duration

private val logger = KotlinLogging.logger {}

fun getTimestampsByParticipant(groups: List<Group>, timestamps: List<TimeStamp>): Map<Participant, List<TimeStamp>> {
    val participantById = groups.flatMap { it.members }.associateBy { it.id ?: 0 }
    return timestamps.sortedBy { it.time }.groupBy { it.participantId }
        .mapKeys { participantById[it.key] ?: throw IllegalStateException() }
}

fun getCourseByParticipant(groups: List<Group>): Map<Participant, Course> =
    groups.flatMap { group -> group.members.associateWith { group.course }.toList() }.toMap()

fun getGroupByParticipant(groups: List<Group>): Map<Participant, Group> {
    val groupByName = groups.associateBy { it.name }
    return groups.flatMap { it.members }.associateWith { groupByName[it.group] ?: throw IllegalStateException() }
}

fun sortGroupsByPlace(groups: List<Group>) {
    groups.forEach { group ->
        val (banned, notBanned) = group.members.partition { it.positionInGroup == null }
        group.members = notBanned.sortedBy { it.positionInGroup?.place } + banned
    }
}

fun fillTimestamps(groups: List<Group>, timestamps: List<TimeStamp>) {
    val timestampsByParticipant = getTimestampsByParticipant(groups, timestamps)
    groups.flatMap { it.members }.forEach {
        it.timeStamps = timestampsByParticipant[it]
        it.finishTime = it.timeStamps?.last()?.time
    }
}

fun fillFinishData(participants: List<Participant>) {

    participants.groupBy { it.group }.forEach { (groupName, members) ->
        val sortedGroup = members.sortedByDescending { it.runTime }
        val leaderFinishTime = sortedGroup[0].runTime
        if (leaderFinishTime == null) {
            logger.info { "Not a single participant finished" }
            return@forEach
        }
        sortedGroup.forEachIndexed { place, participant ->
            val time = participant.runTime
            requireNotNull(time) { "Banned user cannot finish the competition" }
            participant.positionInGroup = Participant.PositionInGroup(
                place + 1,
                leaderFinishTime - participant.runTime
            )
        }
    }
}

fun getNotCheaters(groups: List<Group>): List<Participant> {
    val courseByParticipant = getCourseByParticipant(groups)
    return groups.flatMap { it.members }.filter { participant ->
        val course = courseByParticipant[participant]
        val isBanned = course?.checkPoints?.map { it.id } != participant.timeStamps?.map { it.checkPointId }
        if (isBanned) {
            logger.info {
                "Participant ${participant.id} ${participant.name} ${participant.surname} " +
                        "was disqualified for violating the course."
            }
        }
        !isBanned
    }
}

fun Duration.toIntervalString(): String = "${this.toHoursPart()}h ${this.toMinutesPart()}m ${this.toSecondsPart()}s"
