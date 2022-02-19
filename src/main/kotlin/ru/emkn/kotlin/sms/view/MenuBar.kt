package ru.emkn.kotlin.sms.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import ru.emkn.kotlin.sms.controller.Controller

enum class MenuState(val text: String = "") {
    Hided,
    Blocked("Next"),
    Preparing("Toss"),
    Tossed("Result"),
    Result("Finished"),
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun drawMenuBar(gui: GUI, frame: FrameWindowScope, bottomBar: BottomAppBar) {
    val stages = listOf("Toss", "Results", "Finished")
    val menuState = gui.state.value.menuState
    val loader = LoadOrCreate(
        question = "Load database or create new one?",
        loadTitle = "select database file",
        createTitle = "choose path for new database",
        loadAction = {
            Controller.connectDB(it)
            gui.pushDataBaseState()
        },
        createAction = {
            Controller.createDB(it)
            gui.pushState(GUI.State.EditAnnounceData)
        },
        fileExtension = ".mv.db",
        fileExtensionDescription = "Database"
    )
    frame.MenuBar {
        Menu("Database", mnemonic = 'D') {
            Item("Load", onClick = {
                chooseFileAndProcess(
                    bottomBar,
                    loader.loadTitle,
                    loader.fileExtension,
                    loader.fileExtensionDescription,
                    loader.loadAction
                )
            }, shortcut = KeyShortcut(Key.L, ctrl = true))
            Item("Create", onClick = {
                chooseFileAndProcess(
                    bottomBar,
                    loader.createTitle,
                    loader.fileExtension,
                    loader.fileExtensionDescription,
                    loader.createAction
                )
            }, shortcut = KeyShortcut(Key.N, ctrl = true))
        }

        Menu("Navigate", mnemonic = 'T') {
            val text = menuState.text
            Item(
                text, onClick = {
                    when (text) {
                        "Toss" ->
                            StateSwitcher.doToss(gui, bottomBar)
                        "Result" ->
                            StateSwitcher.doResulted(gui, bottomBar)
                        else ->
                            throw IllegalStateException("Wrong menu state")
                    }
                }, shortcut = KeyShortcut(Key.Y, ctrl = true), enabled = menuState >= MenuState.Preparing
                        && menuState <= MenuState.Tossed
            )
            Item(
                "Rollback",
                onClick = {
                    when (text) {
                        "Result" ->
                            StateSwitcher.undoToss(gui, bottomBar)
                        "Finished" ->
                            StateSwitcher.undoResulted(gui, bottomBar)
                        else ->
                            throw IllegalStateException("Wrong menu state")
                    }
                },
                shortcut = KeyShortcut(Key.Z, ctrl = true),
                enabled = menuState >= MenuState.Tossed && menuState <= MenuState.Result
            )
        }
    }
}