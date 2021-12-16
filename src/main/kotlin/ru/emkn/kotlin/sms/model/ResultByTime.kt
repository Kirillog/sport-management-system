package ru.emkn.kotlin.sms.model

import java.time.Duration
import java.time.LocalTime

class ResultByTime : Result {
    override var state: Result.State =
        Result.State.PREPARING
    override var finishTime: Map<Participant, LocalTime> = mapOf()

    override var positionInGroup: Map<Participant, Result.PositionInGroup> = mapOf()

    override fun calculate() {
        val participantResult = disqualifyCheaters(RuntimeDump.resultsByParticipant())
        finishTime = participantResult.map { (participant, timestamps) ->
            Pair(participant, timestamps.last().time)
        }.toMap()
        positionInGroup = positions()
        state = Result.State.COMPLETED
    }

    override fun sortMembersIn(group: Group) =
        group.members.sortedByDescending {
            finishTime[it] ?: throw IllegalArgumentException("There is no finish time for ${it.id}")
        }


    override fun disqualifyCheaters(participantResult: Map<Participant, List<TimeStamp>>) =
        participantResult.filter { (participant, checkPoints) ->
            participant.group.route.checkPoints == checkPoints
        }

    override fun getParticipantFinishTime(participant: Participant): LocalTime? {
        require(state == Result.State.COMPLETED)
        return finishTime[participant]
    }

    override fun getPositionInGroup(participant: Participant): Result.PositionInGroup {
        require(state == Result.State.COMPLETED)
        return positionInGroup[participant] ?: Result.PositionInGroup(0, Duration.ZERO)
    }

}