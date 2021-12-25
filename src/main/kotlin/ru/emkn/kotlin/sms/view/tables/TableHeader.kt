package ru.emkn.kotlin.sms.view.tables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.view.GUI


data class TableColumn<T>(
    val title: String,
    val field: ObjectFields,
    val visible: Boolean,
    val readOnly: Boolean,
    val comparator: Comparator<Table<T>.TableRow>,
    val getterGenerator: (T) -> (() -> String)
)

private val logger = KotlinLogging.logger {}

class TableHeader<T>(val columns: List<TableColumn<T>>, val deleteButton: Boolean) {

    var orderByColumn = 0
    var reversedOrder = false

    @Composable
    fun draw(gui: GUI) {
        var rowSize by remember { mutableStateOf(IntSize.Zero) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged {
                    rowSize = it
                },
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val columnsCount = columns.count { it.visible }

            val columnWidth = if (deleteButton)
                ((rowSize.width - tableDeleteButtonWidth) / columnsCount).dp
            else
                (rowSize.width / columnsCount).dp
            if (deleteButton)
                Box(modifier = Modifier.width(tableDeleteButtonWidth.dp))

            columns.forEachIndexed { index, column ->
                if (!column.visible)
                    return@forEachIndexed
                TextButton(
                    onClick = {
                        if (orderByColumn == index)
                            reversedOrder = !reversedOrder
                        orderByColumn = index
                        gui.reload()
                    },
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color.Black))
                        .width(columnWidth)
                        .background(Color.LightGray)
                ) {
                    Text(column.title)
                }
            }
        }
    }

    fun makeTableCells(item: T, saveFunction: () -> Unit): Map<ObjectFields, TableCell> {
        return transaction { columns.associate { it.field to TableCell(it.getterGenerator(item), saveFunction) } }
    }

    val comparator: Comparator<Table<T>.TableRow>
        get() = if (reversedOrder)
            columns[orderByColumn].comparator.reversed()
        else
            columns[orderByColumn].comparator
}

fun <T> TableHeader(tableHeader: TableHeader<T>) {

}