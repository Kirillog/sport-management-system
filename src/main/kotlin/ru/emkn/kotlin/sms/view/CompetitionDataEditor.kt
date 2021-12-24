package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.emkn.kotlin.sms.view.tables.CheckpointTable
import ru.emkn.kotlin.sms.view.tables.EventTable
import ru.emkn.kotlin.sms.view.tables.draw

object CompetitionDataEditor {
    enum class EditCompetitionState {
        EventEditing,
        CheckpointsEditing,
        RoutesEditing
    }


    var state = mutableStateOf(EditCompetitionState.EventEditing)
}

@Composable
fun drawCompetitionDataEditor(gui: GUI) {

    val eventTable = remember { EventTable() }
    val checkpointTable = remember { CheckpointTable() }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            draw(
                ActionButton("events") {
                    CompetitionDataEditor.state.value = CompetitionDataEditor.EditCompetitionState.EventEditing
                }
            )
            draw(
                ActionButton("checkpoints") {
                    CompetitionDataEditor.state.value = CompetitionDataEditor.EditCompetitionState.CheckpointsEditing
                }
            )
            draw(
                ActionButton("Routes") {
                    TODO()
                }
            )
        }
        when (CompetitionDataEditor.state.value) {
            CompetitionDataEditor.EditCompetitionState.EventEditing -> draw(gui, eventTable)
            CompetitionDataEditor.EditCompetitionState.CheckpointsEditing -> draw(gui, checkpointTable)
            CompetitionDataEditor.EditCompetitionState.RoutesEditing -> TODO()
        }
    }
}