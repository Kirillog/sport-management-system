package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.client.CsvFileReader
import com.sksamuel.hoplite.simpleName
import kotlinx.datetime.LocalDate
import mu.KotlinLogging
import ru.emkn.kotlin.sms.headers
import ru.emkn.kotlin.sms.objects.*
import java.io.File
import java.io.IOException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

private val logger = KotlinLogging.logger { }

class CSVReader(file: File, private val reader: CsvFileReader) : Reader(file) {
    private fun convert(field: String, lineNumber: Int, kType: KType): Any? {
        return when (kType.jvmErasure) {
            Int::class -> field.toIntOrNull()
                ?: throw IllegalArgumentException("Cannot parse $field as Int at ${lineNumber}th position")
            String::class -> {
                require(kType.isMarkedNullable || field.isNotEmpty()) { "Cannot parse empty field as String at ${lineNumber}th position" }
                field.ifEmpty { null }
            }
            List::class -> field.split(",").dropLastWhile(String::isEmpty).map { element ->
                kType.arguments.first().type?.let {
                    convert(element, lineNumber, it)
                }
            }
            CheckPoint::class -> CheckPoint(convert(field, lineNumber, Int::class.starProjectedType) as Int)
            LocalDate::class -> field.let {
                try {
                    val (date, month, year) = field.split(".")
                    LocalDate(year.toInt(), month.toInt(), date.toInt())
                } catch (e: Exception) {
                    throw IllegalArgumentException("Cannot parse $field as Date at ${lineNumber}th position")
                }
            }
            else -> {
                val message = "Cannot convert essential field for ${kType.simpleName}"
                throw IllegalStateException(message)
            }
        }
    }

    private fun <T> objectList(
        table: List<Map<String, String>>,
        kClass: KClass<T>
    ): List<T> where T : Readable {
        val constructor = kClass.primaryConstructor
        require(constructor != null) { "Try to get instance of class without primary constructor" }
        return table.mapIndexed { lineNumber, recordWithHeader ->
            try {
                val parametersWithValues = constructor.parameters.associateWith {
                    val field = recordWithHeader[it.name]
                    require(field != null) { "Error in checking header" }
                    convert(field, lineNumber + 1, it.type)
                }
                constructor.callBy(parametersWithValues)
            } catch (e: IllegalArgumentException) {
                logger.warn { e.message }
                null
            }
        }.filterNotNull()
    }

    private fun <T> correctHeader(parameters: Set<String>, kClass: KClass<T>): Boolean where T : Readable {
        logger.debug { "Header: $parameters" }
        val constructor = kClass.primaryConstructor
        require(constructor != null) { "Try to get instance of class without primary constructor" }
        val correctHeader = constructor.parameters.all {
            parameters.contains(it.name)
        }
        return if (!correctHeader) {
            logger.warn { "${file.name} have incorrect header, so it was ignored" }
            false
        } else
            true
    }

    private fun tableWithHeader(): List<Map<String, String>>? {
        val data = reader.readAllWithHeaderAsSequence().toList().map { record ->
            record.mapKeys {
                if (it.key.toIntOrNull() == null)
                    headers[it.key] ?: throw IOException("Wrong name in header")
                else
                    it.key
            }
        }
        return data.ifEmpty {
            logger.warn { "${file.name} doesn't have header or members, so it was ignored" }
            null
        }
    }

    private fun name(): String? {
        val line = reader.readNext()
        return when (line) {
            null -> {
                logger.warn { "${file.name} is empty, so it was ignored" }
                null
            }
            else -> line[0]
        }
    }

    override fun team(): Team? {
        val name = name() ?: return null
        val table = tableWithHeader() ?: return null
        val correctedTable = table.map { record ->
            record + ("team" to name)
        }
        if (!correctHeader(correctedTable.first().keys, Participant::class))
            return null
        val members = objectList(correctedTable, Participant::class)
        if (members.isEmpty())
            logger.warn { "Team $name is empty" }
        return Team(name, members)
    }

    override fun groupsToCourses(): Map<String, String>? {
        val table = tableWithHeader() ?: return null
        if (!correctHeader(table.first().keys, GroupToCourse::class))
            return null
        val records = objectList(table, GroupToCourse::class)
        return records.associate { it.group to it.course }
    }

    override fun courses(): List<Course>? {
        val table = tableWithHeader() ?: return null
        val correctedTable = table.map { record ->
            val checkPoints = "checkPoints" to record.filterKeys { it.toIntOrNull() != null }.values.joinToString(",")
            record.filterKeys { it.toIntOrNull() == null } + checkPoints
        }
        val header = correctedTable.first().keys
        if (!correctHeader(header, Course::class))
            return null
        val checkPoints = objectList(correctedTable, Course::class)
        if (checkPoints.isEmpty())
            logger.warn { "List of courses is empty" }
        return checkPoints
    }

    override fun events(): List<Event>? {
        val table = tableWithHeader() ?: return null
        val header = table.first().keys
        if (!correctHeader(header, Event::class))
            return null
        val events = objectList(table, Event::class)
        if (events.isEmpty())
            logger.warn { "List of events is empty" }
        return events
    }

    override fun timestamps(): List<TimeStamp>? {
        return TODO()
    }
}