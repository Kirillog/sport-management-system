package ru.emkn.kotlin.sms.View

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import mu.KotlinLogging
import ru.emkn.kotlin.sms.objects.Participant

private val logger = KotlinLogging.logger {}

class ParticipantTableRow(private var participant: Participant) : TableRow {

    companion object {
        val header = TableHeader(listOf(
            "Name", "Surname", "Group", "Birthday Year"
        ))
    }

    val cellNames = mapOf(
        "Name" to TableCell({ participant.name }, ::saveChanges),
        "Surname" to TableCell({ participant.surname }, ::saveChanges),
        "Group" to TableCell({ participant.group }, ::saveChanges),
        "Birthday Year" to TableCell({ participant.birthdayYear.toString() }, ::saveChanges)
    )

    @Composable
    override fun draw() {

        var rowSize by remember { mutableStateOf(IntSize.Zero) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged {
                    rowSize = it
                },
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            for ((_, cell) in cellNames) {
                cell.draw((rowSize.width / cellNames.size).dp)
            }
        }
    }

    override fun saveChanges() {
        logger.info { "save changes for $participant" }
    }
}

class ParticipantsTable(participants: List<Participant>) : Table {

    private val rows = participants.map { ParticipantTableRow(it) }

    @Composable
    override fun draw() {
        Column {
            ParticipantTableRow.header.draw()
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                rows.forEach {
                    it.draw()
                }
            }
        }

    }
}

