package ru.emkn.kotlin.sms.model

import java.time.Duration

class PersonalResultByWeight(group: Group) : PersonalResult(group) {
    override var penalty: Map<Participant, Int> = mapOf()

    override fun fillPenalty() {
        val tempPenalty = mutableMapOf<Participant, Int>()
        group.members.forEach {
            val way = participantWay[it]?.map { timestamp ->
                timestamp.checkpoint.weight * Duration.between(
                    timestamp.participant.startTime,
                    timestamp.time
                ).seconds.toInt()
            } ?: listOf(0)
            tempPenalty[it] = way.sum()
        }
        penalty = tempPenalty
    }

}