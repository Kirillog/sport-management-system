package ru.emkn.kotlin.sms.view

import java.io.File
import javax.swing.*
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class CompetitionDirectoryChooser {
    fun choose(): File? {
        val fileChooser = JFileChooser()
        fileChooser.dialogType = JFileChooser.OPEN_DIALOG
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        fileChooser.showOpenDialog(null)
        return fileChooser.selectedFile
    }

    enum class Errors {
        OK,
        NoChosenDirectory,
        PathIsNotADirectory,
        NoApplications,
        NoCheckpoints
    }

    fun checkDirectory(file: File?): Errors {
        return when {
            file == null -> Errors.NoChosenDirectory
            !file.isDirectory -> Errors.PathIsNotADirectory
            !file.toPath().resolve("applications").isDirectory() -> Errors.NoApplications
            !file.toPath().resolve("checkpoints").isDirectory() -> Errors.NoCheckpoints
            else -> Errors.OK
        }

    }
}


