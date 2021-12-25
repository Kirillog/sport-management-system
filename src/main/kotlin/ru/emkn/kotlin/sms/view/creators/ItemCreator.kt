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
import ru.emkn.kotlin.sms.view.GUI
import ru.emkn.kotlin.sms.view.TopAppBar

data class ItemCreatorInputField(
    val title: String,
    val field: ObjectFields,
    var data: MutableState<String> = mutableStateOf("")
) {
    @Composable
    fun draw(width: Dp) {

        TextField(data.value,
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.Black))
                .width(width),
            onValueChange = {
                data.value = it.replace("\n", "").take(maxTextLength)
            },
            label = { Text(title) }
        )
    }
}

abstract class ItemCreator<T> {
    abstract val fields: List<ItemCreatorInputField>

    private fun create(gui: GUI) {
        try {
            createAction(input)
            gui.popState()
        } catch (e: Exception) {
            TopAppBar.setMessage(e.message ?: "Undefined error")
        }
    }

    private fun cancel(gui: GUI) {
        gui.popState()
    }

    abstract fun createAction(input: Map<ObjectFields, String>)

    open val input: Map<ObjectFields, String>
        get() = fields.associate { it.field to it.data.value }

    @Composable
    open fun draw(gui: GUI) {
        var columnSize by remember { mutableStateOf(IntSize.Zero) }

        Column(modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                columnSize = it
            }
        ) {
            for (field in fields) {
                field.draw(columnSize.width.dp)
            }
            ActionButton("Create", ::create).draw(gui)
            ActionButton("Cancel", ::cancel).draw(gui)
        }
    }
}