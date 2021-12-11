package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import kotlin.io.path.Path

import ru.emkn.kotlin.sms.model.*
import ru.emkn.kotlin.sms.controller.*
import ru.emkn.kotlin.sms.io.Writer
import java.io.File

//import com.xenomachina.argparser.ArgParser
//import ru.emkn.kotlin.sms.targets.tossTarget
//import ru.emkn.kotlin.sms.targets.personalResultsTarget
//import ru.emkn.kotlin.sms.targets.teamResultsTarget

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

    logger.info { "Program started" }

//    val path = Path("competitions/competition-1")
//    CompetitionController.announceFromPath(
//        path.resolve("input/event.csv"),
//        path.resolve("input/courses.csv")
//    )
//
//    Competition.loadGroups(FileLoader(path.resolve("input/classes.csv")))
//    Competition.loadTeams(FileLoader(path.resolve("applications")))
//    Competition.toss.addAllParticipant()
//    Competition.toss.build()
//
//    val writer = Writer(File("test.csv"), FileType.CSV)
//    CompetitionController.saveToss(writer)

//    val parsedArgs = ArgParser(args).parseInto(::ArgumentsFormat)
//    val competitionPath = Path(parsedArgs.competitionsRoot).resolve(parsedArgs.competitionName)
//    try {
//        when (parsedArgs.target) {
//            Target.TOSS -> tossTarget(competitionPath)
//            Target.PERSONAL_RESULT -> personalResultsTarget(competitionPath)
//            Target.TEAM_RESULT -> teamResultsTarget(competitionPath)
//        }
//
//        logger.info { "Program successfully finished" }
//    } catch (error: Exception) {
//        logger.info { "Wow, that's a big surprise, program was fault" }
//        logger.error { error.message }
//    }

}
