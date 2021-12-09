package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.Writer
//import com.xenomachina.argparser.ArgParser
//import ru.emkn.kotlin.sms.targets.tossTarget
//import ru.emkn.kotlin.sms.targets.personalResultsTarget
//import ru.emkn.kotlin.sms.targets.teamResultsTarget
import kotlin.io.path.Path

import ru.emkn.kotlin.sms.objects.*
import java.io.File
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

    logger.info { "Program started" }

    val competition = Competition(Event("Event Name", LocalDate.of(2021, 11, 11)))
    val path = Path("competitions/competition-1")
    competition.loadRoutes(FileLoader(path.resolve("input/courses.csv")))
    competition.loadGroups(FileLoader(path.resolve("input/classes.csv")))
    competition.loadTeams(FileLoader(path.resolve("applications")))

    println("kek")
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
