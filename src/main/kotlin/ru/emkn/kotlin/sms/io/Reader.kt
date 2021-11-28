package ru.emkn.kotlin.sms.io

import ru.emkn.kotlin.sms.objects.*
import java.io.File

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
     * Map from name of [Group] to name of [Course] located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun groupsToCourses(): Map<String, String>?

    /**
     * [Course] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun courses(): List<Course>?

    /**
     * [Event] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun events(): List<Event>?

    /**
     * [TimeStamp] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun timestamps(): List<TimeStamp>?

    /**
     * [Participant] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun participants(): List<Participant>?
}
