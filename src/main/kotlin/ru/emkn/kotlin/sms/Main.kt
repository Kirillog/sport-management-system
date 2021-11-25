package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.io.formEvent
import ru.emkn.kotlin.sms.io.formGroupsList
import ru.emkn.kotlin.sms.io.formTeamsList
import ru.emkn.kotlin.sms.objects.Competition
import ru.emkn.kotlin.sms.objects.Group
import java.nio.file.Path
import java.time.LocalTime
import kotlin.io.path.Path

private val logger = KotlinLogging.logger {}


fun tossTarget(competition: Competition) {
    competition.simpleToss(LocalTime.NOON, 5)
    val writer = Writer(competition.path.resolve("protocols/toss.csv").toFile(), FileType.CSV)
    writer.addAll(competition.groups)
    writer.write()
}

fun main(args: Array<String>): Unit = mainBody {

    logger.info { "Program started" }
    val parsedArgs = ArgParser(args).parseInto(::ArgumentsFormat)
    val competitionPath = Path(parsedArgs.competitionsRoot).resolve(parsedArgs.competitionName)

    val competition = Competition(competitionPath)
    logger.info { "Competition files read success" }

    when (parsedArgs.target) {
        Target.TOSS -> tossTarget(competition)
        Target.PERSONAL_RESULT -> TODO()
        Target.TEAM_RESULT -> TODO()
    }
    logger.info { "Program successfully finished" }
}
