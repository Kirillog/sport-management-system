package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import ru.emkn.kotlin.sms.MAX_TEXT_FIELD_SIZE
import ru.emkn.kotlin.sms.io.Loader
import java.time.LocalTime
import kotlin.random.Random

object TossTable : IntIdTable("toss") {
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
    val surname: Column<String> = varchar("surname", MAX_TEXT_FIELD_SIZE)
    val birthdayYear: Column<Int> = integer("birthdayYear")
    val grade: Column<String?> = varchar("grade", MAX_TEXT_FIELD_SIZE).nullable()
}

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
        loader.loadToss()
        state = State.TOSSED
    }

    fun addAllParticipant() {
        require(state == State.PREPARING)
//        Participant.byId.values.forEach { this.addParticipant(it) } TODO()
    }

    fun getParticipantStartTime(participant: Participant): LocalTime {
        require(state == State.TOSSED)
        return startTimeByParticipant.getOrElse(participant) {
            throw IllegalStateException("This participant ${participant.id} has not been tossed")
        }
    }

    open fun build() {
        var currentTime = LocalTime.NOON
        val deltaMinutes = 5L
        participants.groupBy { it.groupID }.forEach { (groupID, members) ->
            members.shuffled(Random(0)).forEach { participant ->
                startTimeByParticipant[participant] = currentTime
                currentTime = currentTime.plusMinutes(deltaMinutes)
            }
        }
        state = State.TOSSED
    }
}
