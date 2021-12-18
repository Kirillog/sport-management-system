package ru.emkn.kotlin.sms.model

object RuntimeDump {

    var timestampDump: MutableSet<Timestamp> = mutableSetOf()
    val participantDump: Map<Participant, List<Timestamp>> = mapOf()

    fun addTimestamp(timeStamp: Timestamp) {
        timestampDump.add(timeStamp)
    }

    fun addAllTimestamps(timestamps: Set<Timestamp>) {
        timestampDump.addAll(timestamps)
    }

    fun resultsByParticipant() = timestampDump.groupBy { it.participant }
        .mapValues { it.value.sortedBy(ru.emkn.kotlin.sms.model.Timestamp::time) }

}