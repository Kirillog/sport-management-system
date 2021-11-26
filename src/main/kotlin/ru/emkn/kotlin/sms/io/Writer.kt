package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import mu.KotlinLogging
import ru.emkn.kotlin.sms.FileType
import java.io.File

private val logger = KotlinLogging.logger {}

interface MultilineWritable {
    fun toMultiline(): List<List<String?>>
}

interface SingleLineWritable : MultilineWritable {
    override fun toMultiline(): List<List<String?>> = listOf(toLine())

    fun toLine(): List<String?>
}

fun List<String>.toWritable() = object : SingleLineWritable {
    override fun toLine() = this@toWritable
}

fun String.toWritable() = object : SingleLineWritable {
    override fun toLine() = listOf(this@toWritable)
}

class Writer(private val file: File, val filetype: FileType) {

    val buffer = mutableListOf<MultilineWritable>()

    fun add(el: MultilineWritable) = buffer.add(el)

    fun add(el: String?) {
        if (el != null) buffer.add(el.toWritable())
        else logger.warn { "try to write null string" }
    }

    fun add(el: List<String>) = buffer.add(el.toWritable())

    fun addAll(el: List<MultilineWritable>) = buffer.addAll(el)

    fun clear() = buffer.clear()

    fun write() {
        val lines = buffer.map { it.toMultiline() }.flatten()
        val rowSize = lines.maxOf { it.size }
        val shrunkenLines = lines.map { it + List(rowSize - it.size) { "" } }
        when (filetype) {
            FileType.CSV, FileType.JSON -> csvWriter().writeAll(shrunkenLines, file)
        }
        logger.info { "Written ${buffer.size} objects to ${file.name}" }
        clear()
    }
}