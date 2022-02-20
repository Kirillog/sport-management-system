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
fun drawMenuBar(gui: View, frame: FrameWindowScope) {
    val menuState = gui.state.value.menuState
    frame.MenuBar {
        Menu("Database", mnemonic = 'D') {
            Item("Load", onClick = {
                chooseFileAndProcess(
                        "select database file",
                        ".mv.db",
                        "Database"
                ) {
                    Controller.connectDB(it)
                    gui.pushDataBaseState()
                }
            }, shortcut = KeyShortcut(Key.L, ctrl = true))
            Item("Create", onClick = {
                chooseFileAndProcess(
                        "choose path for new database",
                        ".mv.db",
                        "Database"
                ) {
                    Controller.createDB(it)
                    gui.pushState(View.State.EditAnnounceData)
                }
            }, shortcut = KeyShortcut(Key.N, ctrl = true))
        }

        Menu("Navigate", mnemonic = 'T') {
            Item(
                    menuState.text, onClick = {
                when (menuState) {
                    MenuState.Preparing ->
                        StateSwitcher.doToss(gui)
                    MenuState.Tossed ->
                        StateSwitcher.doResulted(gui)
                    else ->
                        throw IllegalStateException("Wrong menu state")
                }
            }, shortcut = KeyShortcut(Key.Y, ctrl = true), enabled = menuState >= MenuState.Preparing
                    && menuState <= MenuState.Tossed
            )
            Item(
                    "Rollback",
                    onClick = {
                        when (menuState) {
                            MenuState.Tossed, MenuState.Result ->
                                StateSwitcher.undo(gui)
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