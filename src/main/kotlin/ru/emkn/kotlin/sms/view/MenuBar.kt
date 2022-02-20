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
fun drawMenuBar(view: View, frame: FrameWindowScope) {
    val menuState = view.state.value.menuState
    if (menuState == MenuState.Hided)
        return
    frame.MenuBar {
        Menu("Database", mnemonic = 'D') {
            Item("Load", onClick = {
                chooseFileAndProcess(
                    "select database file",
                    ".mv.db",
                    "Database"
                ) {
                    Controller.disconnectDB()
                    Controller.connectDB(it)
                    view.pushDataBaseState()
                }
            }, shortcut = KeyShortcut(Key.L, ctrl = true))
            Item("Create", onClick = {
                chooseFileAndProcess(
                    "choose path for new database",
                    ".mv.db",
                    "Database"
                ) {
                    Controller.disconnectDB()
                    Controller.createDB(it)
                    view.pushState(View.State.EditAnnounceData)
                }
            }, shortcut = KeyShortcut(Key.N, ctrl = true))
        }

        Menu("Navigate", mnemonic = 'T') {
            Item(
                menuState.text, onClick = {
                    when (menuState) {
                        MenuState.Preparing ->
                            StateSwitcher.doToss(view)
                        MenuState.Tossed ->
                            StateSwitcher.doResulted(view)
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
                            StateSwitcher.undo(view)
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