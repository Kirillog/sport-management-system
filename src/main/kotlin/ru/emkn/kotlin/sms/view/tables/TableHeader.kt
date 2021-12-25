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


data class TableColumn<T>(
    val title: String,
    val field: ObjectFields,
    var visible: Boolean,
    var readOnly: Boolean,
    val comparator: Comparator<Table<T>.TableRow>,
    val getterGenerator: (T) -> (() -> String)
)

private val logger = KotlinLogging.logger {}

class TableHeader<T>(val columns: List<TableColumn<T>>, val deleteButton: Boolean) {

    var orderByColumn = mutableStateOf(0)
    var reversedOrder = mutableStateOf(false)

    fun makeTableCells(item: T, saveFunction: () -> Unit): Map<ObjectFields, TableCell> {
        return transaction { columns.associate { it.field to TableCell(it.getterGenerator(item), saveFunction) } }
    }

    fun setVisibility(columnType: ObjectFields, visible: Boolean) {
        val column = columns.firstOrNull { it.field == columnType }
            ?: throw IllegalStateException("column $columnType doesn't exists")
        column.visible = visible
    }

    fun setVisibility(visible: Boolean) {
        for (column in columns)
            column.visible = visible
    }

    fun setReadOnly(columnType: ObjectFields, readOnly: Boolean) {
        val column = columns.firstOrNull { it.field == columnType }
            ?: throw IllegalStateException("column $columnType doesn't exists")
        column.readOnly = readOnly
    }

    fun setReadOnly(readOnly: Boolean) {
        for (column in columns)
            column.readOnly = readOnly
    }

    val comparator: Comparator<Table<T>.TableRow>
        get() = if (reversedOrder.value)
            columns[orderByColumn.value].comparator.reversed()
        else
            columns[orderByColumn.value].comparator
}

@Composable
fun <T> draw(tableHeader: TableHeader<T>) {
    var rowSize by remember { mutableStateOf(IntSize.Zero) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                rowSize = it
            },
        horizontalArrangement = Arrangement.Start
    ) {
        val columnsCount = tableHeader.columns.count { it.visible }

        val columnWidth = if (tableHeader.deleteButton)
            ((rowSize.width - tableDeleteButtonWidth) / columnsCount).dp
        else
            (rowSize.width / columnsCount).dp
        if (tableHeader.deleteButton)
            Box(modifier = Modifier.width(tableDeleteButtonWidth.dp))

        tableHeader.columns.forEachIndexed { index, column ->
            if (!column.visible)
                return@forEachIndexed
            TextButton(
                onClick = {
                    if (tableHeader.orderByColumn.value == index)
                        tableHeader.reversedOrder.value = !tableHeader.reversedOrder.value
                    tableHeader.orderByColumn.value = index
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