package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import mu.KotlinLogging
import ru.emkn.kotlin.sms.FileType
import java.io.File

private val logger = KotlinLogging.logger {}

interface MultilineWritable {
    fun toMultiline(): List<List<String?>>
}

interface SingleLineWritable {
    fun toLine(): List<String?>
}

class Writer(private val file: File, val filetype: FileType) {

    val buffer = mutableListOf<List<String?>>()

    fun <T : SingleLineWritable> add(el: T, formatter: (T) -> List<String?> = { it.toLine() }) =
        buffer.add(formatter(el))

    fun <T : SingleLineWritable> addAllLines(el: List<T>, formatter: (T) -> List<String?> = { it.toLine() }) =
        buffer.addAll(el.map { formatter(it) })

    fun <T : MultilineWritable> add(el: T, formatter: (T) -> List<List<String?>> = { it.toMultiline() }) =
        buffer.addAll(formatter(el))

    fun <T : MultilineWritable> addAll(el: List<T>, formatter: (T) -> List<List<String?>> = { it.toMultiline() }) =
        buffer.addAll(el.map { formatter(it) }.flatten())

    fun add(el: String?) {
        if (el != null) buffer.add(listOf(el))
        else logger.warn { "try to write null string" }
    }

    fun add(el: List<String>) = buffer.add(el)

    fun addAll(el: List<List<String>>) = buffer.addAll(el)

    fun clear() = buffer.clear()

    fun write() {
        val rowSize = buffer.maxOf { it.size }
        val shrunkenLines = buffer.map { it + List(rowSize - it.size) { "" } }
        when (filetype) {
            FileType.CSV, FileType.JSON -> csvWriter().writeAll(shrunkenLines, file)
        }
        logger.info { "Written ${buffer.size} objects to ${file.name}" }
        clear()
    }
}