package ru.emkn.kotlin.sms.view

import androidx.compose.runtime.Composable
import java.io.File

data class LoadOrCreate(
    val question: String,
    val loadTitle: String,
    val createTitle: String,
    val loadAction: (File?) -> Unit,
    val createAction: (File?) -> Unit,
    val fileExtension: String,
    val fileExtensionDescription: String,
)

@Composable
fun draw(loader: LoadOrCreate) {
    draw(ButtonsChooser(
        question = loader.question,
        listOf(
            ActionButton("Load") {
                chooseFileAndProcess(
                    loader.loadTitle,
                    loader.fileExtension,
                    loader.fileExtensionDescription,
                    loader.loadAction
                )
            },
            ActionButton("Create") {
                chooseFileAndProcess(
                    loader.createTitle,
                    loader.fileExtension,
                    loader.fileExtensionDescription,
                    loader.createAction
                )
            }
        )
    ))
}