package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import mu.KotlinLogging
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.view.tables.Table
import java.io.File

private val logger = KotlinLogging.logger {}

/**
 * Interface for classes that can be printed by [Writer] and takes up more than one line
 */
interface MultilineWritable {
    fun toMultiline(): List<List<Any?>>
}

/**
 * Interface for classes that can be printed by [Writer] and takes up one line
 */
interface SingleLineWritable {
    fun toLine(): List<Any?>
}

/**
 * Class for comfortable writing objects implemented [MultilineWritable] and [SingleLineWritable] to supported file types.
 * It works on the principle of a buffer. You can add([Writer.add] and [Writer.addAll]) objects to the buffer
 * and write them all when you want with [Writer.write]. After write buffer always clearing
 */
class Writer(private val file: File, val filetype: FileType) {

    val buffer = mutableListOf<List<Any?>>()

    fun <T : SingleLineWritable> add(el: T, formatter: (T) -> List<Any?> = { it.toLine() }) =
        buffer.add(formatter(el))

    fun <T : SingleLineWritable> addAllLines(el: List<T>, formatter: (T) -> List<Any?> = { it.toLine() }) =
        buffer.addAll(el.map { formatter(it) })

    fun <T : MultilineWritable> add(el: T, formatter: (T) -> List<List<Any?>> = { it.toMultiline() }) =
        buffer.addAll(formatter(el))

    fun <T : MultilineWritable> addAll(el: List<T>, formatter: (T) -> List<List<Any?>> = { it.toMultiline() }) =
        buffer.addAll(el.map { formatter(it) }.flatten())

    fun add(el: Any?) {
        if (el != null) buffer.add(listOf(el))
        else logger.warn { "try to write null string" }
    }

    fun add(el: List<Any?>) = buffer.add(el)

    fun addAll(el: List<List<Any?>>) = buffer.addAll(el)

    fun <T> add(table: Table<T>) {
        this.add(table.header.visibleColumns.map {it.title})
        table.sortedFilteredRows.forEach { row ->
            table.header.visibleColumns.map { visibleColumn ->
                row.cells[visibleColumn.field]?.getText?.let { it() } ?: throw IllegalStateException("Broken table")
            }.also { data ->
                this.add(data)
            }
        }
    }

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