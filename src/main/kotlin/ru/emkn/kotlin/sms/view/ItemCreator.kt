package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.maxTextLength

data class ItemCreatorField(val title: String, val name: String, var data: MutableState<String> = mutableStateOf("")) {
    @Composable
    fun draw(width: Dp) {

        TextField(data.value,
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.Black))
                .width(width),
            onValueChange = {
                data.value = it.replace("\n", "").take(maxTextLength)
            },
            label = { Text(title) })
    }
}

abstract class ItemCreator<T> {
    abstract val fields: List<ItemCreatorField>
    abstract fun create()

    open val changes: Map<String, String>
        get() = fields.associate { it.name to it.data.value }

    @Composable
    open fun draw() {
        var columnSize by remember { mutableStateOf(IntSize.Zero) }

        Column(modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                columnSize = it
            }
            .verticalScroll(rememberScrollState())
        ) {
            for (field in fields) {
                field.draw(columnSize.width.dp)
            }

            Button(onClick = {
                create()
            }) {
                Text("Создать")
            }
        }
    }
}