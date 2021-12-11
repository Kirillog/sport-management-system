package ru.emkn.kotlin.sms.model

class RuntimeDump() {

    fun addTimestamp(timeStamp: TimeStamp) {
        TODO("Add checkpoint to checkPointDump")
    }

    fun addAllTimestamps(timeStamps: Set<TimeStamp>) {
        TODO("Add checkpoint to checkPointDump")
    }

    fun completeDump() {
        TODO("fill participantDump by checkPointDump")
        //TODO("А ещё лучше автоматически добавлять сразу в addCheckpoint")
    }

    val checkPointDump: MutableMap<CheckPoint, List<TimeStamp>> = mutableMapOf()
    val participantDump: Map<Participant, List<TimeStamp>> = mapOf()
}
