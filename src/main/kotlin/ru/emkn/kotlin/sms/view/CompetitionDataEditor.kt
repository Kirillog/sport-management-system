package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier

class CompetitionDataEditor {
    enum class EditCompetitionState {
        EventEditing,
        CheckpointsEditing,
        RoutesEditing
    }

    companion object {
        var state = mutableStateOf(EditCompetitionState.EventEditing)
    }

    @Composable
    fun draw(gui: GUI) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ActionButton("events") {
                    state.value = EditCompetitionState.EventEditing
                }.draw(gui)
                ActionButton("checkpoints") {
                    state.value = EditCompetitionState.CheckpointsEditing
                }.draw(gui)
                ActionButton("Routes") {
                    TODO()
                }.draw(gui)
            }
            when (state.value) {
                EditCompetitionState.EventEditing -> gui.eventTable.value.draw(gui)
                EditCompetitionState.CheckpointsEditing -> gui.checkpointTable.value.draw(gui)
                EditCompetitionState.RoutesEditing -> TODO()
            }
        }
    }
}