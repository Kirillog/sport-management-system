package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.io.Writer
import java.io.File
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
        path.resolve("input/event.csv"),
        path.resolve("input/courses.csv")
    )

    CompetitionController.registerFromPath(
        path.resolve("input/classes.csv"),
        path.resolve("applications")
    )

    CompetitionController.toss()
    val writer = Writer(File("test.csv"), FileType.CSV)
    CompetitionController.saveToss(writer)
}
