package ru.emkn.kotlin.sms.view

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

class PathChooser(private val title: String, private val extension: String, private val description: String) {
    fun choose(mode: Int = JFileChooser.FILES_ONLY): File? {
        val fileChooser = JFileChooser(File("."))
        fileChooser.isAcceptAllFileFilterUsed = false
        fileChooser.dialogType = JFileChooser.OPEN_DIALOG
        fileChooser.fileSelectionMode = mode
        fileChooser.dialogTitle = title

        val fileFilter = FileFilter(extension, "$description (*$extension)")
        fileChooser.addChoosableFileFilter(fileFilter)

        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            return if (fileFilter.accept(fileChooser.selectedFile))
                fileChooser.selectedFile
            else
                File("${fileChooser.selectedFile.absolutePath}${fileFilter.extension}")
        }
        return null
    }

}

fun chooseFileAndProcess(
    chooserTitle: String,
    chooserFileExtension: String,
    chooserFileDescription: String,
    action: (File?) -> Unit
) {
    val file = PathChooser(chooserTitle, chooserFileExtension, chooserFileDescription).choose()
    try {
        action(file)
    } catch (e: Exception) {
        BottomAppBar += e.message ?: "Undefined error"
    }
}

class FileFilter(var extension: String, private var description: String) : FileFilter() {

    override fun accept(file: File?): Boolean {
        if (file != null) {
            return if (file.isDirectory)
                true
            else
                file.name.endsWith(extension)
        }
        return false
    }

    override fun getDescription(): String {
        return description
    }
}
