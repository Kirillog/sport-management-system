package ru.emkn.kotlin.sms.view.tables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
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
import ru.emkn.kotlin.sms.MAX_TEXT_FIELD_SIZE
import ru.emkn.kotlin.sms.ObjectFields


data class TableColumn<T>(
    val title: String,
    val field: ObjectFields,
    var visible: Boolean,
    var readOnly: Boolean,
    val comparator: Comparator<Table<T>.TableRow>,
    val getterGenerator: (T) -> (() -> String)
) {
    var filterString = mutableStateOf("")
}

private val logger = KotlinLogging.logger {}

class TableHeader<T>(val columns: List<TableColumn<T>>, val deleteButton: Boolean, val filtering: Boolean = true) {

    var orderByColumn = mutableStateOf(run {
        val index = columns.indexOfFirst { it.visible }
        if (index == -1)
            throw IllegalStateException("There is no visible columns")
        index
    })
    var reversedOrder = mutableStateOf(false)

    val visibleColumns
        get() = columns.filter { it.visible }

    fun makeTableCells(item: T, saveFunction: () -> Unit): Map<ObjectFields, TableCell> {
        return transaction {
            columns.filter { it.visible }.associate { it.field to TableCell(it.getterGenerator(item), saveFunction) }
        }
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
//        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // space for delete button
        if (tableHeader.deleteButton)
            Box(modifier = Modifier.width(tableDeleteButtonWidth.dp))
        // header and filters
        Column {
            val columnsCount = tableHeader.visibleColumns.size
            val columnWidth = (rowSize.width / columnsCount).dp
            // filter fields
            if (tableHeader.filtering) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    tableHeader.visibleColumns.forEach { column ->
                        BasicTextField(
                            value = column.filterString.value,
                            modifier = Modifier
                                .border(BorderStroke(1.dp, Color.Black))
                                .width(columnWidth),
                            onValueChange = {
                                column.filterString.value = it.replace("\n", "").take(MAX_TEXT_FIELD_SIZE)
                            })
                    }
                }
            }
            // header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged {
                        rowSize = it
                    },
                horizontalArrangement = Arrangement.Start
            ) {
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
    }


}