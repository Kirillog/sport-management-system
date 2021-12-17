package ru.emkn.kotlin.sms.view

import java.io.File
import javax.swing.*

class PathChooser(private val title: String) {
    fun choose(): File? {
        val fileChooser = JFileChooser()
        fileChooser.dialogType = JFileChooser.OPEN_DIALOG
        fileChooser.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
        fileChooser.dialogTitle = title
        fileChooser.showOpenDialog(null)
        return fileChooser.selectedFile
    }

}
