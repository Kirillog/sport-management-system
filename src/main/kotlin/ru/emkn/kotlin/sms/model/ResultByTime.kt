package ru.emkn.kotlin.sms.model

class ResultByTime(override val group: Group) : Result(group) {
    override var penalty: Map<Participant, Int> = mapOf()

    override fun fillPenalty() {
        val tempPenalty = mutableMapOf<Participant, Int>()
        group.members.forEach {
            tempPenalty[it] = it.runTime.toSeconds().toInt()
        }
        penalty = tempPenalty
    }
}