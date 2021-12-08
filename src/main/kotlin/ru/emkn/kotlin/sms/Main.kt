package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.Writer2
import ru.emkn.kotlin.sms.objects.Course
import ru.emkn.kotlin.sms.objects.Group
import ru.emkn.kotlin.sms.objects.Participant
import ru.emkn.kotlin.sms.targets.tossTarget
import ru.emkn.kotlin.sms.targets.personalResultsTarget
import ru.emkn.kotlin.sms.targets.teamResultsTarget
import kotlin.io.path.Path

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

    val group = Group("", Course("", listOf()), listOf(Participant("kek", "lol", 192, "kk", "kk", "k")))
    println(group)
    val writer = Writer2(Group, setOf(Group::name))
    println(writer.header)
    println(writer.formattedHeader)

    logger.info { "Program started" }
    val parsedArgs = ArgParser(args).parseInto(::ArgumentsFormat)
    val competitionPath = Path(parsedArgs.competitionsRoot).resolve(parsedArgs.competitionName)
    try {
        when (parsedArgs.target) {
            Target.TOSS -> tossTarget(competitionPath)
            Target.PERSONAL_RESULT -> personalResultsTarget(competitionPath)
            Target.TEAM_RESULT -> teamResultsTarget(competitionPath)
        }

        logger.info { "Program successfully finished" }
    } catch (error: Exception) {
        logger.info { "Wow, that's a big surprise, program was fault" }
        logger.error { error.message }
    }
}
