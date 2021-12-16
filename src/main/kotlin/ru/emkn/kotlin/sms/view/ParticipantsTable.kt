package ru.emkn.kotlin.sms.view

import androidx.compose.ui.Modifier
import mu.KotlinLogging
import ru.emkn.kotlin.sms.objects.Participant

private val logger = KotlinLogging.logger {}

class ParticipantsTable(participants: List<Participant>) : Table<Participant>() {

    override val header = TableHeader(
        listOf(
            TableColumn<Participant>("Номер", "id") { { it.id.toString() } },
            TableColumn < Participant >("Имя", "name") { { it.name } },
            TableColumn<Participant>("Фамилия", "surname") { { it.surname } },
            TableColumn<Participant>("Группа", "group") { { it.group } },
            TableColumn<Participant>("Год рождения", "birthdayYear") { { it.birthdayYear.toString() } }
        )
    )

    inner class ParticipantTableRow(private var participant: Participant) : TableRow() {

        override val cells = header.makeTableCells(participant, ::saveChanges)

        override fun saveChanges() {
            val changes = cells.map { it.key to it.value.newText.value }.toMap()
            controllerSaver(participant, changes)
            logger.info { "save changes for $participant" }
        }
    }

    override val rows = participants.map { ParticipantTableRow(it) }
}

fun controllerSaver(participant: Participant, changes: Map<String, String>) {

}
