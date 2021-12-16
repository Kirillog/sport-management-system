package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

object TopAppBar {
    enum class States {
        Groups,
        Courses,
        Teams,
        Participants,
        Timestamps
    }

    private val buttons = mapOf(
        States.Groups to TabButton("Group", States.Groups),
        States.Courses to TabButton("Courses", States.Courses),
        States.Teams to TabButton("Teams", States.Teams),
        States.Participants to TabButton("Participants", States.Participants),
        States.Timestamps to TabButton("Timestamps", States.Timestamps)
    )

    var tabState = mutableStateOf(States.Participants)

    class TabButton(private val text: String, private val state: States) {
        @Composable
        fun draw() {

            Button(
                onClick = {
                    tabState.value = state
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (tabState.value == state) Color.Gray else Color.DarkGray
                )
            ) {
                Text(text)
            }
        }
    }

    @Composable
    fun draw() {
        TopAppBar {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for ((_, button) in buttons) {
                    button.draw()
                }
            }
        }
    }
}