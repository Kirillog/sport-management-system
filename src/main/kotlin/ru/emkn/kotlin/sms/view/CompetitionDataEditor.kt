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
    fun draw() {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ActionButton("events") {
                    state.value = EditCompetitionState.EventEditing
                }.draw()
                ActionButton("checkpoints") {
                    state.value = EditCompetitionState.CheckpointsEditing
                }.draw()
                ActionButton("Routes") {
                    TODO()
                }.draw()
            }
            when (state.value) {
                EditCompetitionState.EventEditing -> GUI.eventTable.value.draw()
                EditCompetitionState.CheckpointsEditing -> GUI.checkpointTable.value.draw()
                EditCompetitionState.RoutesEditing -> TODO()
            }
        }
    }
}