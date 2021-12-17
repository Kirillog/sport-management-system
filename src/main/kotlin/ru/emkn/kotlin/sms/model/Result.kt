package ru.emkn.kotlin.sms.model

import java.time.Duration
import java.time.LocalTime

interface Result {

    enum class State {
        PREPARING, FILLEDTIME, COMPLETED
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
                    time - leaderFinishTime
                )
            }
        }
        return tempPositionInGroup
    }

    fun sortMembersIn(group: Group): List<Participant>

    fun disqualifyCheaters(participantResult: Map<Participant, List<TimeStamp>>): Map<Participant, List<TimeStamp>>

    fun getParticipantFinishTime(participant: Participant): LocalTime? {
        require(state >= State.FILLEDTIME)
        return finishTime[participant]
    }

    fun getPositionInGroup(participant: Participant): PositionInGroup {
        require(state == State.COMPLETED)
        return positionInGroup[participant] ?: PositionInGroup(0, Duration.ZERO)
    }

    fun calculate()

    data class PositionInGroup(val place: Int, val laggingFromLeader: Duration)

}

