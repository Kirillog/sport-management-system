package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
import com.xenomachina.argparser.*
//https://github.com/doyaaaaaken/kotlin-csv
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

class MyArgs(parser: ArgParser) {
    val verbose by parser.flagging("-v", "--verbose", help="enable verbose mode")

    val name by parser.storing("-n", "--name", help="user name").default<String>("kek")

    val count by parser.storing("-c", "--count", help="test counter") { toIntOrNull() ?: println("null here") }
}

fun main(args: Array<String>) = mainBody {
    val parsedArgs = ArgParser(args).parseInto(::MyArgs)
    println("Hello ${parsedArgs.name}!")

    val csvFile = File("sample-data/classes.csv")
    val csvData = csvReader().readAllWithHeader(csvFile)
//    val csvData = csvReader().readAll(csvFile)
    println(csvData.toString())
}
