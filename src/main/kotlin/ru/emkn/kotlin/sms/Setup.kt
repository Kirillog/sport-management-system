package ru.emkn.kotlin.sms

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import ru.emkn.kotlin.sms.model.*

const val MAX_TEXT_FIELD_SIZE = 127
const val DB_HEADER = "jdbc:h2"
const val DB_DRIVER = "org.h2.Driver"

enum class FileType {
    JSON, CSV
}

enum class Target {
    TOSS, PERSONAL_RESULT, TEAM_RESULT
}

val DB_TABLES = listOf(
    RouteCheckpointsTable,
    TossTable,
    PersonalResultTable,
    TeamResultTable,
    TimestampTable,
    CheckpointTable,
    ParticipantTable,
    GroupTable,
    RouteTable,
    TeamTable
)


val headers = mapOf(
    "Название группы" to ObjectFields.Name,
    "Группа" to ObjectFields.Group,
    "Фамилия" to ObjectFields.Surname,
    "Имя" to ObjectFields.Name,
    "Результат" to ObjectFields.ResultType,
    "Тип" to ObjectFields.Type,
    "Количество К/П" to ObjectFields.Amount,
    "Название" to ObjectFields.Name,
    "Г.р." to ObjectFields.BirthdayYear,
    "Разр." to ObjectFields.Grade,
    "Дистанция" to ObjectFields.RouteName,
    "Команда" to ObjectFields.Team,
    "Дата" to ObjectFields.Date,
    "К/П" to ObjectFields.CheckPoints,
    "Номер" to ObjectFields.ID,
    "Время старта" to ObjectFields.StartTime,
    "Время" to ObjectFields.Time,
    "Номер К/П" to ObjectFields.Name,
    "Стоимость" to ObjectFields.Weight
)

val english = mapOf(
    "Полный" to "Full",
    "Стоимость" to "Weight",
    "Время" to "Time",
    "Выборочный" to "Selective"
)

/**
 * A class for defining the format of command line arguments
 */
class ArgumentsFormat(parser: ArgParser) {
    val competitionName by parser.positional("EVENT", """
        name of competition directory
    """.trimIndent())

    val target by parser.mapping( mapOf(
        "--toss" to Target.TOSS,
        "--personal" to Target.PERSONAL_RESULT,
        "--team" to Target.TEAM_RESULT
    ), """
        sets the goal to be completed in the following format:
        --toss
            Forms the starting protocols according to the application lists. A simple draw is used with an interval of 1 minute and the start at 12:00:00.
        --personal
            According to the starting protocols and protocols of passing checkpoints, form protocols of results.
        --team
            According to the protocols of results, form a protocol of results for teams.
    """.replace("\n", "\r")
    )

    val competitionsRoot by parser.positional(
        "DIR", """
        sets path for directory, which storing all competitions
    """.trimIndent()
    ).default<String>("competitions")
}

const val maxTextLength = 127

enum class ObjectFields {
    ID,
    Name,
    Surname,
    Group,
    Time,
    Team,
    BirthdayYear,
    Grade,
    StartTime,
    Date,
    RouteName,
    CheckPoints,
    Type,
    Amount,
    Weight,
    ResultType,
    Full,
    Selective
}

