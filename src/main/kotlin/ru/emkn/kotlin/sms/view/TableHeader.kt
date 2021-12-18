package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.ObjectFields


data class TableColumn<T>(
    val title: String,
    val field: ObjectFields,
    val visible: Boolean,
    val readOnly: Boolean,
    val getterGenerator: (T) -> (() -> String)
)

data class TableHeader<T>(val columns: List<TableColumn<T>>) {

    @Composable
    fun draw() {
        var rowSize by remember { mutableStateOf(IntSize.Zero) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged {
                    rowSize = it
                },
            horizontalArrangement = Arrangement.SpaceAround,
        ) {

            val columnsCount = columns.count { it.visible }
            for (column in columns) {
                if (!column.visible)
                    continue
                Text(
                    column.title,
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color.Black))
                        .width((rowSize.width / columnsCount).dp)
                )
            }
        }
    }

    fun makeTableCells(item: T, saveFunction: () -> Unit): Map<ObjectFields, TableCell> {
        return columns.associate { it.field to TableCell(it.getterGenerator(item), saveFunction) }
    }

}