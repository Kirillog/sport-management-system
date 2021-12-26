package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.javatime.time
import java.time.LocalTime

object PersonalResultTable : IntIdTable("personal_results") {
    val participantID = reference("participant", ParticipantTable)
    val finishTime = time("finishTime").nullable()
    val placeInGroup = integer("place_in_group").nullable()
    val penalty = integer("penalty").nullable()
    val deltaFromLeader = integer("delta_from_leader").nullable()
}

abstract class PersonalResult(open val group: Group) {

    protected var participantWay: Map<Participant, List<Timestamp>> = mapOf()

    protected var finishTime: Map<Participant, LocalTime> = mapOf()

    private var positionInGroup: Map<Participant, PositionInGroup> = mapOf()

    abstract var penalty: Map<Participant, Int>

    private fun fillPositions() {
        val tempPositionInGroup = mutableMapOf<Participant, PositionInGroup>()
        val sortedGroup = sortBeforeSaving()
        if (sortedGroup.isEmpty()) return
        val leaderPenalty = penalty[sortedGroup[0]]
        sortedGroup.forEachIndexed { place, participant ->
            tempPositionInGroup[participant] = PositionInGroup(
                place + 1,
                penalty[participant] - leaderPenalty
            )
        }
        positionInGroup = tempPositionInGroup
    }

    private fun sortBeforeSaving() =
        participantWay.keys.sortedBy {
            penalty[it]
        }

    fun sort() =
        group.members.filter { it.penalty != null }.sortedBy { it.penalty }

    private fun fillFinishTime() {
        participantWay = group.members.associateWith { participant -> participant.way.sortedBy { it.time } }
        disqualifyCheaters()
        finishTime = participantWay.map { (participant, timestamps) ->
            Pair(participant, timestamps.last().time)
        }.toMap()
    }

    private fun disqualifyCheaters() {
        participantWay = participantWay.filter { (_, timestamps) ->
            group.route.checkCorrectness(timestamps)
        }
    }


    fun calculate() {
        fillFinishTime()
        fillPenalty()
        fillPositions()
        saveToDB()
    }

    abstract fun fillPenalty()

    private fun saveToDB() {
        PersonalResultTable.batchInsert(group.members, false, false) { participant ->
            this[PersonalResultTable.participantID] = participant.id
            this[PersonalResultTable.finishTime] = this@PersonalResult.finishTime[participant]
            this[PersonalResultTable.placeInGroup] = this@PersonalResult.positionInGroup[participant]?.place
            this[PersonalResultTable.deltaFromLeader] = this@PersonalResult.positionInGroup[participant]?.deltaFromLeader
            this[PersonalResultTable.penalty] = this@PersonalResult.penalty[participant]
        }
    }

    data class PositionInGroup(val place: Int, val deltaFromLeader: Int)
}

private operator fun Int?.minus(other: Int?): Int =
    if (other == null || this == null)
        0
    else
        this - other
