package ru.emkn.kotlin.sms.model

import java.time.Duration

class ResultByTime(override val group: Group) : Result(group) {
    override var penalty: Map<Participant, Int> = mapOf()

    override fun fillPenalty() {
        val tempPenalty = mutableMapOf<Participant, Int>()
        participantWay.keys.forEach {
            tempPenalty[it] = Duration.between(it.startTime, finishTime[it]).toSeconds().toInt()
        }
        penalty = tempPenalty
    }
}