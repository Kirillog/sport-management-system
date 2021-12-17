<<<<<<< HEAD
package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.sksamuel.hoplite.simpleName
import mu.KotlinLogging
import ru.emkn.kotlin.sms.headers
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

private val logger = KotlinLogging.logger { }

/**
 * Represents [Reader] that can read from csv [file].
 *
 */
class CSVReader(file: File) : Reader(file) {
    private val csvReader = csvReader()
    private var buffer: List<List<String>> = csvReader.readAll(file)

    /**
     * Converts [field] in [lineNumber] to [kType].
     *
     * Throws [IllegalArgumentException] if [field] cannot be converted to [kType].
     */
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
            Checkpoint::class -> Checkpoint(convert(field, lineNumber, Int::class.starProjectedType) as Int)
            LocalDate::class ->
                try {
                    val (date, month, year) = field.split(".").map(String::toInt)
                    LocalDate.of(year, month, date)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Cannot parse $field as Date at ${lineNumber}th position")
                }
            LocalTime::class ->
                try {
                    val (hours, minutes, seconds) = field.split(":").map(String::toInt)
                    LocalTime.of(hours, minutes, seconds)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Cannot parse $field as Time at ${lineNumber}th position")
                }
            else -> {
                val message = "Cannot convert essential field for ${kType.simpleName}"
                throw IllegalStateException(message)
            }
        }
    }

    /**
     * Converts [table] to [Readable] objects using their [constructor]
     */
    private fun <T> objectList(
        table: List<Map<String, String>>,
        constructor: KFunction<T>
    ): Set<T> {
        return table.mapIndexed { lineNumber, recordWithHeader ->
            try {
                val parametersWithValues = constructor.parameters.associateWith {
                    val field = recordWithHeader[it.name]
                    requireNotNull(field) { "Error in checking header" }
                    convert(field, lineNumber + 1, it.type)
                }
                constructor.callBy(parametersWithValues)
            } catch (e: IllegalArgumentException) {
                logger.warn { e.message }
                null
            }
        }.filterNotNull().toSet()
    }

    /**
     * Converts [parameters] to [Readable] [KClass] constructor
     */
    private fun <T : Any> constructorByHeader(parameters: Set<String>, kClass: KClass<T>): KFunction<T>? {
        logger.debug { "Header: $parameters" }
        val constructors = kClass.constructors
        require(constructors.isNotEmpty()) { "Try to get instance of class without constructors" }
        val correctHeader = constructors.find { constructor ->
            parameters.size == constructor.parameters.size && constructor.parameters.all {
                parameters.contains(it.name)
            }
        }
        return correctHeader
    }

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
        val constructor = constructorByHeader(table.first().keys, Participant::class)
        requireNotNull(constructor) { "Team doesn't have appropriate constructors" }
        val team = Team(name)
        team.members.addAll(objectList(table, constructor))
        if (team.members.isEmpty())
            logger.warn { "Team $name is empty" }
        return team
    }

    override fun groups(): Set<Group>? {
        val table = tableWithHeader() ?: return null
        val constructor = constructorByHeader(table.first().keys, Group::class)
        requireNotNull(constructor) { "Groups to courses doesn't have appropriate constructors" }
        return objectList(table, constructor)
    }

    override fun courses(): Set<Route>? {
        val table = tableWithHeader()?.map { record ->
            val checkPoints = "checkPoints" to record.filterKeys { it.toIntOrNull() != null }.values.joinToString(",")
            record.filterKeys { it.toIntOrNull() == null } + checkPoints
        } ?: return null
        val constructor = constructorByHeader(table.first().keys, Route::class)
        requireNotNull(constructor) { "Courses doesn't have appropriate constructors" }
        val checkPoints = objectList(table, constructor)
        if (checkPoints.isEmpty())
            logger.warn { "List of courses is empty" }
        return checkPoints
    }

    override fun event(): Event? {
        val table = tableWithHeader() ?: return null
        val constructor = constructorByHeader(table.first().keys, Event::class)
        requireNotNull(constructor) { "Events doesn't have appropriate constructors" }
        val events = objectList(table, constructor)
        if (events.isEmpty())
            logger.warn { "List of events is empty" }
        else if (events.size > 1)
            logger.warn { "There is some events, chose first from them" }
        return events.first()
    }


    override fun timestamps(): Set<TimeStamp>? {
        val name = name()?.toIntOrNull() ?: throw IOException("Wrong type of checkPoint id")
        val table = tableWithHeader()?.map { record ->
            record + ("checkPointId" to name.toString())
        } ?: return null
        val constructor = constructorByHeader(table.first().keys, TimeStamp::class)
        requireNotNull(constructor) { "Timestamps doesn't have appropriate constructors" }
        val timeStamps = objectList(table, constructor)
        if (timeStamps.isEmpty())
            logger.warn { "List of timestamps is empty" }
        return timeStamps
    }

    override fun toss(): Unit? {
        val table = tableWithHeader() ?: return null
        val correctedTable = preprocess(table)
        val constructor = constructorByHeader(correctedTable.first().keys, Participant::class)
        requireNotNull(constructor) { "Participant doesn't have appropriate constructors" }
        val participants = objectList(correctedTable, constructor).toList()
        if (participants.isEmpty())
            logger.warn { "List of participants is empty" }
        return Unit
    }

