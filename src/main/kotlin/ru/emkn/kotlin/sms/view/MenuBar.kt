package ru.emkn.kotlin.sms.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import ru.emkn.kotlin.sms.controller.CompetitionController

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun drawMenuBar(gui: GUI, frame: FrameWindowScope, bottomBar: BottomAppBar) {
    val stages = listOf("Toss", "Results", "Finished")
    val stageIndex = mutableStateOf(0)
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
            val text = stages[stageIndex.value]
            Item(text, onClick = {
                when (text) {
                    "Toss" ->
                        StateSwitcher.doToss(gui, bottomBar)
                    "Result" -> TODO()
//                        StateSwitcher.doResult(gui, bottomBar)
                }
                stageIndex.value++
            }, shortcut = KeyShortcut(Key.T, ctrl = true), enabled = stageIndex.value < 2)
            Item("Rollback", onClick = {
                when (text) {
                    "Result" ->
                        StateSwitcher.undoToss(gui, bottomBar)
                    "Finished" -> TODO()
//                        StateSwitcher.undoResult(gui, bottomBar)
                }
                stageIndex.value--
            }, enabled = stageIndex.value > 0)
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