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
     * Map from name of [Group] to name of [Route] located in [file].
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
    abstract fun events(): Set<Event>?

    /**
     * [TimeStamp] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun timestamps(): Set<TimeStamp>?

    /**
     * [Participant] list located in [file].
     *
     * Returns `null` if data has incorrect format.
     */
    abstract fun participants(): Set<Participant>?
}
