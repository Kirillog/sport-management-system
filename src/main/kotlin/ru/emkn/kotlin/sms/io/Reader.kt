package ru.emkn.kotlin.sms.io

import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.time.LocalTime

/**
 * Provides methods for reading main structures from [file].
 *
 * All methods return `null` if data could not be read.
 */
abstract class Reader(protected val file: File) {
    /**
     * [Team] located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun team(): Team?

    /**
     * [Group] located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun groups(): Set<Group>?

    /**
     * [Route] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun courses(): Set<Route>?

    /**
     * [Event] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun event(): Event?

    /**
     * [Timestamp] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun timestamps(): Set<Timestamp>?

    /**
     * Map from [Participant] to [LocalTime] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun toss(): Unit?

    abstract fun checkPoints(): Set<Checkpoint>?
}