=======
package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.client.CsvFileReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.sksamuel.hoplite.simpleName
import mu.KotlinLogging
import ru.emkn.kotlin.sms.headers
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

private val logger = KotlinLogging.logger { }

/**
 * Represents [Reader] that can read from csv [file].
 *
 * @param reader provides reading with header.
 */
class CSVReader(file: File) : Reader(file) {
    private val csvReader = csvReader()
    private lateinit var reader : CsvFileReader
    init {
        csvReader.open(file) {
            reader = this
        }
    }
    /**
     * Converts [field] in [lineNumber] to [kType].
     *
     * Throws [IllegalArgumentException] if [field] cannot be converted to [kType].
     */
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
            Checkpoint::class -> Checkpoint(convert(field, lineNumber, Int::class.starProjectedType) as Int)
            LocalDate::class ->
                try {
                    val (date, month, year) = field.split(".").map(String::toInt)
                    LocalDate.of(year, month, date)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Cannot parse $field as Date at ${lineNumber}th position")
                }
            LocalTime::class ->
                try {
                    val (hours, minutes, seconds) = field.split(":").map(String::toInt)
                    LocalTime.of(hours, minutes, seconds)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Cannot parse $field as Time at ${lineNumber}th position")
                }
            else -> {
                val message = "Cannot convert essential field for ${kType.simpleName}"
                throw IllegalStateException(message)
            }
        }
    }

    /**
     * Converts [table] to [Readable] objects using their [constructor]
     */
    private fun <T> objectList(
        table: List<Map<String, String>>,
        constructor: KFunction<T>
    ): Set<T> {
        return table.mapIndexed { lineNumber, recordWithHeader ->
            try {
                val parametersWithValues = constructor.parameters.associateWith {
                    val field = recordWithHeader[it.name]
                    requireNotNull(field) { "Error in checking header" }
                    convert(field, lineNumber + 1, it.type)
                }
                constructor.callBy(parametersWithValues)
            } catch (e: IllegalArgumentException) {
                logger.warn { e.message }
                null
            }
        }.filterNotNull().toSet()
    }

    /**
     * Converts [parameters] to [Readable] [KClass] constructor
     */
    private fun <T : Any> constructorByHeader(parameters: Set<String>, kClass: KClass<T>): KFunction<T>? {
        logger.debug { "Header: $parameters" }
        val constructors = kClass.constructors
        require(constructors.isNotEmpty()) { "Try to get instance of class without constructors" }
        val correctHeader = constructors.find { constructor ->
            parameters.size == constructor.parameters.size && constructor.parameters.all {
                parameters.contains(it.name)
            }
        }
        return correctHeader
    }

    /**
     * Returns list of map, each of which transform header name to field
     *
     * Returns `null` if data [file] is inappropriate
     */
    private fun tableWithHeader(): List<Map<String, String>>? {
        val data = reader.readAllWithHeaderAsSequence().toList().map { record ->
            record.mapKeys {
                val key = it.key.trim()
                if (key.toIntOrNull() == null)
                    headers[key] ?: throw IOException("Wrong name in header")
                else
                    key
            }.mapValues { it.value.trim() }
        }
        return data.ifEmpty {
            logger.warn { "${file.name} doesn't have header or members, so it was ignored" }
            null
        }
    }

    /**
     * Returns name of table
     *
     * Returns `null` if [file] is empty
     */
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
        val constructor = constructorByHeader(table.first().keys, Participant::class)
        requireNotNull(constructor) { "Team doesn't have appropriate constructors" }
