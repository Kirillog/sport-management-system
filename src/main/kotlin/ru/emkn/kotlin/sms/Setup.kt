package ru.emkn.kotlin.sms

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

enum class FileType {
    JSON, CSV
}

val headers = mapOf(
    "Группа" to "group",
    "Фамилия" to "surname",
    "Имя" to "name",
    "Название" to "name",
    "Г.р." to "birthdayYear",
    "Разр." to "grade",
    "Дистанция" to "course"
)

class ArgumentsFormat(parser: ArgParser) {
    val competitionName by parser.positional("EVENT", """
        Name of competition directory
    """.trimIndent())

    val competitionsRoot by parser.positional("DIR", """
        Sets path for directory, which storing all competitions
    """.trimIndent()).default<String>("competitions")
}

