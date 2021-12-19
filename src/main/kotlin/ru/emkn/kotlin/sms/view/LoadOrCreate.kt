package ru.emkn.kotlin.sms.view

import androidx.compose.runtime.Composable
import java.io.File

class LoadOrCreate(
    private val question: String,
    private val loadTitle: String,
    private val createTitle: String,
    private val loadAction: (File?) -> Unit,
    private val createAction: (File?) -> Unit,
    private val fileExtension: String,
    private val fileExtensionDescription: String,
) {
    @Composable
    fun draw() {
        ButtonsChooser(
            question = question,
            listOf(
                ActionButton("Load") {
                    GUI.chooseFileAndProcess(loadTitle, fileExtension, fileExtensionDescription, loadAction)
                },
                ActionButton("Create") {
                    GUI.chooseFileAndProcess(createTitle, fileExtension, fileExtensionDescription, createAction)
                }
            )
        ).draw()
    }
}