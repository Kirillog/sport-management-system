package ru.emkn.kotlin.sms.model

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
        state = Result.State.FILLEDTIME
        positionInGroup = positions()
        state = Result.State.COMPLETED
    }

    override fun sortMembersIn(group: Group) =
        group.members.sortedBy {
            it.runTime
        }


    override fun disqualifyCheaters(participantResult: Map<Participant, List<TimeStamp>>) =
        participantResult.filter { (participant, timestamps) ->
            participant.group.route.checkPoints == timestamps.map { it.checkPoint }
        }

}