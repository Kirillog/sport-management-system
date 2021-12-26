package ru.emkn.kotlin.sms.io

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.model.*
import java.time.LocalTime

/**
 * Provides methods for reading main structures from [file].
 *
 * All methods return `null` if data could not be read.
 */
interface Reader {
    /**
     * [Team] located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    fun team(): List<Map<ObjectFields, String>>?

    /**
     * [Group] located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    fun groups(): List<Map<ObjectFields, String>>?

    /**
     * [Route] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    fun courses(): List<Map<ObjectFields, String>>?

    /**
     * [Event] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    fun event(): List<Map<ObjectFields, String>>?

    /**
     * [Timestamp] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    fun timestamps(): List<Map<ObjectFields, String>>?

    /**
     * Map from [Participant] to [LocalTime] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    fun toss(): List<Map<ObjectFields, String>>?

    fun checkPoints(): List<Map<ObjectFields, String>>?
}
