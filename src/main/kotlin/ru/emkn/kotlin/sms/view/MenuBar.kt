package ru.emkn.kotlin.sms.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import ru.emkn.kotlin.sms.controller.CompetitionController


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun drawMenuBar(gui: GUI, frame: FrameWindowScope, bottomBar: BottomAppBar) {
    val loader = LoadOrCreate(
        question = "Load database or create new one?",
        loadTitle = "select database file",
        createTitle = "choose path for new database",
        loadAction = {
            CompetitionController.connectDB(it)
            gui.pushDataBaseState()
        },
        createAction = {
            CompetitionController.createDB(it)
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
            Item("Toss", onClick = {
                toss(gui, bottomBar)
            }, shortcut = KeyShortcut(Key.T, ctrl = true))
            Item("Edit", onClick = {
                TODO()
            })
        }
/*        Menu("Actions", mnemonic = 'A') {
            CheckboxItem(
                "Advanced settings",
                checked = isSubmenuShowing,
                onCheckedChange = {
                    isSubmenuShowing = !isSubmenuShowing
                }
            )
            if (isSubmenuShowing) {
                Menu("Settings") {
                    Item("Setting 1", onClick = { })
                    Item("Setting 2", onClick = { })
                }
            }
            Separator()
            Item("About", onClick = { })
        }*/
    }
}