package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.GUI.TopAppBar.States.Groups
import ru.emkn.kotlin.sms.objects.Participant
import ru.emkn.kotlin.sms.objects.Team
import java.time.LocalTime

private val logger = KotlinLogging.logger {}


fun main(args: Array<String>): Unit = mainBody {
    val gui = GUI()
    gui.run()

//    logger.info { "Program started" }
//    val parsedArgs = ArgParser(args).parseInto(::ArgumentsFormat)
//    val competitionPath = Path(parsedArgs.competitionsRoot).resolve(parsedArgs.competitionName)
//    try {
//        when (parsedArgs.target) {
//            Target.TOSS -> tossTarget(competitionPath)
//            Target.PERSONAL_RESULT -> personalResultsTarget(competitionPath)
//            Target.TEAM_RESULT -> teamResultsTarget(competitionPath)
//        }
//
//        logger.info { "Program successfully finished" }
//    } catch (error: Exception) {
//        logger.info { "Wow, that's a big surprise, program was fault" }
//        logger.error { error.message }
//    }
}

class TableCell(private val contentGetter: () -> String, private val contentSetter: (String) -> Unit, ) {

    @Composable
    fun draw() {

    }
}

class TeamTable(val team: Team) {

    val memberToRow = team.members.associateWith { participant ->
        listOf(
            TableCell(
                contentGetter = {
                    participant.name
                },
                contentSetter = {
                    participant.name = it
                }
            ),
            TableCell(
                contentGetter = {
                    participant.team
                },
                contentSetter = {
                    participant.team = it
                }
            )
        )
    }

    @Composable
    fun draw() {
        for ((member, tableLine) )
    }
}

class GUI {

    private val participants = listOf(
        Participant("Анна", "Сосницкая", 2013, "Ж10", "0-ПСКОВ", "1р", 101, LocalTime.of(12, 0, 0)),
        Participant("АРТЁМ", "КАЧНОВ", 2008, "МЖ14", "ВЕЛИКОЛУКСКИЙ РАЙОН", null, 128, LocalTime.of(12, 5, 0)),
        Participant("АЛЕКСАНДРА", "ЛОВЦОВА", 2014, "МЖ14", "ВЕЛИКИЕ ЛУКИ", null, 102, LocalTime.of(12, 10, 0)),
        Participant("ЗАХАР", "МАЖАРОВ", 2012, "М10", "ВЕЛИКОЛУКСКИЙ РАЙОН", null, 121, LocalTime.of(13, 45, 0)),
        Participant("РОМАН", "МЕРЦАЛОВ", 2013, "М10", "0-ПСКОВ", "3р", 125, LocalTime.of(13, 55, 0))
    )
    private val team = Team("KekMen", participants)

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
            when (TopAppBar.tabState) {
                TopAppBar.States.Groups -> {/* todo */ }
                TopAppBar.States.Courses -> TODO()
                TopAppBar.States.Teams -> TeamTable(team).draw()
                TopAppBar.States.Participants -> TODO()
                TopAppBar.States.Timestamps -> TODO()
            }
        }
    }

    @Preview
    @Composable
    fun drawTeam(team: Team) {
        val lines = team.toMultiline()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (i in lines[1].indices) {
                Column {
                    for (a in lines.subList(1, lines.lastIndex)) {
                        Text(a[i] ?: "")
                    }
                }
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
            Groups to TabButton("Group", Groups),
            States.Courses to TabButton("Courses", States.Courses),
            States.Teams to TabButton("Teams", States.Teams),
            States.Participants to TabButton("Participants", States.Participants),
            States.Timestamps to TabButton("Timestamps", States.Timestamps)
        )

        var tabState by remember { mutableStateOf(Groups) }

        class TabButton(private val text: String, private val state: States) {
            @Composable
            fun draw() {

                Button(
                    onClick = {
                        tabState = state
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (tabState == state) Color.Gray else Color.DarkGray
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
