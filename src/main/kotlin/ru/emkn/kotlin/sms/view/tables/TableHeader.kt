package ru.emkn.kotlin.sms.view.tables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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

class TableHeader<T>(private val columns: List<TableColumn<T>>, val iconsBar: Boolean, val filtering: Boolean = true) {

    var iconsBarSize = mutableStateOf(IntSize(0, 0))

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
            columns.filter { it.visible }.associate { it.field to TableCell(it.getterGenerator(item)(), saveFunction) }
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
fun <T> TableHeader(tableHeader: TableHeader<T>, lazyListState: LazyListState, coroutineScope: CoroutineScope) {
    var rowSize by remember { mutableStateOf(IntSize.Zero) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                rowSize = it
            },
        horizontalArrangement = Arrangement.Start
    ) {
        val columnsCount = tableHeader.visibleColumns.size
        val columnWidth = ((rowSize.width - tableHeader.iconsBarSize.value.width) / columnsCount).dp

        tableHeader.visibleColumns.forEachIndexed { index, column ->
            ColumnTitleWithFilter(tableHeader, column, columnWidth, index)
        }

        if (tableHeader.iconsBar)
            ScrollButtons(tableHeader, lazyListState, coroutineScope)
    }
}

@Composable
fun<T> ColumnTitleWithFilter(tableHeader: TableHeader<T>, column : TableColumn<T>, columnWidth : Dp, columnIndex : Int) {
    Column(
        modifier = Modifier
            .width(columnWidth)
    ) {
        // title
        TextButton(
            onClick = {
                if (tableHeader.orderByColumn.value == columnIndex)
                    tableHeader.reversedOrder.value = !tableHeader.reversedOrder.value
                tableHeader.orderByColumn.value = columnIndex
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, Color.Black))
                .background(Color.LightGray)
        ) {
            Text(column.title)
        }
        // filter field
        if (tableHeader.filtering) {
            OutlinedTextField(
                value = column.filterString.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color.Black)),
                onValueChange = {
                    column.filterString.value = it.replace("\n", "").take(MAX_TEXT_FIELD_SIZE)
                },
                placeholder = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                },
            )
        }
    }
}

@Composable
fun <T> ScrollButtons(tableHeader : TableHeader<T>, lazyListState : LazyListState, coroutineScope: CoroutineScope) {
    Column(modifier = Modifier.width(tableHeader.iconsBarSize.value.width.dp)) {
        IconButton(onClick = {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(
                    lazyListState.firstVisibleItemIndex - 1,
                    0
                )
            }
        }, enabled = lazyListState.firstVisibleItemIndex > 0) {
            Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = "Up", tint = Color.Black)
        }
        IconButton(
            onClick = {
                coroutineScope.launch {
                    lazyListState.animateScrollToItem(
                        lazyListState.firstVisibleItemIndex + 1,
                        0
                    )
                }
            },
            enabled = lazyListState.firstVisibleItemIndex < lazyListState.layoutInfo.totalItemsCount - lazyListState.layoutInfo.visibleItemsInfo.size
        ) {
            Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Down", tint = Color.Black)
        }
    }
}