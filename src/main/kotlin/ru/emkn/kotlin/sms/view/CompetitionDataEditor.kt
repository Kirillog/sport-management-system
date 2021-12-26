package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.emkn.kotlin.sms.view.tables.*

object CompetitionDataEditor {
    enum class EditCompetitionState {
        EventEditing,
        CheckpointsEditing,
        RoutesEditing,
        TeamsEditing,
        GroupsEditing,
        ParticipantsEditing
    }
    var state = mutableStateOf(EditCompetitionState.EventEditing)
}

@Composable
fun drawCompetitionDataEditor(gui: GUI) {

    val eventTable = remember { EventTable() }
    val checkpointTable = remember { CheckpointTable() }
    val routeTable = remember { RouteTable() }
    val teamTable = remember { TeamTable() }
    val groupTable = remember { GroupTable() }
    val participantTable = remember { ParticipantsTable() }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            draw(
                ActionButton("Events") {
                    CompetitionDataEditor.state.value = CompetitionDataEditor.EditCompetitionState.EventEditing
                }
            )
            draw(
                ActionButton("Checkpoints") {
                    CompetitionDataEditor.state.value = CompetitionDataEditor.EditCompetitionState.CheckpointsEditing
                }
            )
            draw(
                ActionButton("Routes") {
                    CompetitionDataEditor.state.value = CompetitionDataEditor.EditCompetitionState.RoutesEditing
                }
            )
            draw(
                ActionButton("Participants") {
                    CompetitionDataEditor.state.value = CompetitionDataEditor.EditCompetitionState.ParticipantsEditing
                }
            )
            draw(
                ActionButton("Teams") {
                    CompetitionDataEditor.state.value = CompetitionDataEditor.EditCompetitionState.TeamsEditing
                }
            )
            draw(
                ActionButton("Groups") {
                    CompetitionDataEditor.state.value = CompetitionDataEditor.EditCompetitionState.GroupsEditing
                }
            )
        }
        draw(gui,
            when (CompetitionDataEditor.state.value) {
                CompetitionDataEditor.EditCompetitionState.EventEditing -> eventTable
                CompetitionDataEditor.EditCompetitionState.CheckpointsEditing -> checkpointTable
                CompetitionDataEditor.EditCompetitionState.RoutesEditing -> routeTable
                CompetitionDataEditor.EditCompetitionState.ParticipantsEditing -> participantTable
                CompetitionDataEditor.EditCompetitionState.TeamsEditing -> teamTable
                CompetitionDataEditor.EditCompetitionState.GroupsEditing -> groupTable
                else -> TODO()
            }
        )
    }
}