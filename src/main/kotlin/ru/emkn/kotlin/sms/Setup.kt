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

class MyArgs(parser: ArgParser) {
    val verbose by parser.flagging("-v", "--verbose", help = "enable verbose mode")

    val name by parser.storing("-n", "--name", help = "user name").default<String>("kek")

    val count by parser.storing("-c", "--count", help = "test counter") { toIntOrNull() ?: println("null here") }
}

