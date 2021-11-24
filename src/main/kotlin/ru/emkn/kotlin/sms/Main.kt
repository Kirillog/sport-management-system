package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.io.formGroupsList
import ru.emkn.kotlin.sms.io.formTeamsList
import java.io.File

private val logger = KotlinLogging.logger {}

fun main() = mainBody {
    logger.info { "Program started" }
    val teams = formTeamsList()
    val group = formGroupsList(teams)
    val writer = Writer(File("test.csv"), FileType.CSV)
    writer.addAll(group)
    writer.write()
}
