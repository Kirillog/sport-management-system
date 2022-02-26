package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.time
import java.time.LocalTime

object TimestampTable : IntIdTable("timestamps") {
    val time = time("time")
    val checkpointID = reference("checkpoint", CheckpointTable)
    val participantID = reference("participant", ParticipantTable)
}

/**
 * Class for storing information about participant has checked by checkpoint at some time
 */
class Timestamp(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<Timestamp>(TimestampTable) {

        fun create(time: LocalTime, checkpoint: Checkpoint, participant: Participant): Timestamp {
            return Timestamp.new {
                this.time = time
                this.checkpoint = checkpoint
                this.participant = participant
            }
        }

        fun create(time: LocalTime, checkpointName: String, participantID: Int): Timestamp {
            val checkpoint = Checkpoint.findByName(checkpointName)
            val participant = Participant.findById(participantID)
            requireNotNull(participant)
            return create(time, checkpoint, participant)
        }
    }

    var time by TimestampTable.time
    private var checkpointID by TimestampTable.checkpointID
    private var participantID by TimestampTable.participantID

    var checkpoint: Checkpoint
        get() = Checkpoint[checkpointID]
        set(checkpoint) {
            checkpointID = checkpoint.id
        }

    var participant: Participant
        get() = Participant[participantID]
        set(participant) {
            participantID = participant.id
        }

    fun change(time: LocalTime, participantID: Int, checkpointName: String) {
        this.time = time
        this.participantID = EntityID(participantID, ParticipantTable)
        this.checkpointID = Checkpoint.findByName(checkpointName).id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Timestamp

        if (time != other.time) return false
        if (checkpointID != other.checkpointID) return false
        if (participantID != other.participantID) return false

        return true
    }

    override fun hashCode(): Int {
        var result = time.hashCode()
        result = 31 * result + checkpointID.hashCode()
        result = 31 * result + participantID.hashCode()
        return result
    }
}
