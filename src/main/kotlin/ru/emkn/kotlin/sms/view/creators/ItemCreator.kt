package ru.emkn.kotlin.sms.view.creators

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.maxTextLength
import ru.emkn.kotlin.sms.view.ActionButton
import ru.emkn.kotlin.sms.view.BottomAppBar
import ru.emkn.kotlin.sms.view.GUI
import ru.emkn.kotlin.sms.view.draw

data class ItemCreatorInputField(
    val title: String,
    val field: ObjectFields,
    var data: MutableState<String> = mutableStateOf("")
)

@Composable
fun draw(inputFieldCreator: ItemCreatorInputField, width: Dp) {

    TextField(inputFieldCreator.data.value,
        modifier = Modifier
            .border(BorderStroke(1.dp, Color.Black))
            .width(width),
        onValueChange = {
            inputFieldCreator.data.value = it.replace("\n", "").take(maxTextLength)
        },
        label = { Text(inputFieldCreator.title) }
    )
}

abstract class ItemCreator<T> {
    abstract val fields: List<ItemCreatorInputField>

    abstract fun createAction(input: Map<ObjectFields, String>)

    fun create(gui: GUI, bottomAppBar: BottomAppBar) {
        try {
            createAction(input)
            gui.popState()
        } catch (e: Exception) {
            bottomAppBar.setMessage(e.message ?: "Undefined error")
        }
    }

    fun cancel(gui: GUI) {
        gui.popState()
    }

    open val input: Map<ObjectFields, String>
        get() = fields.associate { it.field to it.data.value }
}


@Composable
fun <T> draw(gui: GUI, bottomAppBar: BottomAppBar, creator: ItemCreator<T>) {
    var columnSize by remember { mutableStateOf(IntSize.Zero) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .onSizeChanged {
            columnSize = it
        }
    ) {
        for (field in creator.fields) {
            draw(field, columnSize.width.dp)
        }
        draw(ActionButton("Create") { creator.create(gui, bottomAppBar) })
        draw(ActionButton("Cancel") { creator.cancel(gui) })
    }
}