//        TODO()
//        val team = Team(name)
//        team.members.addAll(objectList(table, constructor))
//        if (team.members.isEmpty())
//            logger.warn { "Team $name is empty" }
//        return team
        return null
    }

    override fun groups(): Set<Group>? {
        val table = tableWithHeader() ?: return null
        val constructor = constructorByHeader(table.first().keys, Group::class)
        requireNotNull(constructor) { "Groups to courses doesn't have appropriate constructors" }
        return objectList(table, constructor)
    }

    override fun courses(): Set<Route>? {
        val table = tableWithHeader()?.map { record ->
            val checkPoints = "checkPoints" to record.filterKeys { it.toIntOrNull() != null }.values.joinToString(",")
            record.filterKeys { it.toIntOrNull() == null } + checkPoints
        } ?: return null
        val constructor = constructorByHeader(table.first().keys, Route::class)
        requireNotNull(constructor) { "Courses doesn't have appropriate constructors" }
        val checkPoints = objectList(table, constructor)
        if (checkPoints.isEmpty())
            logger.warn { "List of courses is empty" }
        return checkPoints
    }

    override fun event(): Event? {
        val table = tableWithHeader() ?: return null
        val constructor = constructorByHeader(table.first().keys, Event::class)
        requireNotNull(constructor) { "Events doesn't have appropriate constructors" }
        val events = objectList(table, constructor)
        if (events.isEmpty())
            logger.warn { "List of events is empty" }
        else if (events.size > 1)
            logger.warn { "There is some events, chose first from them" }
        return events.first()
    }


    override fun timestamps(): Set<TimeStamp>? {
        val name = name()?.toIntOrNull() ?: throw IOException("Wrong type of checkPoint id")
        val table = tableWithHeader()?.map { record ->
            record + ("checkPointId" to name.toString())
        } ?: return null
        val constructor = constructorByHeader(table.first().keys, TimeStamp::class)
        requireNotNull(constructor) { "Timestamps doesn't have appropriate constructors" }
        val timeStamps = objectList(table, constructor)
        if (timeStamps.isEmpty())
            logger.warn { "List of timestamps is empty" }
        return timeStamps
    }

    override fun toss(): Map<Participant, LocalTime>? {
        val table = tableWithHeader() ?: return null
        val correctedTable = preprocess(table)
        val constructor = constructorByHeader(correctedTable.first().keys, Participant::class)
        requireNotNull(constructor) { "Participant doesn't have appropriate constructors" }
        val participants = objectList(correctedTable, constructor).toList()
        if (participants.isEmpty())
            logger.warn { "List of participants is empty" }
        val startTimes = correctedTable.mapIndexed { number, line ->
            convert(line["id"] ?: "", number, Participant::id.returnType) as Int to
                    convert(line["startTime"] ?: "", number, Participant::startTime.returnType) as LocalTime
        }.toMap()
        return null
//        TODO()
//        return participants.associateWith {
//            startTimes[it.id] ?: throw IllegalStateException("Cannot find ${it.id} in startTimes")
//        }
    }

>>>>>>> 2247991 (start model refactoring for database)
}