package ru.emkn.kotlin.sms.model

class ResultByWeight(group: Group) : Result(group) {
    override var penalty: Map<Participant, Int> = mapOf()

    override fun fillPenalty() {
        val tempPenalty = mutableMapOf<Participant, Int>()
        group.members.forEach {
            tempPenalty[it] // TODO()
        }
        penalty = tempPenalty
    }

}