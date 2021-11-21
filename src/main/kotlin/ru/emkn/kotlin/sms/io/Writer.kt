package ru.emkn.kotlin.sms.io

import mu.KotlinLogging
import java.io.File
import ru.emkn.kotlin.sms.Filetype

private val logger = KotlinLogging.logger {}

interface Writable {

    fun toString(filetype: Filetype): String {
        return when (filetype) {
            Filetype.JSON -> toJSON()
            Filetype.CSV -> toCSV()
        }
    }

    fun toJSON(): String

    fun toCSV(): String
}

fun String.toWritable() = object : Writable {

    override fun toJSON() = "${this@toWritable} string to json"

    override fun toCSV() = "${this@toWritable} string to csv"
}

class Writer(private val file: File, val filetype: Filetype) {

    val buffer = mutableListOf<Writable>()

    fun add(el: Writable) = buffer.add(el)

    fun add(el: String) = buffer.add(el.toWritable())

    fun addAll(el: List<Writable>) = buffer.addAll(el)

    fun clear() = buffer.clear()

    private fun generate(): String = buffer.joinToString("\n") { it.toString(filetype) }

    fun append() {
        file.appendText(generate())
        clear()
    }

    fun write() {
        file.writeText(generate())
        clear()
    }
}