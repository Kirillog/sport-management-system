package ru.emkn.kotlin.sms.model

import java.time.Duration
import java.time.LocalTime

interface Result {

    enum class State {
        PREPARING, COMPLETED
    }

    var state: State

    var finishTime: Map<Participant, LocalTime>

    var positionInGroup: Map<Participant, PositionInGroup>

    fun positions(): Map<Participant, PositionInGroup> {
        val tempPositionInGroup = mutableMapOf<Participant, PositionInGroup>()

        Competition.groups.forEach { group ->
            val sortedGroup = sortMembersIn(group)
            val leaderFinishTime = sortedGroup[0].runTime
            sortedGroup.forEachIndexed { place, participant ->
                val time = participant.runTime
                tempPositionInGroup[participant] = PositionInGroup(
                    place + 1,
                    leaderFinishTime - time
                )
            }
        }
        return tempPositionInGroup
    }

    fun sortMembersIn(group: Group): List<Participant>

    fun disqualifyCheaters(participantResult: Map<Participant, List<TimeStamp>>): Map<Participant, List<TimeStamp>>

    fun getParticipantFinishTime(participant: Participant): LocalTime?

    fun getPositionInGroup(participant: Participant): PositionInGroup

    fun calculate()

    data class PositionInGroup(val place: Int, val laggingFromLeader: Duration)

}

