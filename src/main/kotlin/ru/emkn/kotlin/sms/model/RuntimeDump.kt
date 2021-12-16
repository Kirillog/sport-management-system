package ru.emkn.kotlin.sms.model

object RuntimeDump {

    var timeStampDump: MutableSet<TimeStamp> = mutableSetOf()
    val participantDump: Map<Participant, List<TimeStamp>> = mapOf()

    fun addTimestamp(timeStamp: TimeStamp) {
        timeStampDump.add(timeStamp)
    }

    fun addAllTimestamps(timeStamps: Set<TimeStamp>) {
        timeStampDump.addAll(timeStamps)
    }

    fun resultsByParticipant() = timeStampDump.groupBy { it.participant }
        .mapValues { it.value.sortedBy(ru.emkn.kotlin.sms.model.TimeStamp::time) }

}