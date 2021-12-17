package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.controller.CompetitionController
import kotlin.io.path.Path

//import com.xenomachina.argparser.ArgParser
//import ru.emkn.kotlin.sms.targets.tossTarget
//import ru.emkn.kotlin.sms.targets.personalResultsTarget
//import ru.emkn.kotlin.sms.targets.teamResultsTarget

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

    logger.info { "Program started" }

    val path = Path("competitions/competition-1")
    CompetitionController.announceFromPath(
        event = path.resolve("input/event.csv"),
        routes = path.resolve("input/courses.csv")
    )

//    CompetitionController.registerFromPath(
//        group = path.resolve("input/classes.csv"),
//        team = path.resolve("applications")
//    )


//    CompetitionController.toss()
//    val writer = Writer(path.resolve("protocols/toss.csv").toFile(), FileType.CSV)
//    CompetitionController.saveToss(writer)

    CompetitionController.groupsAndTossFromPath(
        group = path.resolve("input/classes.csv"),
        toss = path.resolve("protocols/toss.csv")
    )
    CompetitionController.registerResultsFromPath(
        checkPoints = path.resolve("checkpoints")
    )
    CompetitionController.calculatePersonalResults()
    CompetitionController.saveResultsToPath(results = path.resolve("protocols/results.csv"))
    CompetitionController.saveTeamResultsToPath(results = path.resolve("protocols/teamResults.csv"))
}
