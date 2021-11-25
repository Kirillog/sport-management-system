package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.xenomachina.argparser.mainBody
import com.xenomachina.argparser.ArgParser
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.io.formGroupsList
import ru.emkn.kotlin.sms.io.formTeamsList
import java.io.File
import kotlin.io.path.Path

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) = mainBody {
    logger.info { "Program started" }
    val parsedArgs = ArgParser(args).parseInto(::ArgumentsFormat)
    val competitionPath = Path(parsedArgs.competitionsRoot).resolve(parsedArgs.competitionName)
    val teams = formTeamsList(competitionPath)
    val group = formGroupsList(teams, competitionPath)
    val writer = Writer(File("test.csv"), FileType.CSV)
    writer.addAll(group)
    writer.write()
}
