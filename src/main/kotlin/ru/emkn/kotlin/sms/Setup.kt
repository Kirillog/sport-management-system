package ru.emkn.kotlin.sms

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

enum class Filetype {
    JSON, CSV
}

class MyArgs(parser: ArgParser) {
    val verbose by parser.flagging("-v", "--verbose", help="enable verbose mode")

    val name by parser.storing("-n", "--name", help="user name").default<String>("kek")

    val count by parser.storing("-c", "--count", help="test counter") { toIntOrNull() ?: println("null here") }
}

