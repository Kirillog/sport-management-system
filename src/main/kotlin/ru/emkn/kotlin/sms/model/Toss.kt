package ru.emkn.kotlin.sms.model

import ru.emkn.kotlin.sms.io.Loader
import java.time.LocalTime
import kotlin.properties.ReadOnlyProperty
import kotlin.random.Random
import kotlin.reflect.KProperty

open class Toss {

    enum class State {
        PREPARING, TOSSED
    }

    var state = State.PREPARING
        protected set
    protected val participants = mutableSetOf<Participant>()
    val startTimeByParticipant = mutableMapOf<Participant, LocalTime>()

    fun addParticipant(participant: Participant) {
        require(state == State.PREPARING)
        participants.add(participant)
    }

    fun build(loader: Loader) {
        startTimeByParticipant.putAll(loader.loadToss())
        state = State.TOSSED
    }

    fun addAllParticipant() {
        require(state == State.PREPARING)
        Participant.byId.values.forEach { this.addParticipant(it) }
    }

    fun getParticipantStartTime(participant: Participant): LocalTime {
        require(state == State.TOSSED)
        return startTimeByParticipant.getOrElse(participant) {
            throw IllegalStateException("This participant ${participant.id} has not been tossed")
        }
    }

    inner class Entrypoint : ReadOnlyProperty<Participant, LocalTime> {
        override fun getValue(thisRef: Participant, property: KProperty<*>): LocalTime {
            require(state == State.TOSSED)
            return startTimeByParticipant[thisRef]
                ?: throw IllegalStateException("Participant ${thisRef.id} has not been tossed")
        }
    }

    open fun build() {
        var currentTime = LocalTime.NOON
        val deltaMinutes = 5L
        participants.groupBy { it.group }.forEach { (group, members) ->
            members.shuffled(Random(0)).forEach { participant ->
                startTimeByParticipant[participant] = currentTime
                currentTime = currentTime.plusMinutes(deltaMinutes)
            }
        }
        state = State.TOSSED
    }
}