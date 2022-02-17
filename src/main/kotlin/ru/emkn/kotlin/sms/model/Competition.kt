package ru.emkn.kotlin.sms.model

import ru.emkn.kotlin.sms.io.Loader

/**
 * The widest class that stores all the information about the competition
 */
object Competition {

    var toss = Toss()
    var teamResult: TeamResult = TeamResultByAverageScore()

    fun calculateResult() {
        Group.all().forEach {
            it.personalResult.calculate()
        }
        teamResult.calculate()
    }

    fun toss() {
        toss.addAllParticipant()
        toss.build()
    }

    fun toss(loader: Loader) {
        toss.build(loader)
    }

}
