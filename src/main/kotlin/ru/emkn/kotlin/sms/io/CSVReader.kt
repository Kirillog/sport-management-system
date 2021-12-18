package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import ru.emkn.kotlin.sms.controller.Creator
import ru.emkn.kotlin.sms.headers
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.io.IOException

private val logger = KotlinLogging.logger { }

/**
 * Represents [Reader] that can read from csv [file].
 *
 */
class CSVReader(file: File) : Reader(file) {
    private val csvReader = csvReader()
    private var buffer: List<List<String>> = csvReader.readAll(file)

    /**
     * Returns list of map, each of which transform header name to field
     *
     * Returns `null` if data [file] is inappropriate
     */
    private fun tableWithHeader(): List<Map<String, String>>? {
        return try {
            val header = buffer.first()
            buffer = buffer.drop(1)
            val table = buffer.map { line ->
                line.mapIndexed { index, field ->
                    Pair(header[index], field)
                }.toMap()
            }
            val data = table.map { record ->
                record.mapKeys {
                    val key = it.key.trim()
                    if (key.toIntOrNull() == null)
                        headers[key] ?: throw IOException("Wrong name in header")
                    else
                        key
                }.mapValues { it.value.trim() }
            }
            data.ifEmpty {
                logger.warn { "${file.name} doesn't have header or members, so it was ignored" }
                null
            }
        } catch (err: NoSuchElementException) {
            logger.warn { "There is no header in ${file.name}" }
            null
        }

    }

    /**
     * Returns name of table
     *
     * Returns `null` if [file] is empty
     */
    private fun name(): String? =
        try {
            val line = buffer.first()
            buffer = buffer.drop(1)
            line[0]
        } catch (err: NoSuchElementException) {
            logger.warn { "${file.name} is empty, so it was ignored" }
            null
        }

    /**
     * Convert start protocol to list of participants
     */
    private fun preprocess(table: List<Map<String, String>>): List<Map<String, String>> {
        var groupName = ""
        return table.mapNotNull { record ->
            val currentGroupName = record.values.filter(String::isNotEmpty)
            if (currentGroupName.size == 1 && currentGroupName.first() != groupName) {
                groupName = currentGroupName.first()
                null
            } else
                record + ("group" to groupName)
        }
    }

    override fun team(): Team? {
        val name = name() ?: return null
        val table = tableWithHeader()?.map { record ->
            record + ("team" to name)
        } ?: return null
        val team = Team.create(name)
        table.mapNotNull {
            try {
                Creator.createParticipantFrom(it)
            } catch (err: IllegalArgumentException) {
                null
            }
        }
        return team
    }

    override fun groups(): Set<Group>? {
        val table = tableWithHeader() ?: return null
        return table.mapNotNull {
            try {
                Creator.createGroupFrom(it)
            } catch (err: IllegalArgumentException) {
                null
            }
        }.toSet()
    }

    override fun courses(): Set<Route>? {
        val table = tableWithHeader() ?: return null
        val routes = table.mapNotNull { record ->
            try {
                val checkPoints =
                    "checkPoints" to record.filterKeys { it.toIntOrNull() != null }.values.joinToString(",")
                val entry = record.filterKeys { it.toIntOrNull() == null } + checkPoints
                Creator.createRouteFrom(entry)
            } catch (err: IllegalArgumentException) {
                null
            }
        }.toSet()
        if (routes.isEmpty())
            logger.warn { "List of courses is empty" }
        return routes
    }

    override fun event(): Event? {
        val table = tableWithHeader() ?: return null
        val events = table.mapNotNull {
            try {
                Creator.createEventFrom(it)
            } catch (err: IllegalArgumentException) {
                null
            }
        }
        if (events.isEmpty())
            logger.warn { "List of events is empty" }
        else if (events.size > 1)
            logger.warn { "There is some events, chose first from them" }
        return events.first()
    }


    override fun timestamps(): Set<Timestamp>? {
        val name = name()?.toIntOrNull() ?: throw IOException("Wrong type of checkPoint id")
        val table = tableWithHeader()?.map { record ->
            record + ("checkPointId" to name.toString())
        } ?: return null
        val timeStamps = table.mapNotNull {
            try {
                Creator.createTimeStampFrom(it)
            } catch (err: IllegalArgumentException) {
                null
            }
        }.toSet()
        if (timeStamps.isEmpty())
            logger.warn { "List of timestamps is empty" }
        return timeStamps
    }

    override fun toss(): Unit? {
        val table = tableWithHeader() ?: return null
        val correctedTable = preprocess(table)
        val participants = correctedTable.mapNotNull {
            try {
                Creator.createParticipantFrom(it)
            } catch (err: IllegalArgumentException) {
                null
            }
        }
        if (participants.isEmpty())
            logger.warn { "List of participants is empty" }
        return Unit
    }

    override fun checkPoints(): Set<Checkpoint>? {
        val table = tableWithHeader() ?: return null
        return table.mapNotNull {
            try {
                Creator.createCheckPointFrom(it)
            } catch (err: IllegalArgumentException) {
                null
            }
        }.toSet()
    }

}