package ru.emkn.kotlin.sms.View

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.emkn.kotlin.sms.objects.Participant
import ru.emkn.kotlin.sms.objects.Team
import java.time.LocalTime


private val participants = listOf(
    Participant("Анна", "Сосницкая", 2013, "Ж10", "0-ПСКОВ", "1р", 101, LocalTime.of(12, 0, 0)),
    Participant("АРТЁМ", "КАЧНОВ", 2008, "МЖ14", "ВЕЛИКОЛУКСКИЙ РАЙОН", null, 128, LocalTime.of(12, 5, 0)),
    Participant("АЛЕКСАНДРА", "ЛОВЦОВА", 2014, "МЖ14", "ВЕЛИКИЕ ЛУКИ", null, 102, LocalTime.of(12, 10, 0)),
    Participant("ЗАХАР", "МАЖАРОВ", 2012, "М10", "ВЕЛИКОЛУКСКИЙ РАЙОН", null, 121, LocalTime.of(13, 45, 0)),
    Participant("РОМАН", "МЕРЦАЛОВ", 2013, "М10", "0-ПСКОВ", "3р", 125, LocalTime.of(13, 55, 0)),
    Participant("Анна", "Сосницкая", 2013, "Ж10", "0-ПСКОВ", "1р", 101, LocalTime.of(12, 0, 0)),
    Participant("АРТЁМ", "КАЧНОВ", 2008, "МЖ14", "ВЕЛИКОЛУКСКИЙ РАЙОН", null, 128, LocalTime.of(12, 5, 0)),
    Participant("АЛЕКСАНДРА", "ЛОВЦОВА", 2014, "МЖ14", "ВЕЛИКИЕ ЛУКИ", null, 102, LocalTime.of(12, 10, 0)),
    Participant("ЗАХАР", "МАЖАРОВ", 2012, "М10", "ВЕЛИКОЛУКСКИЙ РАЙОН", null, 121, LocalTime.of(13, 45, 0)),
    Participant("РОМАН", "МЕРЦАЛОВ", 2013, "М10", "0-ПСКОВ", "3р", 125, LocalTime.of(13, 55, 0)),
    Participant("Анна", "Сосницкая", 2013, "Ж10", "0-ПСКОВ", "1р", 101, LocalTime.of(12, 0, 0)),
    Participant("АРТЁМ", "КАЧНОВ", 2008, "МЖ14", "ВЕЛИКОЛУКСКИЙ РАЙОН", null, 128, LocalTime.of(12, 5, 0)),
    Participant("АЛЕКСАНДРА", "ЛОВЦОВА", 2014, "МЖ14", "ВЕЛИКИЕ ЛУКИ", null, 102, LocalTime.of(12, 10, 0)),
    Participant("ЗАХАР", "МАЖАРОВ", 2012, "М10", "ВЕЛИКОЛУКСКИЙ РАЙОН", null, 121, LocalTime.of(13, 45, 0)),
    Participant("РОМАН", "МЕРЦАЛОВ", 2013, "М10", "0-ПСКОВ", "3р", 125, LocalTime.of(13, 55, 0))
)
private val team = Team("KekMen", participants)

class GUI {


    fun run() = application {
        Window(onCloseRequest = ::exitApplication) {
            app()
        }
    }

    @Preview
    @Composable
    fun app() {

        Column {
            TopAppBar.draw()
            when (TopAppBar.tabState.value) {
                TopAppBar.States.Groups -> TODO()
                TopAppBar.States.Courses -> TODO()
                TopAppBar.States.Teams -> TODO()
                TopAppBar.States.Participants -> ParticipantsTable(participants).draw()
                TopAppBar.States.Timestamps -> TODO()
            }
        }
    }

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
}
